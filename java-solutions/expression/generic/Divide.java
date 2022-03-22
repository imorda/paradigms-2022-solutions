package expression.generic;

public class Divide<T extends ExpressionNumber<T, ?>> extends NonAssociativeOperation<T> {
    public final static String OPERATION_SYM = "/";

    public Divide(PriorityExpression<T> left, PriorityExpression<T> right) {
        super(left, right);
    }

    @Override
    protected int getPriority() {
        return 1;
    }

    @Override
    protected int getLocalPriority() {
        return -1;
    }

    @Override
    public String getBinaryOperationSymbol() {
        return OPERATION_SYM;
    }

    @Override
    public T evaluate(T x, T y, T z) throws ArithmeticException {
        return left.evaluate(x, y, z).divide(right.evaluate(x, y, z));
    }
}