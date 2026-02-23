package pl.marcinsobanski.notificationssystem.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.marcinsobanski.notificationssystem.application.converters.RuleConverter;
import pl.marcinsobanski.notificationssystem.application.createtemplate.CreateTemplateCommandHandler;
import pl.marcinsobanski.notificationssystem.application.listtemplates.ListTemplatesQueryHandler;
import pl.marcinsobanski.notificationssystem.application.templatedetails.TemplateToTemplateDetailsViewConverter;
import pl.marcinsobanski.notificationssystem.application.replacetemplate.ReplaceTemplateCommandHandler;
import pl.marcinsobanski.notificationssystem.application.sendnewprice.EmailSender;
import pl.marcinsobanski.notificationssystem.application.sendnewprice.SendNewPriceCommandHandler;
import pl.marcinsobanski.notificationssystem.application.templatedetails.TemplateDetailsQueryHandler;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleFactory;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleRepository;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;

@Configuration
public class ApplicationBeansConfiguration {


    @Bean
    public ListTemplatesQueryHandler listTemplatesQueryHandler(
            TemplateRepository templateRepository
    ) {
        return new ListTemplatesQueryHandler(templateRepository);
    }

    @Bean
    public TemplateDetailsQueryHandler templateDetailsQueryHandler(
            TemplateRepository templateRepository,
            TemplateToTemplateDetailsViewConverter templateToTemplateDetailsConverter
    ) {
        return new TemplateDetailsQueryHandler(templateRepository, templateToTemplateDetailsConverter);
    }

    @Bean
    public TemplateToTemplateDetailsViewConverter templateToTemplateDetailsConverter() {
        return new TemplateToTemplateDetailsViewConverter();
    }

    @Bean
    public CreateTemplateCommandHandler createTemplateCommandHandler(
            TemplateRepository templateRepository,
            RuleConverter ruleConverter,
            CompiledRuleFactory compiledRuleFactory,
            CompiledRuleRepository compiledRuleRepository
    ) {
        return new CreateTemplateCommandHandler(templateRepository, ruleConverter, compiledRuleFactory, compiledRuleRepository);
    }

    @Bean
    public ReplaceTemplateCommandHandler replaceTemplateCommandHandler(
            TemplateRepository templateRepository,
            RuleConverter ruleConverter,
            CompiledRuleFactory compiledRuleFactory,
            CompiledRuleRepository compiledRuleRepository
    ) {
        return new ReplaceTemplateCommandHandler(templateRepository, ruleConverter, compiledRuleFactory, compiledRuleRepository);
    }

    @Bean
    public RuleConverter ruleConverter() {
        return new RuleConverter();
    }

    @Bean
    public SendNewPriceCommandHandler sendNewPriceCommandHandler(
            TemplateRepository templateRepository,
            EmailSender emailSender
    ) {
        return new SendNewPriceCommandHandler(templateRepository, emailSender);
    }

}
