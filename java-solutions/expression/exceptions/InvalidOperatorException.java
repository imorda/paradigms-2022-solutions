package expression.exceptions;

import expression.parser.CharSource;

public final class InvalidOperatorException extends ExpressionParseException {
    public InvalidOperatorException(CharSource source, String got) {
        super("expected valid operator", source, got);
    }
}
