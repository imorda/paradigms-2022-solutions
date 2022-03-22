package expression.generic;

import expression.exceptions.ParseException;

public class ExpressionModeSelector {
    public static ExprIntEvaluator<? extends ExpressionNumber<?, ?>> createEvaluatorWithMode(final String mode, final String expr) throws IllegalStateException, ParseException {
        return switch (mode) {
            // :NOTE: Упростить
            case "i" -> new ExprIntEvaluator<>(new ExpressionParser<>(CheckedIntT::parseFromString).parse(expr), CheckedIntT::new);
            case "d" -> new ExprIntEvaluator<>(new ExpressionParser<>(DoubleT::parseFromString).parse(expr), DoubleT::new);
            case "bi" -> new ExprIntEvaluator<>(new ExpressionParser<>(BigIntegerT::parseFromString).parse(expr), BigIntegerT::new);
            case "u" -> new ExprIntEvaluator<>(new ExpressionParser<>(IntT::parseFromString).parse(expr), IntT::new);
            case "l" -> new ExprIntEvaluator<>(new ExpressionParser<>(LongT::parseFromString).parse(expr), LongT::new);
            case "f" -> new ExprIntEvaluator<>(new ExpressionParser<>(FloatT::parseFromString).parse(expr), FloatT::new);
            default -> throw new IllegalStateException("Invalid mode: " + mode);
        };
    }
}
