package wooteco.subway.domain.fare.strategy.discount;

import org.springframework.stereotype.Component;

@Component
public final class BabyDiscountStrategy extends DiscountStrategy {

    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 5;

    @Override
    boolean isMatch(final int age) {
        return MIN_AGE <= age && age <= MAX_AGE;
    }

    @Override
    public int apply(final int fare) {
        return FREE;
    }
}
