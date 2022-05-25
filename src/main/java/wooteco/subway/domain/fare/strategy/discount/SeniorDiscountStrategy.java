package wooteco.subway.domain.fare.strategy.discount;

import org.springframework.stereotype.Component;

@Component
public final class SeniorDiscountStrategy extends DiscountStrategy {

    private static final int MIN_AGE = 65;

    @Override
    boolean isMatch(final int age) {
        return MIN_AGE <= age;
    }

    @Override
    public int apply(final int fare) {
        return FREE;
    }
}
