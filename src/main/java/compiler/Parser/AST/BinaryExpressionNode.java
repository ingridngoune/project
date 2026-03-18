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
}