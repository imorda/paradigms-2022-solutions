package expression.generic;

public abstract class AbstractExpressionNumber<T extends ExpressionNumber<T, R>, R> implements ExpressionNumber<T, R> {
    final R value;

    public AbstractExpressionNumber(R value) {
        this.value = value;
    }

    @Override
    public R getValue() {
        return value;
    }
}
