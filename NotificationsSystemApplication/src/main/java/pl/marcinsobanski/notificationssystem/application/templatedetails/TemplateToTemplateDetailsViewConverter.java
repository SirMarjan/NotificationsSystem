package pl.marcinsobanski.notificationssystem.application.templatedetails;

import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.Rule;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.models.RuleOperator;
import pl.marcinsobanski.notificationssystem.api.cqrs.templatedetails.TemplateDetailsView;
import pl.marcinsobanski.notificationssystem.domain.template.Template;
import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;

@RequiredArgsConstructor
public class TemplateToTemplateDetailsViewConverter {

    public TemplateDetailsView convert(Template template) {
        return new TemplateDetailsView(
                template.getTitle(),
                template.getContent(),
                template.getReceiversEmails(),
                template.getRules().stream().map(
                        rule -> switch (rule) {
                            case ItemTypeRule itemTypeRule -> new Rule(
                                    convertOperation(itemTypeRule.getOperator()),
                                    itemTypeRule.getItemType().name()
                            );
                            case PriceRule priceRule -> new Rule(
                                    convertOperation(priceRule.getOperator()),
                                    priceRule.getPrice().toPlainString()
                            );
                        }
                ).toList()
        );
    }

    public RuleOperator convertOperation(ItemTypeRule.Operator operator) {
        return switch (operator) {
            case IS -> RuleOperator.ITEM_IS;
            case IS_NOT -> RuleOperator.ITEM_IS_NOT;
        };
    }

    public RuleOperator convertOperation(PriceRule.Operator operator) {
        return switch (operator) {
            case EQUAL -> RuleOperator.PRICE_EQUALS;
            case GREATER -> RuleOperator.PRICE_GREATER;
            case GREATER_OR_EQUAL -> RuleOperator.PRICE_GREATER_OR_EQUALS;
            case LESS -> RuleOperator.PRICE_LESS;
            case LESS_OR_EQUAL -> RuleOperator.PRICE_LESS_OR_EQUALS;
        };
    }


}
