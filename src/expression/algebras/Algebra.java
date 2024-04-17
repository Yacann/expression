package expression.algebras;

public interface Algebra<T> {
    abstract T add(T a, T b);
    abstract T subtract(T a, T b);
    abstract T multiply(T a, T b);
    abstract T divide(T a, T b);
    abstract T negate(T a);
    abstract T max(T a, T b);
    abstract T min(T a, T b);
    abstract T count(T a);
    abstract T parseFromInt(int i);
}
