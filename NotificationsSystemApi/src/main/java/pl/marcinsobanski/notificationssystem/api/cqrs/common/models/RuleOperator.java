package pl.marcinsobanski.notificationssystem.api.cqrs.common.models;

public enum RuleOperator {
    ITEM_IS,
    ITEM_IS_NOT,
    PRICE_GREATER,
    PRICE_GREATER_OR_EQUALS,
    PRICE_LESS,
    PRICE_LESS_OR_EQUALS,
    PRICE_EQUALS
}
