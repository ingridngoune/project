package compiler.Parser.AST;

import java.util.List;

public class FunctionCallNode extends Expression {

    private final Expression function;
    private final List<Expression> arguments;

    public FunctionCallNode(Expression function, List<Expression> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public Expression getFunction() {
        return function;
    }

    public List<Expression> getArguments() {
        return arguments;
    }
}
