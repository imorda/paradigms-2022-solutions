package expression.generic;

public abstract class AssociativeOperation<T extends ExpressionNumber<T, ?>> extends BinaryOperation<T> {
    public AssociativeOperation(PriorityExpression<T> left, PriorityExpression<T> right) {
        super(left, right);
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        serializeMiniBinary(sb, true);
    }
}