package pl.marcinsobanski.notificationssystem.application.converters;

import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.RuleOperator;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.Rule;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class RuleConverter {

    public Rule convertRule(pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule rule) {
        return switch (rule.operator()) {
            case ITEM_IS, ITEM_IS_NOT ->
                    new ItemTypeRule(convertToItemTypeRule(rule.operator()), ItemType.valueOf(rule.operand()));
            case PRICE_GREATER, PRICE_GREATER_OR_EQUALS, PRICE_LESS, PRICE_LESS_OR_EQUALS,
                 PRICE_EQUALS -> new PriceRule(convertToPriceRule(rule.operator()), new BigDecimal(rule.operand()));
        };
    }

    public ItemTypeRule.Operator convertToItemTypeRule(RuleOperator ruleOperator) {
        return switch (ruleOperator) {
            case ITEM_IS -> ItemTypeRule.Operator.IS;
            case ITEM_IS_NOT -> ItemTypeRule.Operator.IS_NOT;
            default -> throw new IllegalArgumentException("Illegal argument" + ruleOperator);
        };
    }

    public PriceRule.Operator convertToPriceRule(RuleOperator ruleOperator) {
        return switch (ruleOperator) {
            case PRICE_GREATER -> PriceRule.Operator.GREATER;
            case PRICE_GREATER_OR_EQUALS -> PriceRule.Operator.GREATER_OR_EQUAL;
            case PRICE_LESS -> PriceRule.Operator.LESS;
            case PRICE_LESS_OR_EQUALS -> PriceRule.Operator.LESS_OR_EQUAL;
            case PRICE_EQUALS -> PriceRule.Operator.EQUAL;
            default -> throw new IllegalArgumentException("Illegal argument" + ruleOperator);
        };
    }

}
