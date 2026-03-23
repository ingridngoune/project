package compiler.Parser.AST;

public class IdentifierNode extends ExpressionNode {

    private final String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(String indent) {
        return indent + "Identifier, " + name + "\n";
    }

}