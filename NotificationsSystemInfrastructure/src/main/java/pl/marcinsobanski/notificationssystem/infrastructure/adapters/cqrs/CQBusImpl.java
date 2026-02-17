package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Message;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;
import pl.marcinsobanski.notificationssystem.application.cqrs.CQBus;
import pl.marcinsobanski.notificationssystem.application.cqrs.MessageHandler;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters.CQBusFilter;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.providers.CommandHandlersProvider;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.providers.QueryHandlersProvider;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class CQBusImpl implements CQBus {

    private final CommandHandlersProvider commandHandlersProvider;
    private final QueryHandlersProvider queryHandlersProvider;
    private final List<CQBusFilter> cqBusFilters;

    @Override
    public <RESULT> RESULT executeCommand(Command<RESULT> command) {
        return executeMessage(command);
    }

    @Override
    public <RESULT> RESULT executeQuery(Query<RESULT> query) {
        return executeMessage(query);
    }

    private <RESULT, MESSAGE extends Message<RESULT>> RESULT executeMessage(MESSAGE message) {
        var lastMessageHandler = getMessageHandler(message);
        for (CQBusFilter cqBusFilter : cqBusFilters.reversed()) {
            lastMessageHandler = new FilterMessageHandler<>(cqBusFilter, lastMessageHandler);
        }
        return lastMessageHandler.handle(message);
    }

    private <RESULT, MESSAGE extends Message<RESULT>> MessageHandler<RESULT, MESSAGE> getMessageHandler(MESSAGE message) {
        return (MessageHandler<RESULT, MESSAGE>) (switch (message) {
            case Command command -> commandHandlersProvider.getHandler(command);
            case Query query -> queryHandlersProvider.getHandler(query);
        }).orElseThrow();
    }

    private record FilterMessageHandler<RESULT, MESSAGE extends Message<RESULT>>(
            CQBusFilter cqBusFilter,
            MessageHandler<RESULT, MESSAGE> nextCommandHandler
    ) implements MessageHandler<RESULT, MESSAGE> {

        @Override
        public RESULT handle(MESSAGE message) {
            return cqBusFilter.handleMessage(message, nextCommandHandler);
        }
    }
}
