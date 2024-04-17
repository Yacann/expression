package expression;

public class Const<T> implements Expression<T> {
    private final T c;

    public Const(T c) {
        this.c = c;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return c;
    }
}
