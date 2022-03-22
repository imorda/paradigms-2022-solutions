package expression.generic;

public final class IntT extends AbstractExpressionNumber<IntT, Integer> {
    public IntT(Integer value) {
        super(value);
    }

    public static IntT parseFromString(String number) throws NumberFormatException {
        return new IntT(Integer.parseInt(number));
    }

    @Override
    public IntT add(IntT other) throws ArithmeticException {
        return new IntT(value + other.value);
    }

    @Override
    public IntT subtract(IntT other) throws ArithmeticException {
        return new IntT(value - other.value);
    }

    @Override
    public IntT multiply(IntT other) throws ArithmeticException {
        return new IntT(value * other.value);
    }

    @Override
    public IntT divide(IntT other) throws ArithmeticException {
        return new IntT(value / other.value);
    }

    @Override
    public IntT min(IntT other) throws ArithmeticException {
        return new IntT(Integer.min(value, other.value));
    }

    @Override
    public IntT max(IntT other) throws ArithmeticException {
        return new IntT(Integer.max(value, other.value));
    }

    @Override
    public IntT negate() throws ArithmeticException {
        return new IntT(-value);
    }

    @Override
    public IntT count() throws ArithmeticException {
        return new IntT(Integer.bitCount(value));
    }
}
