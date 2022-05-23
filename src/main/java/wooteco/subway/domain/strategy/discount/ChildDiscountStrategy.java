package wooteco.subway.domain.strategy.discount;

public class ChildDiscountStrategy implements DiscountStrategy {

    private static final DiscountStrategy INSTANCE = new ChildDiscountStrategy();

    private static final int DEDUCTIBLE_AMOUNT = 350;
    private static final double DISCOUNT_RATE = 0.5;

    private ChildDiscountStrategy() {
    }

    public static DiscountStrategy getInstance() {
        return INSTANCE;
    }

    @Override
    public int calculateDiscountedAmount(final int fare) {
        return (int) ((fare - DEDUCTIBLE_AMOUNT) * DISCOUNT_RATE);
    }
}