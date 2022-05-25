package wooteco.subway.domain.fare.strategy.extrafare;

import org.springframework.stereotype.Component;
import wooteco.subway.domain.Distance;

@Component
public final class DefaultExtraFareStrategy extends ExtraFareStrategy {

    private static final int MAX_DISTANCE = 10;

    @Override
    boolean isMatch(final int distance) {
        return distance <= MAX_DISTANCE;
    }

    @Override
    public int apply(final Distance distance) {
        return 0;
    }
}
