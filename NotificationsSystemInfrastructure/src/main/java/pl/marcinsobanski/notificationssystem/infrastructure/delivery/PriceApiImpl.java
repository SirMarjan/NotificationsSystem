package pl.marcinsobanski.notificationssystem.infrastructure.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.marcinsobanski.notificationssystem.api.cqrs.sendnewprice.SendNewPriceCommand;
import pl.marcinsobanski.notificationssystem.api.endpoint.PriceApi;
import pl.marcinsobanski.notificationssystem.api.model.NewPrice;
import pl.marcinsobanski.notificationssystem.infrastructure.adapters.cqrs.CQCommandHandlerImpl;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class PriceApiImpl implements PriceApi {

    private final CQCommandHandlerImpl cqCommandHandler;

    @Override
    public ResponseEntity<Void> postNewPrice(NewPrice newPrice) {
        cqCommandHandler.executeCommand(new SendNewPriceCommand(switch (newPrice.getItemType()) {
            case GOLD -> SendNewPriceCommand.ItemType.GOLD;
            case SILVER -> SendNewPriceCommand.ItemType.SILVER;
            case PLATINUM -> SendNewPriceCommand.ItemType.PLATINUM;
        }, new BigDecimal(newPrice.getPrice())));
        return ResponseEntity.ok(null);
    }
}