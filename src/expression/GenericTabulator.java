package expression;

import expression.algebras.*;
import expression.exceptions.EvaluationException;
import expression.exceptions.ParsingException;
import expression.parser.ExpressionParser;

import java.util.Map;

public class GenericTabulator implements Tabulator {
    private final Map<String, Algebra<?>> algebras = Map.of(
            "i", new IntegerAlgebra(true),
            "d", new DoubleAlgebra(),
            "bi", new BigIntegerAlgebra(),
            "u", new IntegerAlgebra(false),
            "b", new ByteAlgebra(),
            "bool", new BooleanAlgebra()
    );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        Algebra<?> algebra = algebras.get(mode);
        return makeTable(algebra, expression, x1, x2, y1, y2, z1, z2);
    }

    public <T> Object[][][] makeTable(Algebra<T> algebra, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        int dx = x2 - x1 + 1, dy = y2 - y1 + 1, dz = z2 - z1 + 1;
        Object[][][] table = new Object[dx][dy][dz];
        try {
            ExpressionParser parser = new ExpressionParser();
            Expression<T> expr = parser.parse(expression, algebra);
            for (int i = 0; i < dx; i++) {
                for (int j = 0; j < dy; j++) {
                    for (int k = 0; k < dz; k++) {
                        try {
                            table[i][j][k] = expr.evaluate(algebra.parseFromInt(x1 + i), algebra.parseFromInt(y1 + j), algebra.parseFromInt(z1 + k));
                        } catch (EvaluationException e) {
                            table[i][j][k] = null;
                        }
                    }
                }
            }
        } catch (ParsingException e) {
            System.err.println(e.getMessage());
        }
        return table;
    }
}
