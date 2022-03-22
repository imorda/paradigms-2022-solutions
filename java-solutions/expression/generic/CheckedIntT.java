package expression.generic;

public final class CheckedIntT extends AbstractExpressionNumber<CheckedIntT, Integer> {
    public CheckedIntT(Integer value) {
        super(value);
    }

    public static CheckedIntT parseFromString(String number) throws NumberFormatException {
        return new CheckedIntT(Integer.parseInt(number));
    }

    @Override
    public CheckedIntT add(CheckedIntT other) throws ArithmeticException {
        int b = other.value;
        int c = value + b;
        if (b > 0 && Integer.MAX_VALUE - b < value || b < 0 && Integer.MIN_VALUE - b > value) {
            throw new ArithmeticException(String.format("integer overflow (%d + %d)", value, b));
        }
        return new CheckedIntT(c);
    }

    @Override
    public CheckedIntT subtract(CheckedIntT other) throws ArithmeticException {
        int b = other.value;
        int c = value - b;
        if (b > 0 && Integer.MIN_VALUE + b > value || b < 0 && Integer.MAX_VALUE + b < value) {
            throw new ArithmeticException(String.format("integer overflow (%d - %d)", value, b));
        }
        return new CheckedIntT(c);
    }

    @Override
    public CheckedIntT multiply(CheckedIntT other) throws ArithmeticException {
        int a = value;
        int b = other.value;
        if (a < b) {
            int temp = a;
            a = b;
            b = temp;
        }
        int result = a * b;
        if (b == Integer.MIN_VALUE && a == -1 || a != 0 && result / a != b) {
            throw new ArithmeticException(String.format("integer overflow (%d * %d)", a, b));
        }
        return new CheckedIntT(result);
    }

    @Override
    public CheckedIntT divide(CheckedIntT other) throws ArithmeticException {
        if (value == Integer.MIN_VALUE && other.value == -1) {
            throw new ArithmeticException(String.format("32-bit integer overflow while dividing %d and %d",
                    value, other.value));
        }
        return new CheckedIntT(value / other.value);
    }

    @Override
    public CheckedIntT min(CheckedIntT other) throws ArithmeticException {
        return new CheckedIntT(Integer.min(value, other.value));
    }

    @Override
    public CheckedIntT max(CheckedIntT other) throws ArithmeticException {
        return new CheckedIntT(Integer.max(value, other.value));
    }

    @Override
    public CheckedIntT negate() throws ArithmeticException {
        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException(String.format("integer overflow -(%d)", value));
        }
        return new CheckedIntT(-value);
    }

    @Override
    public CheckedIntT count() throws ArithmeticException {
        return new CheckedIntT(Integer.bitCount(value));
    }
}
