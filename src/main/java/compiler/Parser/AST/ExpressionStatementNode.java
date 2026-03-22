package compiler.Parser.AST;


public class ExpressionStatementNode extends StatementNode {

    private final ExpressionNode expression;

    public ExpressionStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}