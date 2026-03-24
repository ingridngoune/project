package compiler.Parser.AST;

public class FieldAccessNode extends ExpressionNode {

    private final ExpressionNode target;
    private final String fieldName;

    public FieldAccessNode(ExpressionNode target, String fieldName) {
        this.target = target;
        this.fieldName = fieldName;
    }

    public ExpressionNode getTarget() {
        return target;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Field\n");
        sb.append(target.toString(indent + "  "));
        sb.append(indent).append("  Identifier, ").append(fieldName).append("\n");
        return sb.toString();
    }
}

