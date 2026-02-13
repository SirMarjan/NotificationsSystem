package pl.marcinsobanski.notificationssystem.domain.template;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository {

    Instant getTemplateCreationTime(UUID id);

    List<SimpleTemplate> getAllSimpleTemplates();

    Optional<Template> getTemplate(UUID id);

    UUID saveTemplate(Template template);

    void updateTemplate(Template template);

    List<SimpleTemplate> findTemplatesMatchingRules(
            ItemType itemType,
            BigDecimal price
    );
}