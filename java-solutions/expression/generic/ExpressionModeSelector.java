package expression.generic;

import expression.exceptions.ParseException;

import java.util.function.Function;

public class ExpressionModeSelector {
    public static ExprIntEvaluator<? extends ExpressionNumber<?, ?>> createEvaluatorWithMode(final String mode, final String expr) throws IllegalStateException, ParseException {
        return switch (mode) {
            case "i" -> constructEvaluator(expr, CheckedIntT::parseFromString, CheckedIntT::new);
            case "d" -> constructEvaluator(expr, DoubleT::parseFromString, DoubleT::new);
            case "bi" -> constructEvaluator(expr, BigIntegerT::parseFromString, BigIntegerT::new);
            case "u" -> constructEvaluator(expr, IntT::parseFromString, IntT::new);
            case "l" -> constructEvaluator(expr, LongT::parseFromString, LongT::new);
            case "f" -> constructEvaluator(expr, FloatT::parseFromString, FloatT::new);
            default -> throw new IllegalStateException("Invalid mode: " + mode);
        };
    }

    private static <T extends ExpressionNumber<T, ?>> ExprIntEvaluator<T> constructEvaluator(
            final String expr,
            Function<String, T> strToNumConverter,
            Function<Integer, T> numObjFactory) throws ParseException {
        return new ExprIntEvaluator<>(new ExpressionParser<>(strToNumConverter).parse(expr), numObjFactory);
    }
}
