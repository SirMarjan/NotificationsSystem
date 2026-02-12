package pl.marcinsobanski.notificationssystem.domain.template.rules;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public abstract sealed class Rule permits ItemTypeRule, PriceRule {
}