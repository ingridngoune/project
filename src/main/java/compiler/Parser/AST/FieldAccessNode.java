package compiler.Parser.AST;

public class FieldAccessNode extends Expression {

    private final Expression target;
    private final String fieldName;

    public FieldAccessNode(Expression target, String fieldName) {
        this.target = target;
        this.fieldName = fieldName;
    }

    public Expression getTarget() {
        return target;
    }

    public String getFieldName() {
        return fieldName;
    }
}
