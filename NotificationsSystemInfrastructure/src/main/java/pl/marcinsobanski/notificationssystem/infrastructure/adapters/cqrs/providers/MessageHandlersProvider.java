package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.providers;

import org.apache.commons.lang3.reflect.TypeUtils;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Message;
import pl.marcinsobanski.notificationssystem.application.cqrs.MessageHandler;

import java.util.*;

class MessageHandlersProvider<M extends Message<?>, MH extends MessageHandler<?, ?>> {

    private final Map<Class<M>, MH> messageHandlerMap;

    protected MessageHandlersProvider(
            Collection<MH> messageHandlers,
            Class<MH> messageClazz,
            String messageHandlerClazzParameter
    ) {
        final var messageHandlerTmpMap = new HashMap<Class<M>, MH>();
        messageHandlers.forEach(commandHandlerBean -> messageHandlerTmpMap.put(extract(commandHandlerBean, messageClazz, messageHandlerClazzParameter), commandHandlerBean));
        messageHandlerMap = Collections.unmodifiableMap(messageHandlerTmpMap);

    }

    private Class<M> extract(Object cqBean, Class<MH> messageClazz, String messageHandlerClazzParameter) {
        final var typeArguments = TypeUtils.getTypeArguments(cqBean.getClass(), messageClazz);
        return (Class<M>) typeArguments.entrySet().stream()
                .filter(typeVariableTypeEntry -> messageHandlerClazzParameter.equals(typeVariableTypeEntry.getKey().getName()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }

    public Optional<MH> getHandler(M message) {
        return Optional.ofNullable(messageHandlerMap.get(message.getClass()));
    }

}
