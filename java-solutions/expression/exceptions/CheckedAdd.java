package expression.exceptions;

import expression.Add;
import expression.PriorityExpression;

import java.math.BigInteger;

public class CheckedAdd extends Add {
    public CheckedAdd(PriorityExpression left, PriorityExpression right) {
        super(left, right);
    }

    static int addExact(int a, int b) {
        int c = a + b;
        if (b > 0 && Integer.MAX_VALUE - b < a || b < 0 && Integer.MIN_VALUE - b > a) {
            throw new ArithmeticException(String.format("integer overflow (%d + %d)", a, b));
        }
        return c;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return addExact(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    @Override
    public int evaluate(int x) {
        return addExact(left.evaluate(x), right.evaluate(x));
    }
}