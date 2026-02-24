package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.providers;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;
import pl.marcinsobanski.notificationssystem.application.cqrs.QueryHandler;

import java.util.Collection;

public class QueryHandlersProvider extends MessageHandlersProvider<Query<?>, QueryHandler<?, ?>> {
    public QueryHandlersProvider(
            Collection<QueryHandler<?, ?>> commandHandlers) {
        super(commandHandlers, (Class) QueryHandler.class, "QUERY");
    }
}
