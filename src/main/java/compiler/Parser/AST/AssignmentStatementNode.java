package compiler.Parser.AST;

public class AssignmentStatementNode extends StatementNode {

    private final ExpressionNode target;
    private final ExpressionNode value;

    public AssignmentStatementNode(ExpressionNode target, ExpressionNode value) {
        this.target = target;
        this.value = value;
    }

    public ExpressionNode getTarget() {
        return target;
    }

    public ExpressionNode getValue() {
        return value;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Assign\n");
        sb.append(target.toString(indent + "  "));
        sb.append(value.toString(indent + "  "));
        return sb.toString();
    }
}
