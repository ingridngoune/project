package compiler.Parser.AST;

public class ReturnStatementNode extends StatementNode {

    private final ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}
