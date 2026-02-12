package pl.marcinsobanski.notificationssystem.api.cqrs.templatedetails;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Query;

import java.util.UUID;

public record TemplateDetailsQuery(
        UUID templateId
) implements Query<TemplateDetailsView> {
}
