package expression.parser;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class StringSource implements CharSource {
    private final String data;
    private int pos;
    private int markedPos = -1;

    public StringSource(final String data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }

    @Override
    public void mark() {
        markedPos = pos;
    }

    @Override
    public void unmark() {
        markedPos = -1;
    }

    @Override
    public void returnToMark() {
        if (markedPos == -1) {
            throw new IllegalArgumentException("No marked position to return to");
        }

        pos = markedPos;
        if (pos > 0) {
            pos--;
            next();
        }
        markedPos = -1;
    }

    @Override
    public IllegalArgumentException error(final String message) {
        return new IllegalArgumentException(pos + ": " + message);
    }

    @Override
    public int getOffset() {
        return pos;
    }
}
