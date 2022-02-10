package expression.exceptions;

import expression.Abs;
import expression.PriorityExpression;

import java.math.BigInteger;

public final class CheckedAbs extends Abs {
    public final static String OPERATION_SYM = "abs";

    public CheckedAbs(PriorityExpression exp) {
        super(exp);
    }

    @Override
    protected int getPriority() {
        return -1;
    }

    @Override
    protected int getLocalPriority() {
        return 0;
    }

    @Override
    public String getUnaryOperationSymbol() {
        return OPERATION_SYM;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return evaluateImpl(exp.evaluate(x, y, z));
    }

    @Override
    public int evaluate(int x) {
        return evaluateImpl(exp.evaluate(x));
    }

    @Override
    protected int evaluateImpl(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new ArithmeticException(String.format("integer overflow abs(%d)", value));
        }
        return super.evaluateImpl(value);
    }
}