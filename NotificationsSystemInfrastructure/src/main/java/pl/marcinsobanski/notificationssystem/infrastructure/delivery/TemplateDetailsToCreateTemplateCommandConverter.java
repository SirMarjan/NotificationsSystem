package pl.marcinsobanski.notificationssystem.infrastructure.delivery;

import org.mapstruct.Mapper;
import pl.marcinsobanski.notificationssystem.api.cqrs.createtemplate.CreateTemplateCommand;
import pl.marcinsobanski.notificationssystem.api.model.TemplateDetails;
import pl.marcinsobanski.notificationssystem.infrastructure.commons.SpringMapperConfig;

@Mapper(config = SpringMapperConfig.class)
public interface TemplateDetailsToCreateTemplateCommandConverter {

    CreateTemplateCommand convert(TemplateDetails templateDetails);

}
