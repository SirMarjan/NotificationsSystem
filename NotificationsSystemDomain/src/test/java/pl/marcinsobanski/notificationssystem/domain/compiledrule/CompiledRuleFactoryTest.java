package pl.marcinsobanski.notificationssystem.domain.compiledrule;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.marcinsobanski.notificationssystem.domain.template.ItemType;
import pl.marcinsobanski.notificationssystem.domain.template.rules.ItemTypeRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.PriceRule;
import pl.marcinsobanski.notificationssystem.domain.template.rules.Rule;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledRuleFactoryTest {

    CompiledRuleFactory compiledRuleFactory = new CompiledRuleFactory();

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void calculateCompiledRule(String name, Collection<Rule> input, Optional<CompiledRule> expected) {
        Optional<CompiledRule> result = compiledRuleFactory.calculateCompiledRule(input);

        assertEquals(expected, result);
    }

    static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        "emptyInput",
                        List.of(),
                        Optional.empty()
                ),
                Arguments.of(
                        "twoDifferentItemTypeRules",
                        List.of(
                                new ItemTypeRule(ItemTypeRule.Operator.IS, ItemType.GOLD),
                                new ItemTypeRule(ItemTypeRule.Operator.IS_NOT, ItemType.SILVER)
                        ),
                        Optional.empty()
                ),
                Arguments.of(
                        "ceilAndFloorOverlap",
                        List.of(
                                new PriceRule(PriceRule.Operator.LESS, new BigDecimal("50")),
                                new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("100"))
                        ),
                        Optional.empty()
                ),
                Arguments.of(
                        "ceilAndFloorEqualOverlap",
                        List.of(
                                new PriceRule(PriceRule.Operator.LESS, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.GREATER_OR_EQUAL, new BigDecimal("100"))
                        ),
                        Optional.empty()
                ),
                Arguments.of(
                        "ceilAndFloorEqualOverlap",
                        List.of(
                                new PriceRule(PriceRule.Operator.LESS_OR_EQUAL, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("100"))
                        ),
                        Optional.empty()
                ),
                Arguments.of(
                        "ceilAndFloorEqualOverlap",
                        List.of(
                                new PriceRule(PriceRule.Operator.LESS, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("100"))
                        ),
                        Optional.empty()
                ),
                // SUCCESSES
                Arguments.of(
                        "oneItemTypeRule",
                        List.of(
                                new ItemTypeRule(ItemTypeRule.Operator.IS, ItemType.GOLD)
                        ),
                        Optional.of(
                                CompiledRule.builder()
                                        .itemType(ItemType.GOLD)
                                        .itemTypeEqual(true)
                                        .ceilLimit(null)
                                        .ceilLimitIncluding(false)
                                        .floorLimit(null)
                                        .floorLimitIncluding(false)
                                        .build()
                        )
                ),
                Arguments.of(
                        "twoSameItemTypeRules",
                        List.of(
                                new ItemTypeRule(ItemTypeRule.Operator.IS_NOT, ItemType.GOLD),
                                new ItemTypeRule(ItemTypeRule.Operator.IS_NOT, ItemType.GOLD)
                        ),
                        Optional.of(
                                CompiledRule.builder()
                                        .itemType(ItemType.GOLD)
                                        .itemTypeEqual(false)
                                        .ceilLimit(null)
                                        .ceilLimitIncluding(false)
                                        .floorLimit(null)
                                        .floorLimitIncluding(false)
                                        .build()
                        )
                ),
                Arguments.of(
                        "twoLessTwoGreaterPriceCompiled",
                        List.of(
                                new PriceRule(PriceRule.Operator.LESS, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.LESS, new BigDecimal("200")),
                                new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("50")),
                                new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("30"))
                        ),
                        Optional.of(
                                CompiledRule.builder()
                                        .itemType(null)
                                        .itemTypeEqual(false)
                                        .ceilLimit(new BigDecimal("100"))
                                        .ceilLimitIncluding(false)
                                        .floorLimit(new BigDecimal("50"))
                                        .floorLimitIncluding(false)
                                        .build()
                        )
                ),
                Arguments.of(
                        "ceilAndFloorEqualOverlapWithEqual",
                        List.of(
                                new PriceRule(PriceRule.Operator.LESS_OR_EQUAL, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.GREATER_OR_EQUAL, new BigDecimal("100"))
                        ),
                        Optional.of(
                                CompiledRule.builder()
                                        .itemType(null)
                                        .itemTypeEqual(false)
                                        .ceilLimit(new BigDecimal("100"))
                                        .ceilLimitIncluding(true)
                                        .floorLimit(new BigDecimal("100"))
                                        .floorLimitIncluding(true)
                                        .build()
                        )
                ),
                Arguments.of(
                        "priceRuleLessAndLessEqualCompiled",
                        List.of(
                                new PriceRule(PriceRule.Operator.LESS, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.LESS_OR_EQUAL, new BigDecimal("100"))
                        ),
                        Optional.of(
                                CompiledRule.builder()
                                        .itemType(null)
                                        .itemTypeEqual(false)
                                        .ceilLimit(new BigDecimal("100"))
                                        .ceilLimitIncluding(false)
                                        .floorLimit(null)
                                        .floorLimitIncluding(false)
                                        .build()
                        )
                ),
                Arguments.of(
                        "priceRuleGreaterAndGreaterEqualCompiled",
                        List.of(
                                new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.GREATER_OR_EQUAL, new BigDecimal("100"))
                        ),
                        Optional.of(
                                CompiledRule.builder()
                                        .itemType(null)
                                        .itemTypeEqual(false)
                                        .ceilLimit(null)
                                        .ceilLimitIncluding(false)
                                        .floorLimit(new BigDecimal("100"))
                                        .floorLimitIncluding(true)
                                        .build()
                        )
                ),
                Arguments.of(
                        "complexTest",
                        List.of(
                                new ItemTypeRule(ItemTypeRule.Operator.IS_NOT, ItemType.SILVER),
                                new PriceRule(PriceRule.Operator.LESS_OR_EQUAL, new BigDecimal("200")),
                                new PriceRule(PriceRule.Operator.LESS, new BigDecimal("100")),
                                new PriceRule(PriceRule.Operator.GREATER_OR_EQUAL, new BigDecimal("50")),
                                new PriceRule(PriceRule.Operator.GREATER, new BigDecimal("30"))
                        ),
                        Optional.of(
                                CompiledRule.builder()
                                        .itemType(ItemType.SILVER)
                                        .itemTypeEqual(false)
                                        .ceilLimit(new BigDecimal("100"))
                                        .ceilLimitIncluding(false)
                                        .floorLimit(new BigDecimal("50"))
                                        .floorLimitIncluding(true)
                                        .build()
                        )
                )
        );
    }
}