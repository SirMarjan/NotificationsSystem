package pl.marcinsobanski.notificationssystem.domain.template.rules;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ItemTypeRule extends Rule {

    private Operator operator;
    private ItemType itemType;

    public ItemTypeRule(Operator operator, ItemType itemType) {
        this.operator = operator;
        this.itemType = itemType;
    }

    public enum Operator {
        IS, IS_NOT
    }

}
