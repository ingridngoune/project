package compiler.Parser.AST;


public class IfStatementNode extends StatementNode {

    private final ExpressionNode condition;
    private final BlockNode thenBlock;
    private final BlockNode elseBlock;

    public IfStatementNode(ExpressionNode condition, BlockNode thenBlock, BlockNode elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockNode getThenBlock() {
        return thenBlock;
    }

    public BlockNode getElseBlock() {
        return elseBlock;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("If\n");
        sb.append(indent).append("  Condition\n");
        sb.append(condition.toString(indent + "    "));
        sb.append(indent).append("  Then\n");
        sb.append(thenBlock.toString(indent + "    "));
        if (elseBlock != null) {
            sb.append(indent).append("  Else\n");
            sb.append(elseBlock.toString(indent + "    "));
        }
        return sb.toString();
    }
}