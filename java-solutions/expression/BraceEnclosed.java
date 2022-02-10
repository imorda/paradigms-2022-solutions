package expression;

import java.math.BigInteger;

public final class BraceEnclosed extends UnaryOperation {
    public BraceEnclosed(PriorityExpression exp) {
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
    public int evaluate(int x, int y, int z) {
        return exp.evaluate(x, y, z);
    }

    @Override
    public int evaluate(int x) {
        return exp.evaluate(x);
    }
}