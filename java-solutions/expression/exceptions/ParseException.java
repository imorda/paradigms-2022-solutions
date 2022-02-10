package expression.exceptions;

import expression.parser.CharSource;

public class ParseException extends Exception {
    private final CharSource source;

    public ParseException(String message, CharSource source) {
        super("Error while parsing at position " + (source.getOffset() + 1) + ": " + message);
        this.source = source;
    }

    public CharSource getSource() {
        return source;
    }
}
