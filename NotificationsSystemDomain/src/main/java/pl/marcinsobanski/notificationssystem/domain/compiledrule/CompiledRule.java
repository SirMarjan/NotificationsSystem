package pl.marcinsobanski.notificationssystem.domain.compiledrule;

import lombok.Builder;
import lombok.Data;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;

import java.math.BigDecimal;

@Data
@Builder
public class CompiledRule {
    private ItemType itemType;
    private boolean itemTypeEqual;
    private BigDecimal ceilLimit;
    private boolean ceilLimitIncluding;
    private BigDecimal floorLimit;
    private boolean floorLimitIncluding;

}
