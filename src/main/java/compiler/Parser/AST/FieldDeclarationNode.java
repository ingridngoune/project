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

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Field\n");
        sb.append(type.toString(indent + "  "));
        sb.append(indent).append("  Identifier, ").append(name).append("\n");
        return sb.toString();
    }
}
