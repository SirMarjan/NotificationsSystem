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
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.CQBusImpl;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TemplateApiImpl implements TemplateApi {

    private final CQBusImpl cqCommandHandler;
    private final TemplateDetailsViewToTemplateDetailsConverter templateDetailsViewToTemplateDetailsConverter;
    private final TemplateDetailsToCreateTemplateCommandConverter templateDetailsToCreateTemplateCommandConverter;
    private final TemplateDetailsToReplaceTemplateCommandConverter templateDetailsToReplaceTemplateCommandConverter;

    @Override
    @Transactional
    public ResponseEntity<AddNewTemplateResponse> addNewTemplate(TemplateDetails templateDetails) {
        final var result = cqCommandHandler.executeCommand(templateDetailsToCreateTemplateCommandConverter.convert(templateDetails));
        return ResponseEntity.ok(new AddNewTemplateResponse(result.templateId()));
    }

    @Override
    public ResponseEntity<TemplateDetails> getTemplateDetails(UUID templateId) {
        final var template = cqCommandHandler.executeQuery(new TemplateDetailsQuery(templateId));
        return ResponseEntity.ok(templateDetailsViewToTemplateDetailsConverter.convert(template));
    }

    @Override
    public ResponseEntity<List<TemplateListElement>> getTemplatesList() {
        final var templates = cqCommandHandler.executeQuery(new ListTemplatesQuery());
        return ResponseEntity.ok(templates.templateListElements().stream()
                .map(templateListItem -> new TemplateListElement(templateListItem.id(), templateListItem.title()))
                .toList());
    }

    @Override
    @Transactional
    public ResponseEntity<UpdateTemplateResponse> updateTemplate(UUID templateId, TemplateDetails templateDetails) {
        final var replaceTemplateCommand = templateDetailsToReplaceTemplateCommandConverter.convert(templateDetails, templateId);
        cqCommandHandler.executeCommand(replaceTemplateCommand);
        return ResponseEntity.ok(new UpdateTemplateResponse(templateId));
    }

}
