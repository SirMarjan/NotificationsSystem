package pl.marcinsobanski.notificationssystem.infrastructure.repository;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.marcinsobanski.notificationssystem.infrastructure.repository.converters.StringListToJsonAttributeConverter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TEMPLATE")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class TemplateEntity extends BaseEntity {

    @Column(name = "ID", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "RECIVERS_EMAILS", nullable = false)
    @Convert(converter = StringListToJsonAttributeConverter.class)
    @Builder.Default
    private List<String> receiversEmails = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "TEMPLATE_RULE",
            joinColumns = @JoinColumn(name = "TEMPLATE_ID"),
            inverseJoinColumns = @JoinColumn(name = "RULE_ID")
    )
    @OrderColumn(name = "rule_order")
    @Builder.Default
    private List<RuleEntity> rules = new ArrayList<>();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "COMPILED_RULE_ID")
    private CompiledRuleEntity compiledRuleEntity;

    @Column(name = "CREATION_TIME", nullable = false)
    private Instant creationTime;

}
