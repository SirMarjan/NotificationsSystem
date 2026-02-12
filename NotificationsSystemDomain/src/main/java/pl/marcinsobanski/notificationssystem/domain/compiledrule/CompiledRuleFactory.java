package pl.marcinsobanski.notificationssystem.domain.compiledrule;


import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.Rule;

import java.util.*;


public class CompiledRuleFactory {

    public Optional<CompiledRule> calculateCompiledRule(Collection<Rule> rules) {
        if (rules.isEmpty()) {
            return Optional.empty();
        }
        final var itemTypesRules = new HashSet<ItemTypeRule>();
        final var priceCeilFloorRules = new HashSet<PriceRule>();
        final var priceEqualsRule = new HashSet<PriceRule>();

        rules.forEach(
                rule -> {
                    switch (rule) {
                        case ItemTypeRule itemTypeRule -> itemTypesRules.add(itemTypeRule);
                        case PriceRule priceRule -> {
                            switch (priceRule.getOperator()) {
                                case EQUAL -> priceEqualsRule.add(priceRule);
                                case GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EQUAL ->
                                        priceCeilFloorRules.add(priceRule);
                            }
                        }
                    }
                }
        );


        if (checkIsMoreThanOneRule(itemTypesRules) || checkIsMoreThanOneRule(priceEqualsRule)) {
            return Optional.empty();
        }

        final var itemTypeRuleOptional = itemTypesRules.stream().findAny();
        final var equalRuleOptional = priceEqualsRule.stream().findAny();

        final var ceilRuleOptional = findCeilLimitRules(priceCeilFloorRules);
        final var floorRuleOptional = findFloorLimitRules(priceCeilFloorRules);

        if (ceilRuleOptional.isPresent() && floorRuleOptional.isPresent() && checkCeilFloorOverlap(ceilRuleOptional.get(), floorRuleOptional.get())) {
            return Optional.empty();
        }

        if ((ceilRuleOptional.isPresent() || floorRuleOptional.isPresent()) && equalRuleOptional.isPresent() && checkIfEqualRuleInCeilFloorRange(ceilRuleOptional.orElse(null), floorRuleOptional.orElse(null), equalRuleOptional.get())) {
            return Optional.empty();
        }

        final var builder = CompiledRule.builder()
                .ceilLimit(ceilRuleOptional.map(PriceRule::getPrice).orElse(null))
                .ceilLimitIncluding(ceilRuleOptional.map(PriceRule::getOperator).map(PriceRule.Operator.LESS_OR_EQUAL::equals).orElse(false))
                .floorLimit(floorRuleOptional.map(PriceRule::getPrice).orElse(null))
                .floorLimitIncluding(floorRuleOptional.map(PriceRule::getOperator).map(PriceRule.Operator.GREATER_OR_EQUAL::equals).orElse(false));

        itemTypeRuleOptional.ifPresent(
                itemTypeRule -> builder.itemType(itemTypeRule.getItemType())
                        .itemTypeEqual(ItemTypeRule.Operator.IS.equals(itemTypeRule.getOperator()))
        );

        if (equalRuleOptional.isPresent()) {
            final var equalRule = equalRuleOptional.get();
            builder.ceilLimit(equalRule.getPrice())
                    .ceilLimitIncluding(true)
                    .floorLimit(equalRule.getPrice())
                    .floorLimitIncluding(true);
            return Optional.of(builder.build());
        }

        ceilRuleOptional.ifPresent(
                ceilRule -> builder.ceilLimit(ceilRule.getPrice())
                        .ceilLimitIncluding(PriceRule.Operator.LESS_OR_EQUAL.equals(ceilRule.getOperator()))
        );
        floorRuleOptional.ifPresent(
                ceilRule -> builder.floorLimit(ceilRule.getPrice())
                        .floorLimitIncluding(PriceRule.Operator.GREATER_OR_EQUAL.equals(ceilRule.getOperator()))
        );
        return Optional.of(builder.build());
    }


    private boolean checkIsMoreThanOneRule(Set<? extends Rule> rule) {
        return rule.size() > 1;
    }


    private Optional<PriceRule> findCeilLimitRules(Set<PriceRule> priceRules) {
        return priceRules.stream()
                .filter(rule -> rule.getOperator() == PriceRule.Operator.LESS ||
                        rule.getOperator() == PriceRule.Operator.LESS_OR_EQUAL)
                .min(Comparator.comparing(PriceRule::getPrice)
                        .thenComparing(rule -> rule.getOperator() == PriceRule.Operator.LESS_OR_EQUAL ? 1 : 0));
    }

    private Optional<PriceRule> findFloorLimitRules(Set<PriceRule> priceRules) {
        return priceRules.stream()
                .filter(rule -> rule.getOperator() == PriceRule.Operator.GREATER ||
                        rule.getOperator() == PriceRule.Operator.GREATER_OR_EQUAL)
                .max(Comparator.comparing(PriceRule::getPrice)
                        .thenComparing(rule -> rule.getOperator() == PriceRule.Operator.GREATER_OR_EQUAL ? 1 : 0));
    }

    private boolean checkCeilFloorOverlap(PriceRule ceil, PriceRule floor) {
        final var comparison = floor.getPrice().compareTo(ceil.getPrice());

        if (comparison > 0) {
            return true;
        }

        if (comparison == 0) {
            return !(floor.getOperator() == PriceRule.Operator.GREATER_OR_EQUAL &&
                    ceil.getOperator() == PriceRule.Operator.LESS_OR_EQUAL);
        }

        return false;
    }

    private boolean checkIfEqualRuleInCeilFloorRange(PriceRule ceil, PriceRule floor, PriceRule equal) {
        if (ceil != null) {
            final var ceilComparison = equal.getPrice().compareTo(ceil.getPrice());
            return ceilComparison > 0 || ceilComparison == 0 && ceil.getOperator() == PriceRule.Operator.LESS;
        }
        if (floor != null) {
            final var floorComparison = equal.getPrice().compareTo(floor.getPrice());
            return floorComparison < 0 || floorComparison == 0 && floor.getOperator() == PriceRule.Operator.GREATER;
        }
        return false;
    }

}