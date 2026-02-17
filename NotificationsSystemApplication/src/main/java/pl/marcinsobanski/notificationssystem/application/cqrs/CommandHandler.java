package pl.marcinsobanski.notificationssystem.application.cqrs;


import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;

public interface CommandHandler<RESULT, COMMAND extends Command<RESULT>> extends MessageHandler<RESULT, COMMAND> {

    @Override
    RESULT handle(COMMAND command);

}