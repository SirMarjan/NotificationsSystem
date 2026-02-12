package pl.marcinsobanski.notificationssystem.api.cqrs.common.models;

public record Rule(
        RuleOperator operator,
        String operand
) {
}