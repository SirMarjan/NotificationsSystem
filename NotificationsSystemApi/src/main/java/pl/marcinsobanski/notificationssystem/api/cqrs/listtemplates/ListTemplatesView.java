package pl.marcinsobanski.notificationssystem.api.cqrs.listtemplates;

import java.util.List;

public record ListTemplatesView(
        List<TemplateListItem> templateListElements
) {
}
