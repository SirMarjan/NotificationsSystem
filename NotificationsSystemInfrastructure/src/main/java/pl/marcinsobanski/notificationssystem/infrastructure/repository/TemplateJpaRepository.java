package pl.marcinsobanski.notificationssystem.infrastructure.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.domain.template.SimpleTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TemplateJpaRepository extends JpaRepository<TemplateEntity, UUID> {

    @Query("SELECT new pl.marcinsobanski.notificationssystem.domain.template.SimpleTemplate(t.id, t.title, t.content, t.receiversEmails) FROM TemplateEntity t ORDER BY t.creationTime DESC")
    List<SimpleTemplate> findAllSimpleTemplateByCreationTimeDesc();

    @EntityGraph(attributePaths = {"rules"})
    @Query("SELECT t FROM TemplateEntity t WHERE t.id = :id")
    Optional<TemplateEntity> findByIdWithRules(UUID id);


    @Query("SELECT r.id FROM TemplateEntity t JOIN t.rules r WHERE t.id = :id")
    Set<UUID> findTemplateRulesIdsById(UUID id);


    @Query("""
            SELECT new pl.marcinsobanski.notificationssystem.domain.template.SimpleTemplate(t.id, t.title, t.content, t.receiversEmails)
            FROM TemplateEntity t
            WHERE t.compiledRuleEntity.id IN (
                SELECT cre.id
                FROM CompiledRuleEntity cre
                WHERE (cre.itemType IS NULL OR cre.itemTypeEqual = true AND cre.itemType = :itemType OR cre.itemTypeEqual = false AND cre.itemType != :itemType)
                AND (cre.ceilLimit IS NULL OR cre.ceilLimitIncluding = true AND cre.ceilLimit >= :price OR cre.ceilLimitIncluding = false AND cre.ceilLimit > :price)
                AND (cre.floorLimit IS NULL OR cre.floorLimitIncluding = true AND cre.floorLimit <= :price OR cre.floorLimitIncluding = false AND cre.floorLimit < :price)
            )
            """)
    List<SimpleTemplate> findTemplatesMatchingRule(
            ItemType itemType,
            BigDecimal price
    );
}