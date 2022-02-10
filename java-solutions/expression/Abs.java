package expression;

import java.math.BigInteger;

public class Abs extends UnaryOperation {
    public final static String OPERATION_SYM = "abs";

    public Abs(PriorityExpression exp) {
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

    protected int evaluateImpl(int value) {
        return value < 0 ? -value : value;
    }
}