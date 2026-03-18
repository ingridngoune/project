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
}