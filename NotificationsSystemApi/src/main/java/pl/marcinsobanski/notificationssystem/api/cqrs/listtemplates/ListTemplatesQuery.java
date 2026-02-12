package pl.marcinsobanski.notificationssystem.api.cqrs.listtemplates;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;

public record ListTemplatesQuery() implements Query<ListTemplatesView> {
}