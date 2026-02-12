package pl.marcinsobanski.notificationssystem.api.cqrs.listtemplates;

import java.util.UUID;

public record TemplateListItem(
        UUID id,
        String title
) {
}
