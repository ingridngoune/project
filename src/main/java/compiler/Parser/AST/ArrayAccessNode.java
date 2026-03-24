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

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Index\n");
        sb.append(array.toString(indent + "  "));
        sb.append(index.toString(indent + "  "));
        return sb.toString();
    }
}
