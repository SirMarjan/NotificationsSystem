package pl.marcinsobanski.notificationssystem.application.createtemplate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.RuleOperator;
import pl.marcinsobanski.notificationssystem.api.cqrs.createtemplate.CreateTemplateCommand;
import pl.marcinsobanski.notificationssystem.application.converters.RuleConverter;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRule;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleFactory;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleRepository;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.domain.template.Template;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;
import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTemplateCommandHandlerTest {

    private static final String TITLE = "Test Title";
    private static final String CONTENT = "Test Content";
    private static final String EMAIL = "test@example.com";
    private static final String EMAIL_2 = "test2@example.com";
    private static final String EMAIL_3 = "test3@example.com";
    private static final String PRICE = "100.00";

    private static final Rule API_ITEM_RULE = new Rule(RuleOperator.ITEM_IS, "GOLD");
    private static final Rule API_PRICE_GREATER_RULE = new Rule(RuleOperator.PRICE_GREATER, PRICE);
    private static final Rule API_PRICE_LESS_RULE = new Rule(RuleOperator.PRICE_LESS, PRICE);

    private static final ItemTypeRule DOMAIN_ITEM_RULE = new ItemTypeRule(ItemTypeRule.Operator.IS, ItemType.GOLD);
    private static final PriceRule DOMAIN_PRICE_GREATER_RULE = new PriceRule(PriceRule.Operator.GREATER, new BigDecimal(PRICE));
    private static final PriceRule DOMAIN_PRICE_LESS_RULE = new PriceRule(PriceRule.Operator.LESS, new BigDecimal(PRICE));

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private RuleConverter ruleConverter;

    @Mock
    private CompiledRuleFactory compiledRuleFactory;

    @Mock
    private CompiledRuleRepository compiledRuleRepository;

    @Captor
    private ArgumentCaptor<Template> templateCaptor;

    @InjectMocks
    private CreateTemplateCommandHandler handler;


    private void mockRepositorySave(UUID templateId) {
        when(templateRepository.saveTemplate(any())).thenReturn(templateId);
        when(compiledRuleFactory.calculateCompiledRule(any())).thenReturn(Optional.empty());
    }

    private void mockRepositorySaveWithCompiledRule(UUID templateId, CompiledRule compiledRule) {
        when(templateRepository.saveTemplate(any())).thenReturn(templateId);
        when(compiledRuleFactory.calculateCompiledRule(any())).thenReturn(Optional.of(compiledRule));
    }

    @Test
    void shouldCreateTemplateWithoutRules() {
        // given
        final var command = new CreateTemplateCommand(TITLE, CONTENT, List.of(EMAIL), Collections.emptyList());
        final var expectedTemplateId = UUID.randomUUID();
        mockRepositorySave(expectedTemplateId);

        // when
        final var result = handler.handle(command);

        // then
        assertEquals(expectedTemplateId, result.templateId());

        verify(templateRepository).saveTemplate(templateCaptor.capture());

        final var savedTemplate = templateCaptor.getValue();
        assertEquals(TITLE, savedTemplate.getTitle());
        assertEquals(CONTENT, savedTemplate.getContent());
        assertEquals(List.of(EMAIL), savedTemplate.getReceiversEmails());
        assertTrue(savedTemplate.getRules().isEmpty());
        assertNotNull(savedTemplate.getCreationTime());

        verify(compiledRuleRepository, never()).saveCompiledRule(any(), any());
    }

    @Test
    void shouldCreateTemplateWithItemTypeRule() {
        // given
        final var command = new CreateTemplateCommand(TITLE, CONTENT, List.of(EMAIL), List.of(API_ITEM_RULE));
        when(ruleConverter.convertRule(API_ITEM_RULE)).thenReturn(DOMAIN_ITEM_RULE);

        final var expectedTemplateId = UUID.randomUUID();
        mockRepositorySave(expectedTemplateId);

        // when
        final var result = handler.handle(command);

        // then
        assertEquals(expectedTemplateId, result.templateId());

        verify(templateRepository).saveTemplate(templateCaptor.capture());

        final var savedTemplate = templateCaptor.getValue();
        assertEquals(TITLE, savedTemplate.getTitle());
        assertEquals(CONTENT, savedTemplate.getContent());
        assertEquals(List.of(EMAIL), savedTemplate.getReceiversEmails());
        assertEquals(1, savedTemplate.getRules().size());
        assertEquals(DOMAIN_ITEM_RULE, savedTemplate.getRules().getFirst());

        verify(ruleConverter).convertRule(API_ITEM_RULE);
        verify(compiledRuleRepository, never()).saveCompiledRule(any(), any());
    }

    @Test
    void shouldCreateTemplateWithPriceRule() {
        // given
        final var command = new CreateTemplateCommand(TITLE, CONTENT, List.of(EMAIL), List.of(API_PRICE_GREATER_RULE));
        when(ruleConverter.convertRule(API_PRICE_GREATER_RULE)).thenReturn(DOMAIN_PRICE_GREATER_RULE);

        final var expectedTemplateId = UUID.randomUUID();
        mockRepositorySave(expectedTemplateId);

        // when
        final var result = handler.handle(command);

        // then
        assertEquals(expectedTemplateId, result.templateId());

        verify(templateRepository).saveTemplate(templateCaptor.capture());

        final var savedTemplate = templateCaptor.getValue();
        assertEquals(1, savedTemplate.getRules().size());
        assertEquals(DOMAIN_PRICE_GREATER_RULE, savedTemplate.getRules().getFirst());

        verify(ruleConverter).convertRule(API_PRICE_GREATER_RULE);
    }

    @Test
    void shouldCreateTemplateWithMultipleRules() {
        // given
        final var command = new CreateTemplateCommand(
                TITLE,
                CONTENT,
                List.of(EMAIL, EMAIL_2),
                List.of(API_ITEM_RULE, API_PRICE_GREATER_RULE, API_PRICE_LESS_RULE)
        );

        when(ruleConverter.convertRule(API_ITEM_RULE)).thenReturn(DOMAIN_ITEM_RULE);
        when(ruleConverter.convertRule(API_PRICE_GREATER_RULE)).thenReturn(DOMAIN_PRICE_GREATER_RULE);
        when(ruleConverter.convertRule(API_PRICE_LESS_RULE)).thenReturn(DOMAIN_PRICE_LESS_RULE);

        final var expectedTemplateId = UUID.randomUUID();
        mockRepositorySave(expectedTemplateId);

        // when
        final var result = handler.handle(command);

        // then
        assertEquals(expectedTemplateId, result.templateId());

        verify(templateRepository).saveTemplate(templateCaptor.capture());

        final var savedTemplate = templateCaptor.getValue();
        assertEquals(3, savedTemplate.getRules().size());
        assertEquals(List.of(EMAIL, EMAIL_2), savedTemplate.getReceiversEmails());

        verify(ruleConverter).convertRule(API_ITEM_RULE);
        verify(ruleConverter).convertRule(API_PRICE_GREATER_RULE);
        verify(ruleConverter).convertRule(API_PRICE_LESS_RULE);
    }

    @Test
    void shouldSaveCompiledRuleWhenFactoryReturnsCompiledRule() {
        // given
        final var command = new CreateTemplateCommand(TITLE, CONTENT, List.of(EMAIL), List.of(API_ITEM_RULE));
        when(ruleConverter.convertRule(API_ITEM_RULE)).thenReturn(DOMAIN_ITEM_RULE);

        final var expectedTemplateId = UUID.randomUUID();
        final var compiledRule = CompiledRule.builder()
                .itemType(ItemType.GOLD)
                .itemTypeEqual(true)
                .build();
        mockRepositorySaveWithCompiledRule(expectedTemplateId, compiledRule);

        // when
        final var result = handler.handle(command);

        // then
        assertEquals(expectedTemplateId, result.templateId());

        verify(compiledRuleRepository).saveCompiledRule(eq(compiledRule), eq(expectedTemplateId));
    }

    @Test
    void shouldNotSaveCompiledRuleWhenFactoryReturnsEmpty() {
        // given
        final var command = new CreateTemplateCommand(TITLE, CONTENT, List.of(EMAIL), Collections.emptyList());
        final var expectedTemplateId = UUID.randomUUID();
        mockRepositorySave(expectedTemplateId);

        // when
        final var result = handler.handle(command);

        // then
        assertNotNull(result);
        verify(compiledRuleRepository, never()).saveCompiledRule(any(), any());
    }

    @Test
    void shouldCreateTemplateWithMultipleReceiversEmails() {
        // given
        final var command = new CreateTemplateCommand(TITLE, CONTENT, List.of(EMAIL, EMAIL_2, EMAIL_3), Collections.emptyList());
        final var expectedTemplateId = UUID.randomUUID();
        mockRepositorySave(expectedTemplateId);

        // when
        handler.handle(command);

        // then

        verify(templateRepository).saveTemplate(templateCaptor.capture());

        final var savedTemplate = templateCaptor.getValue();
        assertEquals(3, savedTemplate.getReceiversEmails().size());
        assertTrue(savedTemplate.getReceiversEmails().contains(EMAIL));
        assertTrue(savedTemplate.getReceiversEmails().contains(EMAIL_2));
        assertTrue(savedTemplate.getReceiversEmails().contains(EMAIL_3));
    }
}