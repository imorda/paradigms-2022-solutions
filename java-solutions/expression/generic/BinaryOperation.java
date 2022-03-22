package expression.generic;

import java.util.Objects;

public abstract class BinaryOperation<T extends ExpressionNumber<T, ?>> extends PriorityExpression<T> {
    protected final PriorityExpression<T> left;
    protected final PriorityExpression<T> right;

    public BinaryOperation(PriorityExpression<T> left, PriorityExpression<T> right) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
    }

    protected static void appendOptionallyWithBrackets(StringBuilder sb, PriorityExpression<?> data, boolean brackets) {
        if (brackets) {
            sb.append('(');
        }

        data.serializeMini(sb);

        if (brackets) {
            sb.append(')');
        }
    }

    protected abstract String getBinaryOperationSymbol();

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;

        if (that != null) {
            if (this.getClass() == that.getClass()) {
                return this.left.equals(((BinaryOperation<?>) that).left)
                        && this.right.equals(((BinaryOperation<?>) that).right);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, this.getClass());
    }

    @Override
    protected void serializeString(StringBuilder sb) {
        sb.append('(');
        left.serializeString(sb);
        sb.append(' ').append(getBinaryOperationSymbol()).append(' ');
        right.serializeString(sb);
        sb.append(')');
    }

    protected void serializeMiniBinary(StringBuilder sb, boolean isAssociative) {
        appendOptionallyWithBrackets(sb, left, left.getPriority() > getPriority());
        sb.append(' ').append(getBinaryOperationSymbol()).append(' ');
        boolean rightPriority = right.getPriority() > getPriority()
                || (!isAssociative && right.getPriority() == getPriority());
        appendOptionallyWithBrackets(sb, right, rightPriority
                || right.getPriority() == getPriority() && right.getLocalPriority() != getLocalPriority());
    }
}