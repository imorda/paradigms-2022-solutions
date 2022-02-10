package expression;

public abstract class NonAssociativeOperation extends BinaryOperation {
    public NonAssociativeOperation(PriorityExpression left, PriorityExpression right) {
        super(left, right);
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        serializeMiniBinary(sb, false);
    }
}