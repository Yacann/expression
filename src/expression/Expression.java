package expression;

@FunctionalInterface
public interface Expression<T> {
    T evaluate(T x, T y, T z);
}
