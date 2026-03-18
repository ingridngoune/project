package compiler.Parser.AST;

public class ReturnStatementNode extends Statement {

    private final ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }
}
