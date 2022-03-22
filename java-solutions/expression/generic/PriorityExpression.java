package expression.generic;

import expression.ToMiniString;

public abstract class PriorityExpression<T extends ExpressionNumber<T, ?>> implements GenericExpression<T>, ToMiniString {
    public PriorityExpression() {
    }

    protected abstract int getPriority();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        serializeString(sb);
        return sb.toString();
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();
        serializeMini(sb);
        return sb.toString();
    }

    protected abstract int getLocalPriority();

    protected abstract void serializeMini(StringBuilder sb);

    protected abstract void serializeString(StringBuilder sb);
}