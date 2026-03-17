package compiler.Parser.AST;

public class ReturnStatementNode extends Statement {

    private final Expression expression;

    public ReturnStatementNode(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
