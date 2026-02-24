package pl.marcinsobanski.notificationssystem.application.listtemplates;

import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.application.cqrs.QueryHandler;
import pl.marcinsobanski.notificationssystem.api.cqrs.listtemplates.ListTemplatesQuery;
import pl.marcinsobanski.notificationssystem.api.cqrs.listtemplates.ListTemplatesView;
import pl.marcinsobanski.notificationssystem.api.cqrs.listtemplates.TemplateListItem;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;

@RequiredArgsConstructor
public class ListTemplatesQueryHandler implements QueryHandler<ListTemplatesView, ListTemplatesQuery> {

    private final TemplateRepository templateRepository;

    @Override
    public ListTemplatesView handle(ListTemplatesQuery query) {
        final var templates = templateRepository.getAllSimpleTemplates().stream()
                .map(template -> new TemplateListItem(template.getId(), template.getTitle()))
                .toList();
        return new ListTemplatesView(templates);
    }
}
