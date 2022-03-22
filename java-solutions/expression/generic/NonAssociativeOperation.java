package expression.generic;

public abstract class NonAssociativeOperation<T extends ExpressionNumber<T, ?>> extends BinaryOperation<T> {
    public NonAssociativeOperation(PriorityExpression<T> left, PriorityExpression<T> right) {
        super(left, right);
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        serializeMiniBinary(sb, false);
    }
}