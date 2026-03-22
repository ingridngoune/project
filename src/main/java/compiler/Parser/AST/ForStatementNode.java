package compiler.Parser.AST;


public class ForStatementNode extends StatementNode {

    private final TypeNode variableType;
    private final String variableName;
    private final ExpressionNode startValue;
    private final ExpressionNode endValue;
    private final ExpressionNode stepValue;
    private final BlockNode body;

    public ForStatementNode(TypeNode variableType, String variableName, ExpressionNode startValue, ExpressionNode endValue, ExpressionNode stepValue, BlockNode body) {
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

    public ExpressionNode getStartValue() {
        return startValue;
    }

    public ExpressionNode getEndValue() {
        return endValue;
    }

    public ExpressionNode getStepValue() {
        return stepValue;
    }

    public BlockNode getBody() {
        return body;
    }
}
