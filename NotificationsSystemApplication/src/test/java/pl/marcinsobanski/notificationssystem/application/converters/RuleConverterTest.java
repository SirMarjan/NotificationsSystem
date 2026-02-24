package pl.marcinsobanski.notificationssystem.application.converters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import org.junit.jupiter.params.provider.MethodSource;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.RuleOperator;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.Rule;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RuleConverterTest {

    RuleConverter ruleConverter = new RuleConverter();

    static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.ITEM_IS, "GOLD"),
                        new ItemTypeRule(ItemTypeRule.Operator.IS, ItemType.GOLD)
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.ITEM_IS, "SILVER"),
                        new ItemTypeRule(ItemTypeRule.Operator.IS, ItemType.SILVER)
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.ITEM_IS, "PLATINUM"),
                        new ItemTypeRule(ItemTypeRule.Operator.IS, ItemType.PLATINUM)
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.ITEM_IS_NOT, "GOLD"),
                        new ItemTypeRule(ItemTypeRule.Operator.IS_NOT, ItemType.GOLD)
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.PRICE_GREATER, "100"),
                        new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("100"))
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.PRICE_GREATER_OR_EQUALS, "50"),
                        new PriceRule(PriceRule.Operator.GREATER_OR_EQUAL, new BigDecimal("50"))
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.PRICE_LESS, "25"),
                        new PriceRule(PriceRule.Operator.LESS, new BigDecimal("25"))
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.PRICE_LESS_OR_EQUALS, "12.5"),
                        new PriceRule(PriceRule.Operator.LESS_OR_EQUAL, new BigDecimal("12.5"))
                ),
                Arguments.of(
                        new pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule(RuleOperator.PRICE_EQUALS, "6.25"),
                        new PriceRule(PriceRule.Operator.EQUAL, new BigDecimal("6.25"))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void convertRule(
            pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule inRule,
            Rule outRule
    ) {
        final var resultRule = ruleConverter.convertRule(inRule);
        assertEquals(outRule, resultRule);
    }

}