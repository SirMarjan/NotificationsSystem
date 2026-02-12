package pl.marcinsobanski.notificationssystem.api.cqrs.sendnewprice;

import pl.marcinsobanski.notificationssystem.api.cqrs.common.cqrs.Command;

import java.math.BigDecimal;

public record SendNewPriceCommand(
        ItemType itemType,
        BigDecimal price
) implements Command<Void> {

    public enum ItemType {
        GOLD, SILVER, PLATINUM
    }

}
