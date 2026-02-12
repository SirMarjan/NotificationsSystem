package pl.marcinsobanski.notificationssystem.application.replacetemplate;

import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.CommandHandler;
import pl.marcinsobanski.notificationssystem.api.cqrs.replacetemplate.ReplaceTemplateCommand;
import pl.marcinsobanski.notificationssystem.application.converters.RuleConverter;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleFactory;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleRepository;
import pl.marcinsobanski.notificationssystem.domain.template.Template;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;

import java.time.Instant;

@RequiredArgsConstructor
public class ReplaceTemplateCommandHandler implements CommandHandler<Void, ReplaceTemplateCommand> {

    private final TemplateRepository templateRepository;
    private final RuleConverter ruleConverter;
    private final CompiledRuleFactory compiledRuleFactory;
    private final CompiledRuleRepository compiledRuleRepository;


    @Override
    public Void handle(ReplaceTemplateCommand command) {
        final var template = Template
                .builder()
                .id(command.id())
                .title(command.title())
                .content(command.content())
                .receiversEmails(command.receiversEmails())
                .rules(command.rules().stream().map(ruleConverter::convertRule).toList())
                .creationTime(Instant.now()).build();
        templateRepository.updateTemplate(template);

        compiledRuleFactory
                .calculateCompiledRule(template.getRules())
                .ifPresent(rule -> compiledRuleRepository.saveCompiledRule(rule, command.id()));

        return null;
    }
}
