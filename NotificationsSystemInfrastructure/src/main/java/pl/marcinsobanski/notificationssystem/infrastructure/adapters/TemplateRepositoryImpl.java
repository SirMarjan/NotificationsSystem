package pl.marcinsobanski.notificationssystem.infrastructure.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.domain.template.SimpleTemplate;
import pl.marcinsobanski.notificationssystem.domain.template.Template;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.RuleEntity;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.RuleJpaRepository;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.TemplateJpaRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TemplateRepositoryImpl implements TemplateRepository {

    private final TemplateJpaRepository templateJpaRepository;
    private final RuleJpaRepository ruleJpaRepository;
    private final TemplateEntityToTemplateConverter templateEntityToTemplateConverter;
    private final TemplateToTemplateEntityConverter templateToTemplateEntityConverter;

    @Override
    public Instant getTemplateCreationTime(UUID id) {
        return templateJpaRepository.findTemplateCreationTimeById(id).orElseThrow();
    }

    @Override
    public List<SimpleTemplate> getAllSimpleTemplates() {
        return templateJpaRepository.findAllSimpleTemplateByCreationTimeDesc();
    }

    @Override
    public Optional<Template> getTemplate(UUID id) {
        return templateJpaRepository.findByIdWithRules(id)
                .map(templateEntityToTemplateConverter::convert);
    }

    @Override
    public UUID saveTemplate(Template template) {
        return templateJpaRepository
                .save(templateToTemplateEntityConverter.convert(template))
                .getId();
    }

    @Override
    public void updateTemplate(Template template) {
        final var oldRulesIds = new HashSet<>(templateJpaRepository.findTemplateRulesIdsById(template.getId()));
        final var newTemplate = templateJpaRepository.save(templateToTemplateEntityConverter.convert(template));
        final var newRulesIds = newTemplate.getRules().stream().map(RuleEntity::getId).collect(Collectors.toSet());
        oldRulesIds.removeAll(newRulesIds);
        if (!oldRulesIds.isEmpty()) {
            ruleJpaRepository.removeRulesWithoutTemplateByRulesIds(oldRulesIds);
        }
    }

    @Override
    public List<SimpleTemplate> findTemplatesMatchingRules(ItemType itemType, BigDecimal price) {
        return templateJpaRepository.findTemplatesMatchingRule(itemType, price);
    }

}
