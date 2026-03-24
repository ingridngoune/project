package compiler.Parser.AST;

public class ReturnStatementNode extends StatementNode {

    private final ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Return\n");
        if (expression != null) {
            sb.append(expression.toString(indent + "  "));
        }
        return sb.toString();
    }
}
