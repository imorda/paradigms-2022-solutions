package expression.exceptions;

import expression.parser.CharSource;

public final class InvalidOperandException extends ExpressionParseException {
    public InvalidOperandException(CharSource source, String got) {
        super("expected valid operand", source, got);
    }
}
