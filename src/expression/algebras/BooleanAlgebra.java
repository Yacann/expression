package expression.algebras;

import expression.exceptions.DivisionByZeroException;

public class BooleanAlgebra implements Algebra<Boolean> {
    @Override
    public Boolean add(Boolean a, Boolean b) {
        return a | b;
    }

    @Override
    public Boolean subtract(Boolean a, Boolean b) {
        return a ^ b;
    }

    @Override
    public Boolean multiply(Boolean a, Boolean b) {
        return a & b;
    }

    @Override
    public Boolean divide(Boolean a, Boolean b) {
        if (!b) throw new DivisionByZeroException("boolean division by zero");
        return a;
    }

    @Override
    public Boolean negate(Boolean a) {
        return a;
    }

    @Override
    public Boolean max(Boolean a, Boolean b) {
        return a | b;
    }

    @Override
    public Boolean min(Boolean a, Boolean b) {
        return a & b;
    }

    @Override
    public Boolean count(Boolean a) {
        return a;
    }

    @Override
    public Boolean parseFromInt(int i) {
        return i != 0;
    }
}
