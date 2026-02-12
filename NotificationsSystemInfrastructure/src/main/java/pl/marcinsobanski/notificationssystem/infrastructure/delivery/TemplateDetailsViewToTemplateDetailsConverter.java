package pl.marcinsobanski.notificationssystem.infrastructure.delivery;

import org.mapstruct.Mapper;
import pl.marcinsobanski.notificationssystem.api.cqrs.templatedetails.TemplateDetailsView;
import pl.marcinsobanski.notificationssystem.api.model.TemplateDetails;
import pl.marcinsobanski.notificationssystem.infrastructure.commons.SpringMapperConfig;

@Mapper(config = SpringMapperConfig.class)
public interface TemplateDetailsViewToTemplateDetailsConverter {

    TemplateDetails convert(TemplateDetailsView templateDetailsView);

}
