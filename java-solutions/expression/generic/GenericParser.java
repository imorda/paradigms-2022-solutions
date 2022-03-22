package expression.generic;

import expression.exceptions.ParseException;

public interface GenericParser<T extends ExpressionNumber<T, ?>> {
    GenericExpression<T> parse(String expression) throws ParseException;
}
