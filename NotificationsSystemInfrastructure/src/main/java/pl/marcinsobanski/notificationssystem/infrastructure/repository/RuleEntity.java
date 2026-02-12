package pl.marcinsobanski.notificationssystem.infrastructure.repository;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "RULE")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class RuleEntity extends BaseEntity {

    @Column(name = "ID", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @Column(name = "RULE_OPERATOR", updatable = false)
    @Enumerated(EnumType.STRING)
    private RuleOperator ruleOperator;

    @Column(name = "ITEM_TYPE_OPERAND", updatable = false)
    @Enumerated(EnumType.STRING)
    private ItemType itemTypeOperand;

    @Column(name = "PRICE_OPERAND", updatable = false)
    private BigDecimal priceOperand;

}
