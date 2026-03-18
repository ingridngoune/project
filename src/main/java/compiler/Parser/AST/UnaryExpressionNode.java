package compiler.Parser.AST;

public class UnaryExpressionNode extends ExpressionNode {

    private final String operator;
    private final ExpressionNode expression;

    public UnaryExpressionNode(String operator, ExpressionNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public String getOperator() {
        return operator;
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}