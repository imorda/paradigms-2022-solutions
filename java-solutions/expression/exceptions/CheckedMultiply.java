package expression.exceptions;

import expression.Multiply;
import expression.PriorityExpression;

import java.math.BigInteger;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(PriorityExpression left, PriorityExpression right) {
        super(left, right);
    }

    static int multiplyExact(int a, int b) {
        if (a < b) {
            int temp = a;
            a = b;
            b = temp;
        }
        int result = a * b;
        if (b == Integer.MIN_VALUE && a == -1 || a != 0 && result / a != b) {
            throw new ArithmeticException(String.format("integer overflow (%d * %d)", a, b));
        }
        return result;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return multiplyExact(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    @Override
    public int evaluate(int x) {
        return multiplyExact(left.evaluate(x), right.evaluate(x));
    }
}