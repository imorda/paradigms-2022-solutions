package expression.generic;

import java.util.function.Function;

public class ExprIntEvaluator<T extends ExpressionNumber<T, ?>> {
    final GenericExpression<T> parsed;
    final Function<Integer, T> intToTFactory;

    public ExprIntEvaluator(GenericExpression<T> parsed, Function<Integer, T> intToTFactory) {
        this.parsed = parsed;
        this.intToTFactory = intToTFactory;
    }

    public T evaluate(int x, int y, int z) {
        return parsed.evaluate(intToTFactory.apply(x), intToTFactory.apply(y), intToTFactory.apply(z));
    }
}
