package expression;

public abstract class Operand extends PriorityExpression {
    public Operand() {
        super();
    }

    @Override
    protected int getPriority() {
        return -1;
    }

    @Override
    protected int getLocalPriority() {
        return 0;
    }
}