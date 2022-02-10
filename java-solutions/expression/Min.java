package expression;

import java.math.BigInteger;

public final class Min extends AssociativeOperation {
    public final static String OPERATION_SYM = "min";

    public Min(PriorityExpression left, PriorityExpression right) {
        super(left, right);
    }

    private static int evaluateImpl(int a, int b) {
        return a < b ? a : b;
    }

    @Override
    protected int getPriority() {
        return 3;
    }

    @Override
    protected int getLocalPriority() {
        return 1;
    }

    @Override
    public String getBinaryOperationSymbol() {
        return OPERATION_SYM;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return evaluateImpl(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    @Override
    public int evaluate(int x) {
        return evaluateImpl(left.evaluate(x), right.evaluate(x));
    }
}