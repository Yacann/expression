package expression.parser;

import expression.Const;
import expression.Expression;
import expression.Variable;
import expression.algebras.Algebra;
import expression.exceptions.ParsingException;
import expression.operations.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class ExpressionParser {
    private <T> Expression<T> parse(final CharSource source, final Algebra<T> algebra) throws ParsingException {
        return new Parser(source, algebra).parse();
    }

    public <T> Expression<T>  parse(final String source, final Algebra<T> algebra) throws ParsingException {
        return parse((CharSource) new StringSource(source), algebra);
    }

    private static class Parser<T> extends BaseParser {
        private Algebra<T> algebra;
        private Deque<Expression<T>> operands;
        private Deque<TOKEN> tokens;
        private boolean correctMinMax = false;

        interface TOKEN {}

        enum CONTROL implements TOKEN {
            LPAREN,
            RPAREN
        }

        interface OPERATION extends TOKEN {}

        enum UNARY_OPERATION implements OPERATION {
            MINUS,
            COUNT
        }

        enum BINARY_OPERATION implements OPERATION {
            DIVIDE, MULTIPLY,
            ADD, SUBTRACT,
            MIN, MAX
        }

        private static final Map<TOKEN, Integer> priority = Map.of(
                CONTROL.LPAREN, 0,
                CONTROL.RPAREN, 0,
                BINARY_OPERATION.DIVIDE, Divide.priority,
                BINARY_OPERATION.MULTIPLY, Multiply.priority,
                BINARY_OPERATION.ADD, Add.priority,
                BINARY_OPERATION.SUBTRACT, Subtract.priority,
                BINARY_OPERATION.MIN, Min.priority,
                BINARY_OPERATION.MAX, Max.priority
        );
        
        private Parser(final CharSource source) {
            super(source);
        }

        public Parser(final CharSource source, final Algebra<T> algebra) {
            this(source);
            this.algebra = algebra;
        }

        private Expression<T> parse() throws ParsingException {
            operands = new ArrayDeque<>();
            tokens = new ArrayDeque<>();
            tokens.addLast(CONTROL.LPAREN);
            skipWhitespace();
            if (eof()) {
                throw new ParsingException("expression expected");
            }
            parseElement();
            while (parseBinaryOperation()) {
                parseElement();
            }
            tokens.addLast(CONTROL.RPAREN);
            squeeze();
            if (operands.size() > 1 || !eof()) {
                throw new ParsingException("invalid expression");
            }
            return operands.getLast();
        }

        private void parseElement() throws ParsingException {
            correctMinMax = false;
            skipWhitespace();
            if (take('(')) {
                parseExpression();
                correctMinMax = true;
            } else {
                parseTerm();
            }
            skipWhitespace();
        }

        private void parseExpression() throws ParsingException {
            tokens.addLast(CONTROL.LPAREN);
            skipWhitespace();
            if (take(')')) {
                throw new ParsingException("empty brackets");
            }

            parseElement();
            while (parseBinaryOperation()) {
                parseElement();
            }
            expect(')');
            tokens.addLast(CONTROL.RPAREN);
            squeeze();
        }

        private void parseTerm() throws ParsingException {
            if (parseUnaryOperation()) {
                if (tokens.getLast() == UNARY_OPERATION.MINUS && between('0', '9')) {
                    tokens.removeLast();
                    operands.addLast(new Const<T>(algebra.parseFromInt(parseInteger(true))));
                } else {
                    parseElement();
                }
            } else if (between('x', 'z')) {
                operands.addLast(new Variable<T>("" + take()));
            } else if (between('0', '9')) {
                operands.addLast(new Const<T>(algebra.parseFromInt(parseInteger(false))));
            } else {
                throw new ParsingException("invalid term");
            }
            if (Character.isWhitespace(getChar())) {
                correctMinMax = true;
            }
            squeeze();
        }

        private boolean parseBinaryOperation() throws ParsingException {
            if (take('/')) {
                tokens.addLast(BINARY_OPERATION.DIVIDE);
            } else if (take('*')) {
                tokens.addLast(BINARY_OPERATION.MULTIPLY);
            } else if (take('+')) {
                tokens.addLast(BINARY_OPERATION.ADD);
            } else if (take('-')) {
                tokens.addLast(BINARY_OPERATION.SUBTRACT);
            } else if (take('m')) {
                if (take("in")) {
                    tokens.addLast(BINARY_OPERATION.MIN);
                } else if (take("ax")) {
                    tokens.addLast(BINARY_OPERATION.MAX);
                } else {
                    return false;
                }
                if (!correctMinMax) {
                    throw new ParsingException("invalid operation");
                }
            } else {
                return false;
            }
            squeeze();
            return true;
        }

        private boolean parseUnaryOperation() throws ParsingException {
            if (take('-')) {
                tokens.addLast(UNARY_OPERATION.MINUS);
            } else if (take("count")) {
                tokens.addLast(UNARY_OPERATION.COUNT);
            } else {
                return false;
            }
            return true;
        }

        private int parseInteger(boolean minus) throws ParsingException {
            final StringBuilder sb = new StringBuilder();
            if (minus) {
                sb.append('-');
            }
            if (take('0')) {
                return 0;
            }
            if (between('1', '9')) {
                while (between('0', '9')) {
                    sb.append(take());
                }
            }
            try {
                return Integer.parseInt(sb.toString());
            } catch (NumberFormatException e) {
                throw new ParsingException("invalid number " + sb);
            }
        }

        private void skipWhitespace() {
            while (Character.isWhitespace(getChar())) {
                take();
            }
        }

        private void squeeze() throws ParsingException {
            if (tokens.size() > 1) {
                if (tokens.getLast() == CONTROL.RPAREN) {
                    tokens.removeLast();
                    while (tokens.getLast() != CONTROL.LPAREN) {
                        Expression<T> b = operands.removeLast();
                        Expression<T> a = operands.removeLast();
                        operands.addLast(createBinaryExpression(a, (BINARY_OPERATION) tokens.removeLast(), b));
                    }
                    tokens.removeLast();
                } else if (tokens.getLast().getClass() == BINARY_OPERATION.class) {
                    TOKEN token = tokens.removeLast();
                    while (!tokens.isEmpty() && priority.get(tokens.getLast()) >= priority.get(token)) {
                        Expression<T> b = operands.removeLast();
                        Expression<T> a = operands.removeLast();
                        operands.addLast(createBinaryExpression(a, (BINARY_OPERATION) tokens.removeLast(), b));
                    }
                    tokens.addLast(token);
                } else if (tokens.getLast().getClass() == UNARY_OPERATION.class) {
                    while (tokens.getLast().getClass() == UNARY_OPERATION.class) {
                        operands.addLast(createUnaryExpression((UNARY_OPERATION) tokens.removeLast()));
                    }
                }
            }
        }

        private Expression<T> createBinaryExpression(Expression<T> a, BINARY_OPERATION operation, Expression<T> b) throws ParsingException {
            return switch (operation) {
                case DIVIDE -> new Divide<T>(algebra, a, b);
                case MULTIPLY -> new Multiply<T>(algebra, a, b);
                case ADD -> new Add<T>(algebra, a, b);
                case SUBTRACT -> new Subtract<T>(algebra, a, b);
                case MIN -> new Min<T>(algebra, a, b);
                case MAX -> new Max<T>(algebra, a, b);
                default -> throw new ParsingException("Unsupported operation: " + operation);
            };
        }

        private Expression<T> createUnaryExpression(UNARY_OPERATION operation) throws ParsingException {
            return switch (operation) {
                case MINUS -> new Negate<T>(algebra, operands.removeLast());
                case COUNT -> new Count<T>(algebra, operands.removeLast());
                default -> throw new ParsingException("Unsupported operation: " + operation);
            };
        }
    }
}
