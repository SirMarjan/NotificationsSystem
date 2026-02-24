package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Message;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;
import pl.marcinsobanski.notificationssystem.application.cqrs.MessageHandler;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class LoggingCQBusFilterImpl implements CQBusFilter {

    private final CQTypeNameProvider cqTypeNameProvider;

    @Override
    public <RESULT, MESSAGE extends Message<RESULT>, MESSAGE_HANDLER extends MessageHandler<RESULT, MESSAGE>> RESULT handleMessage(
            MESSAGE message, MESSAGE_HANDLER messageHandler
    ) {
        final var nameLowerCase = cqTypeNameProvider.lowerCase(message);
        final var nameCamelCase = cqTypeNameProvider.lowerCase(message);
        log.info("Execute {} {}", nameLowerCase, message);
        try {
            final var result = messageHandler.handle(message);
            log.info("{} {} executed: {}", nameCamelCase, message, result);
            return result;
        } catch (Exception e) {
            log.error("{} {} execution failed", nameCamelCase, message, e);
            throw e;
        }
    }


}
