package wooteco.subway.domain.fare.strategy.extrafare;

import java.util.Set;
import org.springframework.stereotype.Component;
import wooteco.subway.domain.Distance;

@Component
public class ExtraFareStrategyFactory {

    private final Set<ExtraFareStrategy> strategies;

    public ExtraFareStrategyFactory(
            final Set<ExtraFareStrategy> strategies) {
        this.strategies = strategies;
    }

    public ExtraFareStrategy findStrategyBy(final Distance distance) {
        return strategies
                .stream()
                .filter(it -> it.isMatch(distance.getValue()))
                .findFirst()
                .orElseThrow();
    }
}
