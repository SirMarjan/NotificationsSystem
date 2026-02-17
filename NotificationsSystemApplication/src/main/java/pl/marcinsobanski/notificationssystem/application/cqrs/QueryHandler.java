package pl.marcinsobanski.notificationssystem.application.cqrs;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;

public interface QueryHandler<RESULT, QUERY extends Query<RESULT>> extends MessageHandler<RESULT, QUERY> {

    @Override
    RESULT handle(QUERY query);

}