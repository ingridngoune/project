package compiler.Parser.AST;

public class UnaryExpressionNode extends Expression {

    private final String operator;
    private final Expression expression;

    public UnaryExpressionNode(String operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getExpression() {
        return expression;
    }
}