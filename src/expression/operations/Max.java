package expression.operations;

import expression.Expression;
import expression.algebras.Algebra;

public class Max<T> extends BinaryOperation<T> {
    public final static int priority = 3;
    public Max(Algebra<T> algebra, Expression<T> a, Expression<T> b) {
        super(algebra, a, b);
    }

    @Override
    public T operation(T a, T b) {
        return algebra.max(a, b);
    }
}
