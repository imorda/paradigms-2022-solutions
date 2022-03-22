package expression.generic;

import java.math.BigInteger;

public final class BigIntegerT extends AbstractExpressionNumber<BigIntegerT, BigInteger> {
    public BigIntegerT(BigInteger value) {
        super(value);
    }

    public BigIntegerT(final int value) {
        super(BigInteger.valueOf(value));
    }

    public static BigIntegerT parseFromString(String number) throws NumberFormatException {
        return new BigIntegerT(new BigInteger(number));
    }

    @Override
    public BigIntegerT add(BigIntegerT other) throws ArithmeticException {
        return new BigIntegerT(value.add(other.value));
    }

    @Override
    public BigIntegerT subtract(BigIntegerT other) throws ArithmeticException {
        return new BigIntegerT(value.subtract(other.value));
    }

    @Override
    public BigIntegerT multiply(BigIntegerT other) throws ArithmeticException {
        return new BigIntegerT(value.multiply(other.value));
    }

    @Override
    public BigIntegerT divide(BigIntegerT other) throws ArithmeticException {
        return new BigIntegerT(value.divide(other.value));
    }

    @Override
    public BigIntegerT min(BigIntegerT other) throws ArithmeticException {
        return new BigIntegerT(value.min(other.value));
    }

    @Override
    public BigIntegerT max(BigIntegerT other) throws ArithmeticException {
        return new BigIntegerT(value.max(other.value));
    }

    @Override
    public BigIntegerT negate() throws ArithmeticException {
        return new BigIntegerT(value.negate());
    }

    @Override
    public BigIntegerT count() throws ArithmeticException {
        return new BigIntegerT(BigInteger.valueOf(value.bitCount()));
    }
}
