package expression.generic;

public final class BraceEnclosed<T extends ExpressionNumber<T, ?>> extends UnaryOperation<T> {
    public BraceEnclosed(PriorityExpression<T> exp) {
        super(exp);
    }

    @Override
    public String toMiniString() {
        return exp.toMiniString();
    }

    @Override
    protected int getPriority() {
        return exp.getPriority();
    }

    @Override
    protected int getLocalPriority() {
        return 0;
    }

    @Override
    protected void serializeString(StringBuilder sb) {
        sb.append('(');
        exp.serializeString(sb);
        sb.append(')');
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        exp.serializeMini(sb);
    }

    @Override
    public String getUnaryOperationSymbol() {
        return "";
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return exp.evaluate(x, y, z);
    }
}