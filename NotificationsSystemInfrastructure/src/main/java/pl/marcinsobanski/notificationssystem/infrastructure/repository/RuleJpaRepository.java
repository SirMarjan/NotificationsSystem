package pl.marcinsobanski.notificationssystem.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface RuleJpaRepository extends JpaRepository<RuleEntity, UUID> {

    Optional<RuleEntity> findByRuleOperatorAndItemTypeOperand(RuleOperator ruleOperator, ItemType itemTypeOperand);

    Optional<RuleEntity> findAllByRuleOperatorAndPriceOperand(RuleOperator ruleOperator, BigDecimal priceOperand);

    @Modifying
    @Query("DELETE FROM RuleEntity r where r.id IN :rulesIds AND r.id NOT IN (SELECT rule.id FROM TemplateEntity t JOIN t.rules rule)")
    void removeRulesWithoutTemplateByRulesIds(Collection<UUID> rulesIds);

}