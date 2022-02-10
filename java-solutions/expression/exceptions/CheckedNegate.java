package expression.exceptions;

import expression.Negate;
import expression.PriorityExpression;

import java.math.BigInteger;

public class CheckedNegate extends Negate {
    public CheckedNegate(PriorityExpression exp) {
        super(exp);
    }

    static int negateExact(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException(String.format("integer overflow (-%d)", value));
        }
        return -value;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return negateExact(exp.evaluate(x, y, z));
    }

    @Override
    public int evaluate(int x) {
        return negateExact(exp.evaluate(x));
    }
}