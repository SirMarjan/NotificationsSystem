package pl.marcinsobanski.notificationssystem.application.cqrs;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Message;

public interface MessageHandler<RESULT, MESSAGE extends Message<RESULT>> {

    RESULT handle(MESSAGE message);

}