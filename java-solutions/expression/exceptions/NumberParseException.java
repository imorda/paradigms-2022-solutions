package expression.exceptions;

import expression.parser.CharSource;

public final class NumberParseException extends ParseException {
    private final String numberToParse;

    public NumberParseException(CharSource source, String numberToParse) {
        super("Could not parse a number: " + numberToParse, source);
        this.numberToParse = numberToParse;
    }

    public String getNumberToParse() {
        return numberToParse;
    }
}
