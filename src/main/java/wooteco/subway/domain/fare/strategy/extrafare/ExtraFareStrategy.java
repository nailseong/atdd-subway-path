package wooteco.subway.domain.fare.strategy.extrafare;

import wooteco.subway.domain.Distance;

public interface ExtraFareStrategy {

    int calculate(final Distance distance);
}