package expression.algebras;

import expression.exceptions.DivisionByZeroException;

public class ByteAlgebra implements Algebra<Byte> {
    @Override
    public Byte add(Byte a, Byte b) {
        return (byte) (a.intValue() + b.intValue());
    }

    @Override
    public Byte subtract(Byte a, Byte b) {
        return (byte) (a.intValue() - b.intValue());
    }

    @Override
    public Byte multiply(Byte a, Byte b) {
        return (byte) (a.intValue() * b.intValue());
    }

    @Override
    public Byte divide(Byte a, Byte b) {
        if (b == 0) throw new DivisionByZeroException("byte division by zero");
        return (byte) (a.intValue() / b.intValue());
    }

    @Override
    public Byte negate(Byte a) {
        return (byte) -a.intValue();
    }
    @Override
    public Byte max(Byte a, Byte b) {
        return (byte) Integer.max(a.intValue(), b.intValue());
    }

    @Override
    public Byte min(Byte a, Byte b) {
        return (byte) Integer.min(a.intValue(), b.intValue());
    }

    @Override
    public Byte count(Byte a) {
        return (byte) Integer.bitCount(a.intValue() & ((1 << 8) - 1));
    }

    @Override
    public Byte parseFromInt(int i) {
        return (byte) i;
    }
}
