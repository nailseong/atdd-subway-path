package wooteco.subway.domain.fare.strategy.discount;

import org.springframework.stereotype.Component;

@Component
public final class TeenagerDiscountStrategy extends DiscountStrategy {

    private static final double DISCOUNT_RATE = 0.8;
    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 18;

    boolean isMatch(final int age) {
        return MIN_AGE <= age && age <= MAX_AGE;
    }

    @Override
    public int apply(final int fare) {
        return discountFareBy(fare, DISCOUNT_RATE);
    }
}
