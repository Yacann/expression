package expression.algebras;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class IntegerAlgebra implements Algebra<Integer> {
    private final boolean checkOverflow;

    public IntegerAlgebra(boolean checkOverflow) {
        this.checkOverflow = checkOverflow;
    }

    @Override
    public Integer add(Integer a, Integer b) {
        if (checkOverflow && (a ^ b) >= 0
                && (a >= 0 && a > Integer.MAX_VALUE - b
                || a < 0 && a < Integer.MIN_VALUE - b)) {
            throw new OverflowException("integer add overflow");
        }
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (checkOverflow && (a ^ b) < 0
                && (a >= 0 && a > Integer.MAX_VALUE + b
                || a < 0 && a < Integer.MIN_VALUE + b)) {
            throw new OverflowException("integer subtract overflow");
        }
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (checkOverflow && ((a != 0 && a * b / a != b)
                || (b != 0 && a * b / b != a))) {
            throw new OverflowException("integer multiply overflow");
        }
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivisionByZeroException("integer division by zero");
        } else if (checkOverflow && a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException("integer divide overflow");
        }
        return a / b;
    }

    @Override
    public Integer negate(Integer a) {
        if (checkOverflow && a == Integer.MIN_VALUE) {
            throw new OverflowException("integer negate overflow");
        }
        return -a;
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Integer.max(a, b);
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Integer.min(a, b);
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer parseFromInt(int i) {
        return i;
    }
}
