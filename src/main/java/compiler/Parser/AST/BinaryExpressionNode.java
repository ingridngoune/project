package compiler.Parser.AST;

public class BinaryExpressionNode extends ExpressionNode {

    private final ExpressionNode left;
    private final String operator;
    private final ExpressionNode right;

    public BinaryExpressionNode(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ExpressionNode getRight() {
        return right;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(operator).append("\n");
        sb.append(left.toString(indent + "  "));
        sb.append(right.toString(indent + "  "));
        return sb.toString();
    }
}