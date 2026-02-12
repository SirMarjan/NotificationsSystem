package pl.marcinsobanski.notificationssystem.infrastructure.delivery;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.marcinsobanski.notificationssystem.api.cqrs.listtemplates.ListTemplatesQuery;
import pl.marcinsobanski.notificationssystem.api.cqrs.templatedetails.TemplateDetailsQuery;
import pl.marcinsobanski.notificationssystem.api.endpoint.TemplateApi;
import pl.marcinsobanski.notificationssystem.api.model.AddNewTemplateResponse;
import pl.marcinsobanski.notificationssystem.api.model.TemplateDetails;
import pl.marcinsobanski.notificationssystem.api.model.TemplateListElement;
import pl.marcinsobanski.notificationssystem.api.model.UpdateTemplateResponse;
import pl.marcinsobanski.notificationssystem.application.createtemplate.CreateTemplateCommandHandler;
import pl.marcinsobanski.notificationssystem.application.listtemplates.ListTemplatesQueryHandler;
import pl.marcinsobanski.notificationssystem.application.replacetemplate.ReplaceTemplateCommandHandler;
import pl.marcinsobanski.notificationssystem.application.templatedetails.TemplateDetailsQueryHandler;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TemplateApiImpl implements TemplateApi {

    private final ListTemplatesQueryHandler listTemplatesQueryHandler;
    private final TemplateDetailsQueryHandler templateDetailsQueryHandler;
    private final CreateTemplateCommandHandler createTemplateCommandHandler;
    private final ReplaceTemplateCommandHandler replaceTemplateCommandHandler;
    private final TemplateDetailsViewToTemplateDetailsConverter templateDetailsViewToTemplateDetailsConverter;
    private final TemplateDetailsToCreateTemplateCommandConverter templateDetailsToCreateTemplateCommandConverter;
    private final TemplateDetailsToReplaceTemplateCommandConverter templateDetailsToReplaceTemplateCommandConverter;

    @Override
    @Transactional
    public ResponseEntity<AddNewTemplateResponse> addNewTemplate(TemplateDetails templateDetails) {
        final var result = createTemplateCommandHandler.handle(templateDetailsToCreateTemplateCommandConverter.convert(templateDetails));
        return ResponseEntity.ok(new AddNewTemplateResponse(result.templateId()));
    }

    @Override
    public ResponseEntity<TemplateDetails> getTemplateDetails(UUID templateId) {
        final var template = templateDetailsQueryHandler.handle(new TemplateDetailsQuery(templateId));
        return ResponseEntity.ok(templateDetailsViewToTemplateDetailsConverter.convert(template));
    }

    @Override
    public ResponseEntity<List<TemplateListElement>> getTemplatesList() {
        final var templates = listTemplatesQueryHandler.handle(new ListTemplatesQuery());
        return ResponseEntity.ok(templates.templateListElements().stream()
                .map(templateListItem -> new TemplateListElement(templateListItem.id(), templateListItem.title()))
                .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<UpdateTemplateResponse> updateTemplate(UUID templateId, TemplateDetails templateDetails) {
        final var replaceTemplateCommand = templateDetailsToReplaceTemplateCommandConverter.convert(templateDetails, templateId);
        replaceTemplateCommandHandler.handle(replaceTemplateCommand);
        return ResponseEntity.ok(new UpdateTemplateResponse(templateId));
    }

}
