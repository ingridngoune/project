package compiler.Parser.AST;


public class ForStatementNode extends Statement {

    private final TypeNode variableType;
    private final String variableName;
    private final Expression startValue;
    private final Expression endValue;
    private final Expression stepValue;
    private final BlockNode body;

    public ForStatementNode(TypeNode variableType, String variableName, Expression startValue, Expression endValue, Expression stepValue, BlockNode body) {
        this.variableType = variableType;
        this.variableName = variableName;
        this.startValue = startValue;
        this.endValue = endValue;
        this.stepValue = stepValue;
        this.body = body;
    }

    public TypeNode getVariableType() {
        return variableType;
    }

    public String getVariableName() {
        return variableName;
    }

    public Expression getStartValue() {
        return startValue;
    }

    public Expression getEndValue() {
        return endValue;
    }

    public Expression getStepValue() {
        return stepValue;
    }

    public BlockNode getBody() {
        return body;
    }
}
