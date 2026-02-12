package pl.marcinsobanski.notificationssystem.infrastructure.adapters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.marcinsobanski.notificationssystem.domain.template.Template;
import pl.marcinsobanski.notificationssystem.infrastructure.commons.SpringMapperConfig;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.TemplateEntity;

@Mapper(config = SpringMapperConfig.class, uses = RulesMapperService.class)
public interface TemplateToTemplateEntityConverter {

    @Mapping(target = "compiledRuleEntity", ignore = true)
    @Mapping(source = "rules", target = "rules", qualifiedByName = "mapRulesToRuleEntities")
    TemplateEntity convert(Template template);

}