package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Message;
import pl.marcinsobanski.notificationssystem.application.cqrs.MessageHandler;

public interface CQBusFilter {

    <RESULT, MESSAGE extends Message<RESULT>, MESSAGE_HANDLER extends MessageHandler<RESULT, MESSAGE>> RESULT handleMessage(
            MESSAGE message,
            MESSAGE_HANDLER messageHandler
    );

}
