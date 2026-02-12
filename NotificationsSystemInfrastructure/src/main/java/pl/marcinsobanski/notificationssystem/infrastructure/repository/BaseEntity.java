package pl.marcinsobanski.notificationssystem.infrastructure.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @Column(name = "CREATE_DATE", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdDate;

    @Column(name = "LAST_MODIFIED_DATE", nullable = false)
    @LastModifiedDate
    private Instant lastModifiedDate;

    @Column(name = "VERSION", nullable = false)
    @Version
    private long version;

}
