package expression;

import java.util.Objects;

public abstract class UnaryOperation extends PriorityExpression {
    protected final PriorityExpression exp;

    public UnaryOperation(PriorityExpression exp) {
        this.exp = Objects.requireNonNull(exp);
    }

    protected abstract String getUnaryOperationSymbol();

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;

        if (that != null) {
            if (this.getClass() == that.getClass()) {
                return this.exp.equals(((UnaryOperation) that).exp);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(exp, this.getClass());
    }

    @Override
    protected void serializeString(StringBuilder sb) {
        sb.append(getUnaryOperationSymbol());
        exp.serializeString(sb);
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        if (exp.getPriority() <= getPriority()) {
            sb.append(getUnaryOperationSymbol()).append(' ');
            exp.serializeMini(sb);
        } else {
            sb.append(getUnaryOperationSymbol()).append('(');
            exp.serializeMini(sb);
            sb.append(')');
        }
    }
}