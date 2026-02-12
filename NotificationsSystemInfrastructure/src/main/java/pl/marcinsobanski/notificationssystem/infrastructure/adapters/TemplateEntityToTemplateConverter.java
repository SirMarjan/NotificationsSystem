package pl.marcinsobanski.notificationssystem.infrastructure.adapters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.marcinsobanski.notificationssystem.domain.template.Template;
import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.Rule;
import pl.marcinsobanski.notificationssystem.infrastructure.commons.SpringMapperConfig;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.RuleEntity;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.TemplateEntity;

import java.util.List;

@Mapper(config = SpringMapperConfig.class)
public interface TemplateEntityToTemplateConverter {

    @Mapping(source = "rules", target = "rules", qualifiedByName = "rulesMapper")
    Template convert(TemplateEntity template);

    @Named("rulesMapper")
    default List<Rule> rulesMapper(List<RuleEntity> ruleEntities) {
        return ruleEntities.stream()
                .map(ruleEntity -> switch (ruleEntity.getRuleOperator()) {
                    case IS, IS_NOT ->
                            new ItemTypeRule(ItemTypeRule.Operator.valueOf(ruleEntity.getRuleOperator().name()), ruleEntity.getItemTypeOperand());
                    case GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL, EQUAL ->
                            new PriceRule(PriceRule.Operator.valueOf(ruleEntity.getRuleOperator().name()), ruleEntity.getPriceOperand());
                })
                .toList();
    }

}