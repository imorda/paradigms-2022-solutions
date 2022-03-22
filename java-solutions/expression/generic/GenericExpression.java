package expression.generic;

public interface GenericExpression<T extends ExpressionNumber<?, ?>> {
    T evaluate(T x, T y, T z) throws ArithmeticException;
}
