package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;
import pl.marcinsobanski.notificationssystem.application.cqrs.CQCommandHandler;

@Service
@Slf4j
@RequiredArgsConstructor
public class CQCommandHandlerImpl implements CQCommandHandler {

    private final CQHandlersProvider cqHandlersProvider;
    private final ObservationRegistry observationRegistry;

    @Override
    public <RESULT> RESULT executeCommand(Command<RESULT> command) {
        final var commandExecutor = cqHandlersProvider.getCommandHandler(command);
        if (commandExecutor == null) {
            throw new IllegalArgumentException("Command handler for class " + command.getClass() + " not exist");
        }
        return Observation.createNotStarted("execute command", observationRegistry)
                .lowCardinalityKeyValue("command.class", command.getClass().getSimpleName())
                .observe(() -> {
                    log.info("Execute command {}", command);
                    try {
                        final var result = commandExecutor.handle(command);
                        log.info("Command {} executed: {}", command, result);
                        return result;
                    } catch (Exception e) {
                        log.error("Command {} execution failed", command, e);
                        throw e;
                    }
                });
    }

    @Override
    public <RESULT> RESULT executeQuery(Query<RESULT> query) {
        final var queryExecutor = cqHandlersProvider.getQueryHandler(query);
        if (queryExecutor == null) {
            throw new IllegalArgumentException("Query handler for class " + query.getClass() + " not exist");
        }
        return Observation.createNotStarted("execute query", observationRegistry)
                .lowCardinalityKeyValue("query.class", query.getClass().getName())
                .observe(() -> {
                    log.info("Execute query {}", query);
                    try {
                        final var result = queryExecutor.handle(query);
                        log.info("Query {} executed: {}", query, result);
                        return result;
                    } catch (Exception e) {
                        log.error("Query {} execution failed", query, e);
                        throw e;
                    }
                });
    }
}
