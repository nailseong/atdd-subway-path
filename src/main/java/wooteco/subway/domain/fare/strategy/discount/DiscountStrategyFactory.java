package wooteco.subway.domain.fare.strategy.discount;

import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class DiscountStrategyFactory {

    private final Set<DiscountStrategy> strategies;

    public DiscountStrategyFactory(final Set<DiscountStrategy> strategies) {
        this.strategies = strategies;
    }

    public DiscountStrategy findStrategyBy(final int age) {
        return strategies
                .stream()
                .filter(it -> it.isMatch(age))
                .findFirst()
                .orElseThrow();
    }
}
