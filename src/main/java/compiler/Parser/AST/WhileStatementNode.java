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

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("While\n");
        sb.append(indent).append("  Condition\n");
        sb.append(condition.toString(indent + "    "));
        sb.append(body.toString(indent + "  "));
        return sb.toString();
    }




}
