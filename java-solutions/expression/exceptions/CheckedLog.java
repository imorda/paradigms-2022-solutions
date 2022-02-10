package expression.exceptions;

import expression.NonAssociativeOperation;
import expression.PriorityExpression;

import java.math.BigInteger;

public class CheckedLog extends NonAssociativeOperation {
    public final static String OPERATION_SYM = "//";

    public CheckedLog(PriorityExpression left, PriorityExpression right) {
        super(left, right);
    }

    static int evaluateImpl(int a, int b) {
        if (a <= 0 || b <= 0 || b == 1) {
            throw new ArithmeticException(String.format("%d log %d is undefined", a, b));
        }
        if (a < b) {
            return 0;
        }
        return 1 + evaluateImpl(CheckedDivide.divideExact(a, b), b);
    }

    @Override
    protected int getPriority() {
        return 0;
    }

    @Override
    protected int getLocalPriority() {
        return 0;
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