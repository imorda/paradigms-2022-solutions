package expression.exceptions;

import expression.parser.CharSource;

public class ExpressionParseException extends ParseException {
    private final String got;

    public ExpressionParseException(String message, CharSource source, String got) {
        super(message + "; got '" + got + "'", source);
        this.got = got;
    }

    public String getGot() {
        return got;
    }
}
