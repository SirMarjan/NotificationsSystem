package pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs;

public interface CommandHandler<RESULT, COMMAND extends Command<RESULT>> {

    RESULT handle(COMMAND command);

}