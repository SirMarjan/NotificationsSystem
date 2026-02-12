package pl.marcinsobanski.notificationssystem.infrastructure.adapters;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRule;
import pl.marcinsobanski.notificationssystem.infrastructure.commons.SpringMapperConfig;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.CompiledRuleEntity;

@Mapper(config = SpringMapperConfig.class)
public interface CompiledRuleToCompiledRuleEntityConverter {

    @Mapping(target = "id", ignore = true)
    CompiledRuleEntity convert(CompiledRule compiledRule);

}
