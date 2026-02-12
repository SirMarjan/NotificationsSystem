package pl.marcinsobanski.notificationssystem.infrastructure.delivery;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.RuleOperator;
import pl.marcinsobanski.notificationssystem.api.cqrs.createtemplate.CreateTemplateCommand;
import pl.marcinsobanski.notificationssystem.api.endpoint.PriceApi;
import pl.marcinsobanski.notificationssystem.api.model.ItemType;
import pl.marcinsobanski.notificationssystem.api.model.NewPrice;
import pl.marcinsobanski.notificationssystem.application.createtemplate.CreateTemplateCommandHandler;
import pl.marcinsobanski.notificationssystem.infrastructure.configuration.EmailSenderMock;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PriceApiImplIT {

    static final List<CreateTemplateCommand> TEMPLATES = List.of(
            new CreateTemplateCommand(
                    "Template IS",
                    "Content IS",
                    List.of("is1@test.pl", "is2@test.pl"),
                    List.of(new Rule(RuleOperator.ITEM_IS, "GOLD"))
            ),
            new CreateTemplateCommand(
                    "Template IS_NOT",
                    "Content IS_NOT",
                    List.of("isnot1@test.pl", "isnot2@test.pl"),
                    List.of(new Rule(RuleOperator.ITEM_IS_NOT, "SILVER"))
            ),
            new CreateTemplateCommand(
                    "Template EQUAL",
                    "Content EQUAL",
                    List.of("equal1@test.pl", "equal2@test.pl"),
                    List.of(new Rule(RuleOperator.PRICE_EQUALS, "100"))
            ),
            new CreateTemplateCommand(
                    "Template LESS",
                    "Content LESS",
                    List.of("less1@test.pl", "less2@test.pl"),
                    List.of(new Rule(RuleOperator.PRICE_LESS, "50"))
            ),
            new CreateTemplateCommand(
                    "Template LESS_OR_EQUAL",
                    "Content LESS_OR_EQUAL",
                    List.of("lesseq1@test.pl", "lesseq2@test.pl"),
                    List.of(new Rule(RuleOperator.PRICE_LESS_OR_EQUALS, "40"))
            ),
            new CreateTemplateCommand(
                    "Template GREATER",
                    "Content GREATER",
                    List.of("greater1@test.pl", "greater2@test.pl"),
                    List.of(new Rule(RuleOperator.PRICE_GREATER, "150"))
            ),
            new CreateTemplateCommand(
                    "Template GREATER_OR_EQUAL",
                    "Content GREATER_OR_EQUAL",
                    List.of("greatereq1@test.pl", "greatereq2@test.pl"),
                    List.of(new Rule(RuleOperator.PRICE_GREATER_OR_EQUALS, "160"))
            ),
            new CreateTemplateCommand(
                    "Template Complex",
                    "Content Complex",
                    List.of("complex1@test.pl", "complex2@test.pl"),
                    List.of(
                            new Rule(RuleOperator.ITEM_IS, "PLATINUM"),
                            new Rule(RuleOperator.PRICE_GREATER, "50"),
                            new Rule(RuleOperator.PRICE_LESS, "100")
                    )
            ));

    final static Map<String, EmailSenderMock.Email> EMAILS = Map.of(
            "Template IS", new EmailSenderMock.Email(
                    Set.of("is1@test.pl", "is2@test.pl"),
                    "Template IS",
                    "Content IS"
            ),
            "Template IS_NOT", new EmailSenderMock.Email(
                    Set.of("isnot1@test.pl", "isnot2@test.pl"),
                    "Template IS_NOT",
                    "Content IS_NOT"
            ),
            "Template EQUAL", new EmailSenderMock.Email(
                    Set.of("equal1@test.pl", "equal2@test.pl"),
                    "Template EQUAL",
                    "Content EQUAL"
            ),
            "Template LESS", new EmailSenderMock.Email(
                    Set.of("less1@test.pl", "less2@test.pl"),
                    "Template LESS",
                    "Content LESS"
            ),
            "Template LESS_OR_EQUAL", new EmailSenderMock.Email(
                    Set.of("lesseq1@test.pl", "lesseq2@test.pl"),
                    "Template LESS_OR_EQUAL",
                    "Content LESS_OR_EQUAL"
            ),
            "Template GREATER", new EmailSenderMock.Email(
                    Set.of("greater1@test.pl", "greater2@test.pl"),
                    "Template GREATER",
                    "Content GREATER"
            ),
            "Template GREATER_OR_EQUAL", new EmailSenderMock.Email(
                    Set.of("greatereq1@test.pl", "greatereq2@test.pl"),
                    "Template GREATER_OR_EQUAL",
                    "Content GREATER_OR_EQUAL"
            ),
            "Template Complex", new EmailSenderMock.Email(
                    Set.of("complex1@test.pl", "complex2@test.pl"),
                    "Template Complex",
                    "Content Complex"
            )
    );

    @Autowired
    CreateTemplateCommandHandler createTemplateCommandHandler;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    EmailSenderMock emailSenderMock;

    @BeforeEach
    void setUp() {
        emailSenderMock.clear();
        TEMPLATES.forEach(createTemplateCommandHandler::handle);
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void postNewPrice(
            String name,
            NewPrice newPrice,
            Set<EmailSenderMock.Email> expectedEmails
    ) throws Exception {
        mvc.perform(post(PriceApi.PATH_POST_NEW_PRICE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPrice)))
                .andExpect(status().isOk());

        assertEquals(expectedEmails, emailSenderMock.getEmailsStore());
    }

    static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        "GOLD 75",
                        new NewPrice()
                                .itemType(ItemType.GOLD)
                                .price("75.00"),
                        Set.of(
                                EMAILS.get("Template IS"),
                                EMAILS.get("Template IS_NOT")
                        )
                ),
                Arguments.of(
                        "SILVER 75",
                        new NewPrice()
                                .itemType(ItemType.SILVER)
                                .price("75.00"),
                        Set.of()
                ),
                Arguments.of(
                        "PLATINUM 100",
                        new NewPrice()
                                .itemType(ItemType.PLATINUM)
                                .price("100.00"),
                        Set.of(
                                EMAILS.get("Template IS_NOT"),
                                EMAILS.get("Template EQUAL")
                        )
                ),
                Arguments.of(
                        "PLATINUM 40",
                        new NewPrice()
                                .itemType(ItemType.PLATINUM)
                                .price("40.00"),
                        Set.of(
                                EMAILS.get("Template IS_NOT"),
                                EMAILS.get("Template LESS"),
                                EMAILS.get("Template LESS_OR_EQUAL")
                        )
                ),
                Arguments.of(
                        "PLATINUM 160",
                        new NewPrice()
                                .itemType(ItemType.PLATINUM)
                                .price("160.00"),
                        Set.of(
                                EMAILS.get("Template IS_NOT"),
                                EMAILS.get("Template GREATER"),
                                EMAILS.get("Template GREATER_OR_EQUAL")
                        )
                ),
                Arguments.of(
                        "PLATINUM 75",
                        new NewPrice()
                                .itemType(ItemType.PLATINUM)
                                .price("75.00"),
                        Set.of(
                                EMAILS.get("Template IS_NOT"),
                                EMAILS.get("Template Complex")
                        )
                )
        );
    }
}