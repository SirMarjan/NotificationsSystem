package pl.marcinsobanski.notificationssystem.infrastructure.delivery;

import org.mapstruct.Mapper;
import pl.marcinsobanski.notificationssystem.api.cqrs.replacetemplate.ReplaceTemplateCommand;
import pl.marcinsobanski.notificationssystem.api.model.TemplateDetails;
import pl.marcinsobanski.notificationssystem.infrastructure.commons.SpringMapperConfig;

import java.util.UUID;

@Mapper(config = SpringMapperConfig.class)
public interface TemplateDetailsToReplaceTemplateCommandConverter {

    ReplaceTemplateCommand convert(TemplateDetails templateDetails, UUID id);

}
