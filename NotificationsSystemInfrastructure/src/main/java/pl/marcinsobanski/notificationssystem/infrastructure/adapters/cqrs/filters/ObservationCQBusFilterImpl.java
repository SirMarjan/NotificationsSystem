package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Message;
import pl.marcinsobanski.notificationssystem.application.cqrs.MessageHandler;

@RequiredArgsConstructor
public class ObservationCQBusFilterImpl implements CQBusFilter {

    private final ObservationRegistry observationRegistry;
    private final CQTypeNameProvider cqTypeNameProvider;

    @Override
    public <RESULT, MESSAGE extends Message<RESULT>, MESSAGE_HANDLER extends MessageHandler<RESULT, MESSAGE>> RESULT handleMessage(MESSAGE message, MESSAGE_HANDLER messageHandler) {
        final var nameLowerCase = cqTypeNameProvider.lowerCase(message);

        return Observation.createNotStarted("execute " + nameLowerCase, observationRegistry)
                .lowCardinalityKeyValue(nameLowerCase + ".class", messageHandler.getClass().getSimpleName())
                .observe(() -> messageHandler.handle(message));

    }
}
