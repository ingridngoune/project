package compiler.Parser.AST;

public class ArrayAccessNode extends ExpressionNode {

    private final ExpressionNode array;
    private final ExpressionNode index;

    public ArrayAccessNode(ExpressionNode array, ExpressionNode index) {
        this.array = array;
        this.index = index;
    }

    public ExpressionNode getArray() {
        return array;
    }

    public ExpressionNode getIndex() {
        return index;
    }
}
