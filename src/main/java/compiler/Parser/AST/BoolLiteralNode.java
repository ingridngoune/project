package compiler.Parser.AST;

public class BoolLiteralNode extends ExpressionNode {

    private final boolean value;

    public BoolLiteralNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString(String indent) {
        return indent + "Bool, " + value + "\n";
    }
}