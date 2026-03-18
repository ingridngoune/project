package compiler.Parser.AST;

public class VariableDeclarationNode extends Declaration{
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
}
