package compiler.Parser.AST;

public class WhileStatementNode extends Statement {

    private final Expression condition;
    private final BlockNode body;

    public WhileStatementNode(Expression condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockNode getBody() {
        return body;
    }
}
