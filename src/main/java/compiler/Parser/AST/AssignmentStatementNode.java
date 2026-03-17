package compiler.Parser.AST;

public class AssignmentStatementNode extends Statement {

    private final Expression target;
    private final Expression value;

    public AssignmentStatementNode(Expression target, Expression value) {
        this.target = target;
        this.value = value;
    }

    public Expression getTarget() {
        return target;
    }

    public Expression getValue() {
        return value;
    }
}
