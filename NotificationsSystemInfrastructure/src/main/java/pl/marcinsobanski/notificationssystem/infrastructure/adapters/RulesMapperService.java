package pl.marcinsobanski.notificationssystem.infrastructure.adapters;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.Rule;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.RuleEntity;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.RuleJpaRepository;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.RuleOperator;

import java.util.List;

@Service
public class RulesMapperService {

    private final RuleJpaRepository ruleJpaRepository;

    public RulesMapperService(RuleJpaRepository ruleJpaRepository) {
        this.ruleJpaRepository = ruleJpaRepository;
    }

    @Named("mapRulesToRuleEntities")
    public List<RuleEntity> mapRulesToRuleEntities(List<Rule> rules) {
        return rules.stream()
                .map(rule -> switch (rule) {
                    case ItemTypeRule itemTypeRule -> {
                        final var ruleType = RuleOperator.valueOf(itemTypeRule.getOperator().name());
                        yield ruleJpaRepository.findByRuleOperatorAndItemTypeOperand(ruleType, itemTypeRule.getItemType())
                                .orElseGet(() -> RuleEntity.builder().id(null).ruleOperator(ruleType).itemTypeOperand(itemTypeRule.getItemType()).build());
                    }

                    case PriceRule priceRule -> {
                        final var ruleType = RuleOperator.valueOf(priceRule.getOperator().name());
                        yield ruleJpaRepository.findAllByRuleOperatorAndPriceOperand(ruleType, priceRule.getPrice())
                                .orElseGet(() -> RuleEntity.builder().id(null).ruleOperator(ruleType).priceOperand(priceRule.getPrice()).build());
                    }
                })
                .toList();
    }
}

