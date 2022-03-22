package expression.generic;

public final class LongT extends AbstractExpressionNumber<LongT, Long> {
    public LongT(Long value) {
        super(value);
    }

    public LongT(int value) {
        super((long) value);
    }

    public static LongT parseFromString(String number) throws NumberFormatException {
        return new LongT(Long.parseLong(number));
    }

    @Override
    public LongT add(LongT other) throws ArithmeticException {
        return new LongT(value + other.value);
    }

    @Override
    public LongT subtract(LongT other) throws ArithmeticException {
        return new LongT(value - other.value);
    }

    @Override
    public LongT multiply(LongT other) throws ArithmeticException {
        return new LongT(value * other.value);
    }

    @Override
    public LongT divide(LongT other) throws ArithmeticException {
        return new LongT(value / other.value);
    }

    @Override
    public LongT min(LongT other) throws ArithmeticException {
        return new LongT(Long.min(value, other.value));
    }

    @Override
    public LongT max(LongT other) throws ArithmeticException {
        return new LongT(Long.max(value, other.value));
    }

    @Override
    public LongT negate() throws ArithmeticException {
        return new LongT(-value);
    }

    @Override
    public LongT count() throws ArithmeticException {
        return new LongT((long) Long.bitCount(value));
    }
}
