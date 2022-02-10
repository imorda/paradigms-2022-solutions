package expression;

public abstract class AssociativeOperation extends BinaryOperation {
    public AssociativeOperation(PriorityExpression left, PriorityExpression right) {
        super(left, right);
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        serializeMiniBinary(sb, true);
    }
}