package wooteco.subway.domain.fare.strategy.extrafare;

import org.springframework.stereotype.Component;
import wooteco.subway.domain.Distance;

@Component
public final class FiftyExtraFareStrategy extends ExtraFareStrategy {

    private static final int MIN_DISTANCE = 51;
    private static final int DISTANCE_OF_EXTRA_FARE = 50;
    private static final int STANDARD_DISTANCE_FOR_EXTRA_FARE = 8;
    private static final int BASIC_EXTRA_FARE = 800;

    @Override
    boolean isMatch(final int distance) {
        return MIN_DISTANCE <= distance;
    }

    @Override
    public int apply(final Distance distance) {
        final int extraFare = calculateExtraFare(distance, DISTANCE_OF_EXTRA_FARE, STANDARD_DISTANCE_FOR_EXTRA_FARE);
        return BASIC_EXTRA_FARE + extraFare;
    }
}
