package compiler.Parser.AST;

public class VariableDeclarationNode extends StatementNode{
    private final TypeNode type;
    private final String name;
    private final ExpressionNode initValue;

    public VariableDeclarationNode(TypeNode type, String name, ExpressionNode initValue) {
        this.type = type;
        this.name = name;
        this.initValue = initValue;
    }
    public TypeNode getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public ExpressionNode getInitValue() {
        return initValue;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Var, ").append(name).append("\n");
        sb.append(type.toString(indent + "  "));
        if (initValue != null) {
            sb.append(initValue.toString(indent + "  "));
        }
        return sb.toString();
    }
}
