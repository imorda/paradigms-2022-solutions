package expression.generic;

public final class FloatT extends AbstractExpressionNumber<FloatT, Float> {
    public FloatT(Float value) {
        super(value);
    }

    public FloatT(int value) {
        super((float) value);
    }

    public static FloatT parseFromString(String number) throws NumberFormatException {
        return new FloatT(Float.parseFloat(number));
    }

    @Override
    public FloatT add(FloatT other) throws ArithmeticException {
        return new FloatT(value + other.value);
    }

    @Override
    public FloatT subtract(FloatT other) throws ArithmeticException {
        return new FloatT(value - other.value);
    }

    @Override
    public FloatT multiply(FloatT other) throws ArithmeticException {
        return new FloatT(value * other.value);
    }

    @Override
    public FloatT divide(FloatT other) throws ArithmeticException {
        return new FloatT(value / other.value);
    }

    @Override
    public FloatT min(FloatT other) throws ArithmeticException {
        return new FloatT(Float.min(value, other.value));
    }

    @Override
    public FloatT max(FloatT other) throws ArithmeticException {
        return new FloatT(Float.max(value, other.value));
    }

    @Override
    public FloatT negate() throws ArithmeticException {
        return new FloatT(-value);
    }

    @Override
    public FloatT count() throws ArithmeticException {
        return new FloatT((float) Integer.bitCount(Float.floatToIntBits(value)));
    }
}
