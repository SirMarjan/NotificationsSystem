package pl.marcinsobanski.notificationssystem.application.sendnewprice;

import lombok.RequiredArgsConstructor;
import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.CommandHandler;
import pl.marcinsobanski.notificationssystem.api.cqrs.sendnewprice.SendNewPriceCommand;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.domain.template.TemplateRepository;

import java.util.Set;

@RequiredArgsConstructor
public class SendNewPriceCommandHandler implements CommandHandler<Void, SendNewPriceCommand> {

    private final TemplateRepository templateRepository;
    private final EmailSender emailSender;

    @Override
    public Void handle(SendNewPriceCommand command) {
        templateRepository.findTemplatesMatchingRules(convertItemType(command.itemType()), command.price())
                .forEach(template -> emailSender.send(Set.copyOf(template.getReceiversEmails()), template.getTitle(), template.getContent()));
        return null;
    }

    private ItemType convertItemType(SendNewPriceCommand.ItemType itemType) {
        return switch (itemType) {
            case GOLD -> ItemType.GOLD;
            case SILVER -> ItemType.SILVER;
            case PLATINUM -> ItemType.PLATINUM;
        };
    }

}