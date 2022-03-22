package expression.generic;

public class Const<T extends ExpressionNumber<T, ?>> extends Operand<T> {
    private final T value;

    public Const(T value) {
        super();
        this.value = value;
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
    public T evaluate(T x, T y, T z) {
        return value;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;

        if (that != null) {
            if (this.getClass() == that.getClass()) {
                return this.value.equals(((Const<?>) that).value);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}