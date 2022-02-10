package expression.exceptions;

import expression.parser.CharSource;

public final class EOFException extends ParseException {
    public EOFException(CharSource source) {
        super("Unexpected end-of-file", source);
    }
}
