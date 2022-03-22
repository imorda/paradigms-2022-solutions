package expression.generic;

public abstract class Operand<T extends ExpressionNumber<T, ?>> extends PriorityExpression<T> {
    public Operand() {
        super();
    }

    @Override
    protected int getPriority() {
        return -1;
    }

    @Override
    protected int getLocalPriority() {
        return 0;
    }
}