package expression.generic;

public class Multiply<T extends ExpressionNumber<T, ?>> extends AssociativeOperation<T> {
    public final static String OPERATION_SYM = "*";

    public Multiply(PriorityExpression<T> left, PriorityExpression<T> right) {
        super(left, right);
    }

    @Override
    protected int getPriority() {
        return 1;
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
        return left.evaluate(x, y, z).multiply(right.evaluate(x, y, z));
    }
}