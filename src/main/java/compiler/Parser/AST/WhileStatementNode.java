package compiler.Parser.AST;

public class WhileStatementNode extends StatementNode {

    private final ExpressionNode condition;
    private final BlockNode body;

    public WhileStatementNode(ExpressionNode condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockNode getBody() {
        return body;
    }
}
