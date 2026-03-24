package compiler.Parser.AST;

public class ArrayCreationNode extends ExpressionNode {

    private final TypeNode elementType;
    private final ExpressionNode size;

    public ArrayCreationNode(TypeNode elementType, ExpressionNode size) {
        this.elementType = elementType;
        this.size = size;
    }

    public TypeNode getElementType() {
        return elementType;
    }

    public ExpressionNode getSize() {
        return size;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("NewArray\n");
        sb.append(elementType.toString(indent + "  "));
        sb.append(size.toString(indent + "  "));
        return sb.toString();
    }
}