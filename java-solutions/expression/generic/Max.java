package expression.generic;

public final class Max<T extends ExpressionNumber<T, ?>> extends AssociativeOperation<T> {
    public static final String OPERATION_SYM = "max";

    public Max(PriorityExpression<T> left, PriorityExpression<T> right) {
        super(left, right);
    }

    @Override
    protected int getPriority() {
        return 3;
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
    public T evaluate(T x, T y, T z) {
        return left.evaluate(x, y, z).max(right.evaluate(x, y, z));
    }
}