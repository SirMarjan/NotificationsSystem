package pl.marcinsobanski.notificationssystem.domain.compiledrule;

import java.util.UUID;

public interface CompiledRuleRepository {

    void saveCompiledRule(CompiledRule compiledRule, UUID templateId);

}
