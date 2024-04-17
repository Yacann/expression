package expression.operations;

import expression.Expression;
import expression.algebras.Algebra;

abstract class UnaryOperation<T> implements Expression<T> {
    protected final Algebra<T> algebra;
    private final Expression<T> a;

    public UnaryOperation(Algebra<T> algebra, Expression<T> a) {
        this.algebra = algebra;
        this.a = a;
    }

    abstract T operation(T a);

    @Override
    public T evaluate(T x, T y, T z) {
        return operation(a.evaluate(x, y, z));
    }

}
