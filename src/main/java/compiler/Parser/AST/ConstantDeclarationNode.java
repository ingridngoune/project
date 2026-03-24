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

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Const, ").append(name).append("\n");
        sb.append(type.toString(indent + "  "));
        sb.append(value.toString(indent + "  "));
        return sb.toString();
    }
}