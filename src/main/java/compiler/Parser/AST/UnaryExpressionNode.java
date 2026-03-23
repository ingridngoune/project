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

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(operator).append("\n");
        sb.append(expression.toString(indent + "  "));
        return sb.toString();
    }
}