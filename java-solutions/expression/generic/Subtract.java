package expression.generic;

public class Subtract<T extends ExpressionNumber<T, ?>> extends NonAssociativeOperation<T> {
    public final static String OPERATION_SYM = "-";

    public Subtract(PriorityExpression<T> left, PriorityExpression<T> right) {
        super(left, right);
    }

    @Override
    protected int getPriority() {
        return 2;
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
    public T evaluate(T x, T y, T z) throws ArithmeticException {
        return left.evaluate(x, y, z).subtract(right.evaluate(x, y, z));
    }
}