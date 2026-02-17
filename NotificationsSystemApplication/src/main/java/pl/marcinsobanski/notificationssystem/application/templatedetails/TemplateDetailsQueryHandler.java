package pl.marcinsobanski.notificationssystem.application.templatedetails;

import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.application.cqrs.QueryHandler;
import pl.marcinsobanski.notificationssystem.api.cqrs.templatedetails.TemplateDetailsQuery;
import pl.marcinsobanski.notificationssystem.api.cqrs.templatedetails.TemplateDetailsView;
import pl.marcinsobanski.notificationssystem.application.listtemplates.TemplateToTemplateDetailsViewConverter;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;

@RequiredArgsConstructor
public class TemplateDetailsQueryHandler implements QueryHandler<TemplateDetailsView, TemplateDetailsQuery> {

    private final TemplateRepository templateRepository;
    private final TemplateToTemplateDetailsViewConverter templateToTemplateDetailsViewConverter;

    @Override
    public TemplateDetailsView handle(TemplateDetailsQuery query) {
        return templateRepository.getTemplate(query.templateId())
                .map(templateToTemplateDetailsViewConverter::convert)
                .orElseThrow();
    }
}