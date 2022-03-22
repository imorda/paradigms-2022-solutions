package expression.generic;

public class Negate<T extends ExpressionNumber<T, ?>> extends UnaryOperation<T> {
    public final static String OPERATION_SYM = "-";

    public Negate(PriorityExpression<T> exp) {
        super(exp);
    }

    @Override
    protected int getPriority() {
        return -1;
    }

    @Override
    protected int getLocalPriority() {
        return 0;
    }

    @Override
    public String getUnaryOperationSymbol() {
        return OPERATION_SYM;
    }

    @Override
    public T evaluate(T x, T y, T z) throws ArithmeticException {
        return exp.evaluate(x, y, z).negate();
    }
}