package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.domain.fare.Fare;
import wooteco.subway.domain.fare.strategy.discount.BabyDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.ChildDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.DefaultDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.DiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.DiscountStrategyFactory;
import wooteco.subway.domain.fare.strategy.discount.SeniorDiscountStrategy;
import wooteco.subway.domain.fare.strategy.discount.TeenagerDiscountStrategy;
import wooteco.subway.domain.fare.strategy.extrafare.DefaultExtraFareStrategy;
import wooteco.subway.domain.fare.strategy.extrafare.ExtraFareStrategy;
import wooteco.subway.domain.fare.strategy.extrafare.ExtraFareStrategyFactory;
import wooteco.subway.domain.fare.strategy.extrafare.FiftyExtraFareStrategy;
import wooteco.subway.domain.fare.strategy.extrafare.TenExtraFareStrategy;

class FareTest {

    @ParameterizedTest
    @DisplayName("거리에 따른 추가 요금이 부과된 요금을 계산한다.")
    @CsvSource(
            value = {"9:1250", "10:1250", "11:1350", "12:1350", "49:2050", "50:2050", "51:2150", "58:2150", "59:2250"},
            delimiter = ':'
    )
    void AddByDistance(final int distanceValue, final int expected) {
        // given
        final Set<ExtraFareStrategy> strategies = Set.of(
                new TenExtraFareStrategy(),
                new FiftyExtraFareStrategy(),
                new DefaultExtraFareStrategy()
        );
        final ExtraFareStrategyFactory strategyFactory = new ExtraFareStrategyFactory(strategies);

        final Distance distance = new Distance(distanceValue);
        final ExtraFareStrategy strategy = strategyFactory.findStrategyBy(distance);

        final Fare fare = Fare.from(0);

        // when
        final Fare actual = fare.addExtraFareByDistance(distance, strategy);

        // then
        assertThat(actual.getValue()).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("나이에 따른 할인된 요금을 계산한다.")
    @CsvSource(
            value = {"1:0", "5:0", "6:1000", "12:1000", "13:1600", "18:1600", "19:2350", "64:2350", "65:0"},
            delimiter = ':'
    )
    void DiscountByAge(final int age, final int expected) {
        // given
        final Set<DiscountStrategy> strategies = Set.of(
                new BabyDiscountStrategy(),
                new ChildDiscountStrategy(),
                new TeenagerDiscountStrategy(),
                new SeniorDiscountStrategy(),
                new DefaultDiscountStrategy()
        );
        final DiscountStrategyFactory strategyFactory = new DiscountStrategyFactory(strategies);
        final DiscountStrategy strategy = strategyFactory.findStrategyBy(age);

        final Fare fare = Fare.from(1100);

        // when
        final Fare actual = fare.discountByAge(strategy);

        // then
        assertThat(actual.getValue()).isEqualTo(expected);
    }
}