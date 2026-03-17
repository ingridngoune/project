package compiler.Parser.AST;

public class BoolLiteralNode extends Expression {

    private final boolean value;

    public BoolLiteralNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}