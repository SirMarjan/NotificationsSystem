package pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs;

public interface QueryHandler<RESULT, QUERY extends Query<RESULT>> {

    RESULT handle(QUERY query);

}