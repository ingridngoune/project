package compiler.Parser.AST;

public class IntLiteralNode extends ExpressionNode {

    private final int value;

    public IntLiteralNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString(String indent) {
        return indent + "Int, " + value + "\n";
    }
}