package compiler.Parser.AST;

public class FloatLiteralNode extends ExpressionNode {

    private final float value;

    public FloatLiteralNode(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString(String indent) {
        return indent + "Float, " + value + "\n";
    }
}
