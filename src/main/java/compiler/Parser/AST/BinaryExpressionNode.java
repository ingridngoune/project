package compiler.Parser.AST;

public class BinaryExpressionNode extends Expression {

    private final Expression left;
    private final String operator;
    private final Expression right;

    public BinaryExpressionNode(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}