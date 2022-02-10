package expression;

public abstract class PriorityExpression implements Expression, TripleExpression, ToMiniString {
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