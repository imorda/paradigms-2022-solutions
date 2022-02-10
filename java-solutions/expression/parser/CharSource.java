package expression.parser;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface CharSource {
    boolean hasNext();

    char next();

    void mark();

    void unmark();

    void returnToMark();

    IllegalArgumentException error(final String message);

    int getOffset();
}
