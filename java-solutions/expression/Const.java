package expression;

import java.math.BigInteger;

public class Const extends Operand {
    private final Number value;

    public Const(int value) {
        super();
        this.value = value;
    }

    public Const(BigInteger value) {
        super();
        this.value = value;
    }

    public Number getValue() {
        return value;
    }

    @Override
    protected void serializeString(StringBuilder sb) {
        sb.append(value);
    }

    @Override
    protected void serializeMini(StringBuilder sb) {
        sb.append(value);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return evaluate(x);
    }

    @Override
    public int evaluate(int x) {
        return value.intValue();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;

        if (that != null) {
            if (this.getClass() == that.getClass()) {
                return this.value.equals(((Const) that).value);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}