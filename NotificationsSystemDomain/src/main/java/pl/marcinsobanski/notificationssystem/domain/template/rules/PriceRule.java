package pl.marcinsobanski.notificationssystem.domain.template.rules;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public final class PriceRule extends Rule {

    private Operator operator;
    private BigDecimal price;

    public PriceRule(Operator operator, BigDecimal price) {
        this.operator = operator;
        this.price = price;
    }

    public enum Operator {
        EQUAL, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL
    }

}
