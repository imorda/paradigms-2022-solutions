package expression.generic;

public final class Min<T extends ExpressionNumber<T, ?>> extends AssociativeOperation<T> {
    public final static String OPERATION_SYM = "min";

    public Min(PriorityExpression<T> left, PriorityExpression<T> right) {
        super(left, right);
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
    public T evaluate(T x, T y, T z) {
        return left.evaluate(x, y, z).min(right.evaluate(x, y, z));
    }
}