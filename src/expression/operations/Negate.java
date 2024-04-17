package expression.operations;

import expression.Expression;
import expression.algebras.Algebra;

public class Negate<T> extends UnaryOperation<T> {
    public final static int priority = 13;
    public Negate(Algebra<T> algebra, Expression<T> a) {
        super(algebra, a);
    }

    @Override
    public T operation(T a) {
        return algebra.negate(a);
    }
}
