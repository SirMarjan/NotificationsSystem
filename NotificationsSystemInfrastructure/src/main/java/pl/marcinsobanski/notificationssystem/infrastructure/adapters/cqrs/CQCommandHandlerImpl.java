package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.stereotype.Service;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.CommandHandler;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.QueryHandler;
import pl.marcinsobanski.notificationssystem.application.cqrs.CQCommandHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
// TODO remove duplication
public class CQCommandHandlerImpl implements CQCommandHandler {

    private final Map<Class<Command<?>>, CommandHandler<?, ?>> commandHandlerMap;
    private final Map<Class<Query<?>>, QueryHandler<?, ?>> queryHandlerMap;

    public CQCommandHandlerImpl(
            List<CommandHandler<?, ?>> commandHandlerBeans,
            List<QueryHandler<?, ?>> queryHandlerBeans
    ) {
        final var commandHandlers = new HashMap<Class<Command<?>>, CommandHandler<?, ?>>();
        for (CommandHandler<?, ?> commandHandlerBean : commandHandlerBeans) {
            final var typeArguments = TypeUtils.getTypeArguments(commandHandlerBean.getClass(), CommandHandler.class);
            final var commandClazz = (Class) typeArguments.entrySet().stream()
                    .filter(typeVariableTypeEntry -> "COMMAND".equals(typeVariableTypeEntry.getKey().getName()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElseThrow();
            commandHandlers.put(commandClazz, commandHandlerBean);
        }
        commandHandlerMap = Collections.unmodifiableMap(commandHandlers);

        final var queryHandlers = new HashMap<Class<Query<?>>, QueryHandler<?, ?>>();
        for (QueryHandler<?, ?> queryHandlerBean : queryHandlerBeans) {
            final var typeArguments = TypeUtils.getTypeArguments(queryHandlerBean.getClass(), QueryHandler.class);
            final var queryClazz = (Class) typeArguments.entrySet().stream()
                    .filter(typeVariableTypeEntry -> "QUERY".equals(typeVariableTypeEntry.getKey().getName()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElseThrow();
            queryHandlers.put(queryClazz, queryHandlerBean);
        }
        queryHandlerMap = Collections.unmodifiableMap(queryHandlers);
    }

    @Override
    public <RESULT> RESULT executeCommand(Command<RESULT> command) {
        final var commandExecutor = (CommandHandler<RESULT, Command<RESULT>>) commandHandlerMap.get(command.getClass());
        if (commandExecutor == null) {
            throw new IllegalArgumentException("Command handler for class " + command.getClass() + " not exist");
        }
        log.info("Execute command {}", command);
        try {
            final var result = commandExecutor.handle(command);
            log.info("Command {} executed: {}", command, result);
            return result;
        } catch (Exception e) {
            log.error("Command {} execution failed", command, e);
            throw e;
        }
    }

    @Override
    public <RESULT> RESULT executeQuery(Query<RESULT> query) {
        final var queryExecutor = (QueryHandler<RESULT, Query<RESULT>>) queryHandlerMap.get(query.getClass());
        if (queryExecutor == null) {
            throw new IllegalArgumentException("Query handler for class " + query.getClass() + " not exist");
        }
        log.info("Execute query {}", query);
        try {
            final var result = queryExecutor.handle(query);
            log.info("Query {} executed: {}", query, result);
            return result;
        } catch (Exception e) {
            log.error("Query {} execution failed", query, e);
            throw e;
        }
    }
}
