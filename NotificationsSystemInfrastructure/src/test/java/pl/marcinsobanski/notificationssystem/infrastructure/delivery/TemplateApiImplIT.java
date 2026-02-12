package pl.marcinsobanski.notificationssystem.infrastructure.delivery;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.marcinsobanski.notificationssystem.api.model.AddNewTemplateResponse;
import pl.marcinsobanski.notificationssystem.api.model.Rule;
import pl.marcinsobanski.notificationssystem.api.model.TemplateDetails;
import pl.marcinsobanski.notificationssystem.api.model.TemplateListElement;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.marcinsobanski.notificationssystem.api.endpoint.TemplateApi.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TemplateApiImplIT {

    @Autowired
    private TemplateJpaRepository templateJpaRepository;

    @Autowired
    private RuleJpaRepository ruleJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldReturnEmptyListOfTemplates() throws Exception {
        // GIVEN

        // WHEN
        final var json = mvc.perform(get(PATH_GET_TEMPLATES_LIST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        final var elements = objectMapper.<List<TemplateListElement>>readValue(json, new TypeReference<>() {
        });

        // THEN
        assertEquals(0, elements.size());
    }

    @Test
    void shouldReturnListOfTemplatesInOrder() throws Exception {
        // GIVEN
        final var template1 = TemplateEntity.builder()
                .title("Title")
                .content("Content")
                .creationTime(Instant.parse("2007-12-03T10:15:30.00Z"))
                .build();
        final var template2 = TemplateEntity.builder()
                .title("Title2")
                .content("Content2")
                .creationTime(Instant.parse("2008-12-03T10:15:30.00Z"))
                .build();
        templateJpaRepository.saveAll(
                List.of(template1, template2)
        );

        // WHEN
        final var json = mvc.perform(get(PATH_GET_TEMPLATES_LIST))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        final var elements = objectMapper.<List<TemplateListElement>>readValue(json, new TypeReference<>() {
        });

        // THEN
        assertEquals(2, elements.size());
        assertEquals(template2.getId(), elements.getFirst().getId());
        assertEquals(template2.getTitle(), elements.getFirst().getTitle());
        assertEquals(template1.getId(), elements.get(1).getId());
        assertEquals(template1.getTitle(), elements.get(1).getTitle());
    }

    @Test
    void shouldGetComplexTemplateDetails() throws Exception {
        // GIVEN
        final var template = TemplateEntity.builder()
                .title("Title")
                .content("Content")
                .creationTime(Instant.now())
                .receiversEmails(List.of("a@a.pl", "b@b.pl"))
                .rules(List.of(
                        RuleEntity.builder().ruleOperator(RuleOperator.IS).itemTypeOperand(ItemType.GOLD).build(),
                        RuleEntity.builder().ruleOperator(RuleOperator.EQUAL).priceOperand(BigDecimal.valueOf(10.10)).build()
                ))
                .build();
        final var id = templateJpaRepository.save(template).getId();

        // WHEN
        final var json = mvc.perform(get(PATH_GET_TEMPLATE_DETAILS, id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        final var templateDetails = objectMapper.readValue(json, TemplateDetails.class);

        // THEN
        assertEquals(template.getTitle(), templateDetails.getTitle());
        assertEquals(template.getContent(), templateDetails.getContent());
        assertEquals(template.getReceiversEmails(), templateDetails.getReceiversEmails());

        final var rule0 = templateDetails.getRules().getFirst();
        assertEquals(Rule.OperatorEnum.ITEM_IS, rule0.getOperator());
        assertEquals(ItemType.GOLD.name(), rule0.getOperand());

        final var rule1 = templateDetails.getRules().get(1);
        assertEquals(Rule.OperatorEnum.PRICE_EQUALS, rule1.getOperator());
        assertEquals("10.1", rule1.getOperand());
    }

    @Test
    void shouldGetTemplateDetailsWOEmailsAndRules() throws Exception {
        // GIVEN
        final var template = TemplateEntity.builder()
                .title("Title")
                .content("Content")
                .creationTime(Instant.now())
                .build();
        final var id = templateJpaRepository.save(template).getId();

        // WHEN
        final var json = mvc.perform(get(PATH_GET_TEMPLATE_DETAILS, id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        final var templateDetails = objectMapper.readValue(json, TemplateDetails.class);

        // THEN
        assertEquals(template.getTitle(), templateDetails.getTitle());
        assertEquals(template.getContent(), templateDetails.getContent());
        assertTrue(templateDetails.getReceiversEmails().isEmpty());
        assertTrue(templateDetails.getRules().isEmpty());

        final var savedTemplate = templateJpaRepository.findById(id).orElseThrow();
        assertNull(savedTemplate.getCompiledRuleEntity());
    }

    @Test
    void shouldAddNewTemplate() throws Exception {
        // GIVEN
        final var existingRule = RuleEntity.builder()
                .ruleOperator(RuleOperator.IS)
                .itemTypeOperand(ItemType.GOLD)
                .build();
        ruleJpaRepository.save(existingRule);

        final var templateDetails = new TemplateDetails(
                "Title",
                List.of("a@a.com"),
                "content",
                List.of(
                        new Rule(Rule.OperatorEnum.ITEM_IS, ItemType.GOLD.name()),
                        new Rule(Rule.OperatorEnum.PRICE_EQUALS, "25.50")
                )
        );

        // WHEN
        final var json = mvc.perform(post(PATH_ADD_NEW_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(templateDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        final var response = objectMapper.readValue(json, AddNewTemplateResponse.class);
        final var templateId = response.getId();

        // THEN
        final var savedTemplate = templateJpaRepository.findById(templateId).orElseThrow();

        assertEquals("Title", savedTemplate.getTitle());
        assertEquals("content", savedTemplate.getContent());
        assertEquals(List.of("a@a.com"), savedTemplate.getReceiversEmails());
        assertEquals(2, savedTemplate.getRules().size());

        final var rules = ruleJpaRepository.findAll();
        assertEquals(2, rules.size());

        assertTrue(rules.stream().anyMatch(rule ->
                rule.getRuleOperator() == RuleOperator.IS &&
                        rule.getItemTypeOperand() == ItemType.GOLD));

        assertTrue(rules.stream().anyMatch(rule ->
                rule.getRuleOperator() == RuleOperator.EQUAL &&
                        rule.getPriceOperand().compareTo(new BigDecimal("25.50")) == 0));

        final var compiledRuleEntity = savedTemplate.getCompiledRuleEntity();
        assertEquals(ItemType.GOLD, compiledRuleEntity.getItemType());
        assertTrue(compiledRuleEntity.isItemTypeEqual());
        assertEquals(new BigDecimal("25.50"), compiledRuleEntity.getCeilLimit());
        assertTrue(compiledRuleEntity.isCeilLimitIncluding());
        assertEquals(new BigDecimal("25.50"), compiledRuleEntity.getFloorLimit());
        assertTrue(compiledRuleEntity.isFloorLimitIncluding());
    }

    @Test
    void shouldReplaceTemplate() throws Exception {
        // GIVEN
        final var templateEntity = TemplateEntity.builder()
                .title("Title")
                .content("Content")
                .receiversEmails(List.of("a1@a.pl", "a2@a.pl"))
                .rules(new ArrayList<>(List.of(
                        RuleEntity.builder()
                                .ruleOperator(RuleOperator.IS)
                                .itemTypeOperand(ItemType.GOLD)
                                .build(),
                        RuleEntity.builder()
                                .ruleOperator(RuleOperator.IS)
                                .itemTypeOperand(ItemType.PLATINUM)
                                .build()
                )))
                .creationTime(Instant.now())
                .build();

        final var savedTemplateId = templateJpaRepository.save(templateEntity).getId();


        final var templateDetails = new TemplateDetails(
                "Title2",
                List.of("a3@a.com"),
                "Content2",
                List.of(
                        new Rule(Rule.OperatorEnum.ITEM_IS, ItemType.GOLD.name()),
                        new Rule(Rule.OperatorEnum.PRICE_GREATER, "25.50"),
                        new Rule(Rule.OperatorEnum.PRICE_GREATER, "40.50")
                )
        );

        // WHEN
        mvc.perform(put(PATH_UPDATE_TEMPLATE, savedTemplateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(templateDetails)))
                .andExpect(status().isOk());

        // THEN
        final var savedTemplate = templateJpaRepository.findById(savedTemplateId).orElseThrow();

        assertEquals("Title2", savedTemplate.getTitle());
        assertEquals("Content2", savedTemplate.getContent());
        assertEquals(List.of("a3@a.com"), savedTemplate.getReceiversEmails());
        assertEquals(3, savedTemplate.getRules().size());

        final var rules = ruleJpaRepository.findAll();
        assertEquals(3, rules.size());

        assertTrue(rules.stream().anyMatch(rule ->
                rule.getRuleOperator() == RuleOperator.IS &&
                        rule.getItemTypeOperand() == ItemType.GOLD));

        assertTrue(rules.stream().anyMatch(rule ->
                rule.getRuleOperator() == RuleOperator.GREATER &&
                        rule.getPriceOperand().compareTo(new BigDecimal("40.50")) == 0));

        final var compiledRuleEntity = savedTemplate.getCompiledRuleEntity();
        assertEquals(ItemType.GOLD, compiledRuleEntity.getItemType());
        assertTrue(compiledRuleEntity.isItemTypeEqual());
        assertEquals(new BigDecimal("40.50"), compiledRuleEntity.getFloorLimit());
        assertFalse(compiledRuleEntity.isFloorLimitIncluding());
        assertNull(compiledRuleEntity.getCeilLimit());
    }


}