package compiler.Parser.AST;


public class ExpressionStatementNode extends Statement {

    private final Expression expression;

    public ExpressionStatementNode(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}