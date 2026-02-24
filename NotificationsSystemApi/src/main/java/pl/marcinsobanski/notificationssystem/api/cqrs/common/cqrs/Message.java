package pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs;

public sealed interface Message<Result> permits Command, Query {
}
