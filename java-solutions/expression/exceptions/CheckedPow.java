package expression.exceptions;

import expression.NonAssociativeOperation;
import expression.PriorityExpression;

import java.math.BigInteger;

public class CheckedPow extends NonAssociativeOperation {
    public static final String OPERATION_SYM = "**";

    public CheckedPow(PriorityExpression left, PriorityExpression right) {
        super(left, right);
    }

    static int evaluateImpl(int a, int b) {
        if (b == 1) {
            return a;
        }
        if (b <= 0) {
            if (a == 0 || b < 0) {
                throw new ArithmeticException(String.format("%d pow %d is undefined", a, b));
            }
            return 1;
        }
        if (b % 2 == 0) {
            int sqrtA = evaluateImpl(a, b / 2);
            return CheckedMultiply.multiplyExact(sqrtA, sqrtA);
        }
        return CheckedMultiply.multiplyExact(evaluateImpl(a, b - 1), a);
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