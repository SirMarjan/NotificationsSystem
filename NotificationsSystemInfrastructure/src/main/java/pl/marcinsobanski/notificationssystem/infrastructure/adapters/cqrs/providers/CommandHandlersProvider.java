package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.providers;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.application.cqrs.CommandHandler;

import java.util.Collection;

public class CommandHandlersProvider extends MessageHandlersProvider<Command<?>, CommandHandler<?, ?>> {
    public CommandHandlersProvider(
            Collection<CommandHandler<?, ?>> commandHandlers) {
        super(commandHandlers, (Class) CommandHandler.class, "COMMAND");
    }
}
