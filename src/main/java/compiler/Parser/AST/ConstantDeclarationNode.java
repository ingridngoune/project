package compiler.Parser.AST;

public class ConstantDeclarationNode extends Declaration {
    private final TypeNode type;
    private final String name;
    private final ExpressionNode value;

    public ConstantDeclarationNode(TypeNode type, String name, ExpressionNode value) {
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

    public ExpressionNode getValue() {
        return value;
    }
}