package compiler.Parser.AST;

public class IntLiteralNode extends Expression {

    private final int value;

    public IntLiteralNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}