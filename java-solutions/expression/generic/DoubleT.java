package expression.generic;

public final class DoubleT extends AbstractExpressionNumber<DoubleT, Double> {
    public DoubleT(Double value) {
        super(value);
    }

    public DoubleT(int value) {
        super((double) value);
    }

    public static DoubleT parseFromString(String number) throws NumberFormatException {
        return new DoubleT(Double.parseDouble(number));
    }

    @Override
    public DoubleT add(DoubleT other) throws ArithmeticException {
        return new DoubleT(value + other.value);
    }

    @Override
    public DoubleT subtract(DoubleT other) throws ArithmeticException {
        return new DoubleT(value - other.value);
    }

    @Override
    public DoubleT multiply(DoubleT other) throws ArithmeticException {
        return new DoubleT(value * other.value);
    }

    @Override
    public DoubleT divide(DoubleT other) throws ArithmeticException {
        return new DoubleT(value / other.value);
    }

    @Override
    public DoubleT min(DoubleT other) throws ArithmeticException {
        return new DoubleT(Double.min(value, other.value));
    }

    @Override
    public DoubleT max(DoubleT other) throws ArithmeticException {
        return new DoubleT(Double.max(value, other.value));
    }

    @Override
    public DoubleT negate() throws ArithmeticException {
        return new DoubleT(-value);
    }

    @Override
    public DoubleT count() throws ArithmeticException {
        return new DoubleT((double) Long.bitCount(Double.doubleToLongBits(value)));
    }
}
