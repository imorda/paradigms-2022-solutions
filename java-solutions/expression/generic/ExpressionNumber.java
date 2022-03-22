package expression.generic;

public interface ExpressionNumber<T extends ExpressionNumber<T, R>, R> {
    T min(T other) throws ArithmeticException;

    T max(T other) throws ArithmeticException;

    T negate() throws ArithmeticException;

    T count() throws ArithmeticException;

    T add(T other) throws ArithmeticException;

    T subtract(T other) throws ArithmeticException;

    T multiply(T other) throws ArithmeticException;

    T divide(T other) throws ArithmeticException;

    R getValue();
}
