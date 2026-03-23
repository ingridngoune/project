package compiler.Parser.AST;

import java.util.List;

public class FunctionCallNode extends ExpressionNode {

    private final ExpressionNode function;
    private final List<ExpressionNode> arguments;

    public FunctionCallNode(ExpressionNode function, List<ExpressionNode> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public ExpressionNode getFunction() {
        return function;
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Call\n");
        sb.append(function.toString(indent + "  "));
        for (ExpressionNode argument : arguments) {
            sb.append(indent).append("  Argument\n");
            sb.append(argument.toString(indent + "    "));
        }
        return sb.toString();
    }
}
