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
}
