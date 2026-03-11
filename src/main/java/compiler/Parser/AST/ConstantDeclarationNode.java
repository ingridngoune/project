package compiler.Parser.AST;

public class ConstantDeclarationNode extends Declaration {
    private final TypeNode type;
    private final String name;
    private final Expression value;

    public ConstantDeclarationNode(TypeNode type, String name, Expression value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public TypeNode getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }
}