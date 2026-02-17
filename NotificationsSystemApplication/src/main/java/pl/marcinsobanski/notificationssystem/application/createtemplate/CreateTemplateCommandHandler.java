package pl.marcinsobanski.notificationssystem.application.createtemplate;

import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.application.cqrs.CommandHandler;
import pl.marcinsobanski.notificationssystem.api.cqrs.createtemplate.CreateTemplateCommand;
import pl.marcinsobanski.notificationssystem.api.cqrs.createtemplate.CreateTemplateResult;
import pl.marcinsobanski.notificationssystem.application.converters.RuleConverter;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleFactory;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleRepository;
import pl.marcinsobanski.notificationssystem.domain.template.Template;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;

import java.time.Instant;

@RequiredArgsConstructor
public class CreateTemplateCommandHandler implements CommandHandler<CreateTemplateResult, CreateTemplateCommand> {

    private final TemplateRepository templateRepository;
    private final RuleConverter ruleConverter;
    private final CompiledRuleFactory compiledRuleFactory;
    private final CompiledRuleRepository compiledRuleRepository;

    @Override
    public CreateTemplateResult handle(CreateTemplateCommand command) {
        final var template = Template
                .builder()
                .title(command.title())
                .content(command.content())
                .receiversEmails(command.receiversEmails())
                .rules(command.rules().stream().map(ruleConverter::convertRule).toList())
                .creationTime(Instant.now()).build();
        final var templateId = templateRepository.saveTemplate(template);

        compiledRuleFactory
                .calculateCompiledRule(template.getRules())
                .ifPresent(rule -> compiledRuleRepository.saveCompiledRule(rule, templateId));

        return new CreateTemplateResult(templateId);
    }

}