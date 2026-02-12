package pl.marcinsobanski.notificationssystem.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.marcinsobanski.notificationssystem.domain.compiledrule.CompiledRuleFactory;

@Configuration
public class DomainBeansConfiguration {

    @Bean
    public CompiledRuleFactory compiledRuleFactory() {
        return new CompiledRuleFactory();
    }

}
