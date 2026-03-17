package compiler.Parser.AST;

public class ArrayCreationNode extends Expression {

    private final TypeNode elementType;
    private final Expression size;

    public ArrayCreationNode(TypeNode elementType, Expression size) {
        this.elementType = elementType;
        this.size = size;
    }

    public TypeNode getElementType() {
        return elementType;
    }

    public Expression getSize() {
        return size;
    }
}