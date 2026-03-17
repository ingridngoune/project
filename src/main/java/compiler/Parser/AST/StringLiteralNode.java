package compiler.Parser.AST;

public class StringLiteralNode extends Expression {

    private final String value;

    public StringLiteralNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}