package pl.marcinsobanski.notificationssystem.infrastructure.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRule;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleRepository;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.TemplateJpaRepository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompiledRuleRepositoryImpl implements CompiledRuleRepository {

    private final TemplateJpaRepository templateJpaRepository;
    private final CompiledRuleToCompiledRuleEntityConverter compiledRuleToCompiledRuleEntityConverter;

    @Override
    public void saveCompiledRule(CompiledRule compiledRule, UUID templateId) {
        final var template = templateJpaRepository.findById(templateId).orElseThrow();
        final var compiledRuleEntity = compiledRuleToCompiledRuleEntityConverter.convert(compiledRule);
        template.setCompiledRuleEntity(compiledRuleEntity);
    }


}
