package expression.operations;

import expression.Expression;
import expression.algebras.Algebra;

abstract class BinaryOperation<T> implements Expression<T> {
    protected final Algebra<T> algebra;
    private final Expression<T> a;
    private final Expression<T> b;

    public BinaryOperation(Algebra<T> algebra, Expression<T> a, Expression<T> b) {
        this.algebra = algebra;
        this.a = a;
        this.b = b;
    }

    abstract T operation(T a, T b);

    @Override
    public T evaluate(T x, T y, T z) {
        return operation(a.evaluate(x, y, z), b.evaluate(x, y, z));
    }
}
