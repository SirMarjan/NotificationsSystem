package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.stereotype.Service;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.CommandHandler;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.QueryHandler;

import java.util.*;

@Service
public class CQHandlersProvider {

    private final Map<Class<Command<?>>, CommandHandler<?, ?>> commandHandlerMap;
    private final Map<Class<Query<?>>, QueryHandler<?, ?>> queryHandlerMap;

    public CQHandlersProvider(
            List<CommandHandler<?, ?>> commandHandlerBeans,
            List<QueryHandler<?, ?>> queryHandlerBeans
    ) {
        final var commandHandlers = new HashMap<Class<Command<?>>, CommandHandler<?, ?>>();
        commandHandlerBeans.forEach(commandHandlerBean -> commandHandlers.put((Class<Command<?>>) extract(commandHandlerBean, CommandHandler.class, "COMMAND"), commandHandlerBean));
        commandHandlerMap = Collections.unmodifiableMap(commandHandlers);


        final var queryHandlers = new HashMap<Class<Query<?>>, QueryHandler<?, ?>>();
        queryHandlerBeans.forEach(queryHandlerBean -> queryHandlers.put((Class<Query<?>>) extract(queryHandlerBean, QueryHandler.class, "QUERY"), queryHandlerBean));
        queryHandlerMap = Collections.unmodifiableMap(queryHandlers);
    }

    private Class<?> extract(Object cqBean, Class<?> cqHandlerClazz, String argument) {
        final var typeArguments = TypeUtils.getTypeArguments(cqBean.getClass(), cqHandlerClazz);
        return (Class) typeArguments.entrySet().stream()
                .filter(typeVariableTypeEntry -> argument.equals(typeVariableTypeEntry.getKey().getName()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }

    public <R, C extends Command<R>> CommandHandler<R, C> getCommandHandler(C command) {
        final var clazz = command.getClass();
        return (CommandHandler<R, C>) commandHandlerMap.get(clazz);
    }

    public <R, Q extends Query<R>> QueryHandler<R, Q> getQueryHandler(Q query) {
        final var clazz = query.getClass();
        return (QueryHandler<R, Q>) queryHandlerMap.get(clazz);
    }

}
