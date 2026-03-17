package compiler.Parser.AST;


public class IfStatementNode extends Statement {

    private final Expression condition;
    private final BlockNode thenBlock;
    private final BlockNode elseBlock;

    public IfStatementNode(Expression condition, BlockNode thenBlock, BlockNode elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockNode getThenBlock() {
        return thenBlock;
    }

    public BlockNode getElseBlock() {
        return elseBlock;
    }
}