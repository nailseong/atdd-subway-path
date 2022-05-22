package wooteco.subway.domain;

import java.util.Objects;

public class Fare {

    private static final int BASIC_VALUE = 1250;
    private static final double TEENAGER_DISCOUNT_RATE = 0.8;
    private static final double CHILD_DISCOUNT_RATE = 0.5;
    private static final int CHILD_AGE_LOWER_BOUND = 6;
    private static final int CHILD_AGE_UPPER_BOUND = 13;
    private static final int TEENAGER_AGE_LOWE_BOUND = 13;
    private static final int TEENAGER_AGE_UPPER_BOUND = 19;
    private static final int DEDUCTIBLE_AMOUNT = 350;
    private static final int DISTANCE_OF_BASIC_FARE = 10;
    private static final int DISTANCE_OF_OVER_FARE = 50;
    private static final int STANDARD_OF_OVER_FARE = 100;
    private static final int STANDARD_DISTANCE_OF_OVER_FARE = 5;
    private static final int MAX_STANDARD_DISTANCE_OF_OVER_FARE = 8;

    private final int value;

    private Fare(final int value) {
        this.value = value;
    }

    public static Fare from(final int extraFare) {
        return new Fare(BASIC_VALUE + extraFare);
    }

    public Fare addByDistance(final Distance distance) {
        if (distance.isLessThanOrEqual(DISTANCE_OF_BASIC_FARE)) {
            return this;
        }
        if (distance.isLessThanOrEqual(DISTANCE_OF_OVER_FARE)) {
            final int extraFare = calculateOverFare(distance.getValue() - DISTANCE_OF_BASIC_FARE,
                    STANDARD_DISTANCE_OF_OVER_FARE);
            return new Fare(value + extraFare);
        }
        final int extraFare = calculateOverFare(DISTANCE_OF_OVER_FARE - DISTANCE_OF_BASIC_FARE,
                STANDARD_DISTANCE_OF_OVER_FARE) +
                calculateOverFare(distance.getValue() - DISTANCE_OF_OVER_FARE, MAX_STANDARD_DISTANCE_OF_OVER_FARE);
        return new Fare(value + extraFare);
    }

    private int calculateOverFare(final int value, final int standardValue) {
        return (int) ((Math.ceil((value - 1) / standardValue) + 1) * STANDARD_OF_OVER_FARE);
    }

    public Fare discountByAge(final int age) {
        if (isChild(age)) {
            return new Fare((int) ((value - DEDUCTIBLE_AMOUNT) * CHILD_DISCOUNT_RATE));
        }
        if (isTeenager(age)) {
            return new Fare((int) ((value - DEDUCTIBLE_AMOUNT) * TEENAGER_DISCOUNT_RATE));
        }
        return this;
    }

    private boolean isChild(final int age) {
        return CHILD_AGE_LOWER_BOUND <= age && age < CHILD_AGE_UPPER_BOUND;
    }

    private boolean isTeenager(final int age) {
        return TEENAGER_AGE_LOWE_BOUND <= age && age < TEENAGER_AGE_UPPER_BOUND;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Fare fare = (Fare) o;
        return value == fare.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Fare{" +
                "value=" + value +
                '}';
    }
}
