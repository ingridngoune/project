package compiler.Parser.AST;

public class FieldDeclarationNode extends ASTNode{
    private final TypeNode type;
    private final String name;

    public FieldDeclarationNode(TypeNode type, String name) {
        this.type = type;
        this.name = name;
    }

    public TypeNode getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
