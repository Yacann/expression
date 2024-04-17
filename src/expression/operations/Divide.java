package expression.operations;

import expression.Expression;
import expression.algebras.Algebra;

public class Divide<T> extends BinaryOperation<T> {
    public final static int priority = 12;
    public Divide(Algebra<T> algebra, Expression<T> a, Expression<T> b) {
        super(algebra, a, b);
    }

    @Override
    public T operation(T a, T b) {
        return algebra.divide(a, b);
    }
}
