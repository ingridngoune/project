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
}
