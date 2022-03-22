package expression.generic;

public final class Variable<T extends ExpressionNumber<T, ?>> extends Operand<T> {
    private final static String allowedSymbols = "xyz";
    private final char symbol;

    public Variable(String symbol) {
        if (symbol.length() != 1 || !allowedSymbols.contains(symbol.toLowerCase())) {
            throw new IllegalArgumentException("Only X,Y,Z allowed as Const symbol, given " + symbol);
        }
        this.symbol = symbol.toLowerCase().charAt(0);
    }

    @Override
    protected void serializeString(StringBuilder sb) {
        sb.append(symbol);
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        sb.append(symbol);
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return switch (symbol) {
            case 'x' -> x;
            case 'y' -> y;
            case 'z' -> z;
            default -> throw new IllegalStateException("Invalid Const symbol: " + symbol);
        };
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;

        if (that != null) {
            if (this.getClass() == that.getClass()) {
                return this.symbol == ((Variable<?>) that).symbol;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return symbol;
    }
}