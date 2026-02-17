package pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.filters;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Message;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;

public class CQTypeNameProvider {


    public String lowerCase(Message<?> message) {
        return switch (message) {
            case Command command -> "command";
            case Query query -> "query";
        };
    }

    public String camelCase(Message<?> message) {
        return switch (message) {
            case Command command -> "Command";
            case Query query -> "Query";
        };
    }

}
