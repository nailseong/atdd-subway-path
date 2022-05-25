package wooteco.subway.domain.fare.strategy.discount;

import org.springframework.stereotype.Component;

@Component
public final class DefaultDiscountStrategy extends DiscountStrategy {

    private static final int MIN_AGE = 19;
    private static final int MAX_AGE = 64;

    boolean isMatch(final int age) {
        return MIN_AGE <= age && age <= MAX_AGE;
    }

    @Override
    public int apply(final int fare) {
        return fare;
    }
}
