package pl.marcinsobanski.notificationssystem.infrastructure.repository;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "COMPILED_RULE", indexes = {
        @Index(name = "idx_item_type", columnList = "ITEM_TYPE"),
        @Index(name = "idx_ceil_limit", columnList = "CEIL_LIMIT"),
        @Index(name = "idx_floor_limit", columnList = "FLOOR_LIMIT"),
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class CompiledRuleEntity extends BaseEntity {

    @Column(name = "ID", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @Column(name = "ITEM_TYPE")
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @Column(name = "ITEM_TYPE_EQUAL")
    private boolean itemTypeEqual;

    @Column(name = "CEIL_LIMIT")
    private BigDecimal ceilLimit;

    @Column(name = "CEIL_LIMIT_INCLUDING")
    private boolean ceilLimitIncluding;

    @Column(name = "FLOOR_LIMIT")
    private BigDecimal floorLimit;

    @Column(name = "FLOOR_INCLUDING")
    private boolean floorLimitIncluding;

}
