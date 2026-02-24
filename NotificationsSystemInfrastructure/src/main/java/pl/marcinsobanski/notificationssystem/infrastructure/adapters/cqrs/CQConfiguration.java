package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.marcinsobanski.notificationssystem.application.cqrs.CommandHandler;
import pl.marcinsobanski.notificationssystem.application.cqrs.QueryHandler;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters.CQTypeNameProvider;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters.LoggingCQBusFilterImpl;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters.ObservationCQBusFilterImpl;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.providers.CommandHandlersProvider;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.providers.QueryHandlersProvider;

import java.util.*;

@Configuration
public class CQConfiguration {

    @Bean
    public CQTypeNameProvider cqTypeNameProvider() {
        return new CQTypeNameProvider();
    }

    @Bean
    public LoggingCQBusFilterImpl loggingCQBusFilter(
            CQTypeNameProvider cqTypeNameProvider
    ) {
        return new LoggingCQBusFilterImpl(cqTypeNameProvider);
    }

    @Bean
    public ObservationCQBusFilterImpl observationCQBusFilter(
            CQTypeNameProvider cqTypeNameProvider,
            ObservationRegistry observationRegistry
    ) {
        return new ObservationCQBusFilterImpl(observationRegistry, cqTypeNameProvider);
    }

    @Bean
    public CommandHandlersProvider commandHandlersProvider(
            Collection<CommandHandler<?, ?>> commandHandlers
    ) {
        return new CommandHandlersProvider(commandHandlers);
    }

    @Bean

    public QueryHandlersProvider queryHandlersProvider(
            Collection<QueryHandler<?, ?>> queryHandlers
    ) {
        return new QueryHandlersProvider(queryHandlers);
    }

    @Bean
    public CQBusImpl CQBusImpl(
            CommandHandlersProvider commandHandlersProvider,
            QueryHandlersProvider queryHandlersProvider,
            LoggingCQBusFilterImpl loggingCQBusFilter,
            ObservationCQBusFilterImpl observationCQBusFilter
    ) {
        return new CQBusImpl(
                commandHandlersProvider,
                queryHandlersProvider,
                List.of(
                        loggingCQBusFilter,
                        observationCQBusFilter
                )
        );
    }
}
