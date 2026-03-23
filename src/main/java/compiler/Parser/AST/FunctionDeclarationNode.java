package compiler.Parser.AST;

import java.util.List;

public class FunctionDeclarationNode extends Declaration {
    private final TypeNode returnType;
    private final String name;
    private final List<ParameterNode> parameters;
    private final BlockNode body;

    public FunctionDeclarationNode(TypeNode returnType, String name, List<ParameterNode> parameters, BlockNode body) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    public BlockNode getBody() {
        return body;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Function, ").append(name).append("\n");
        if (returnType != null) {
            sb.append(returnType.toString(indent + "  "));
        }
        for (ParameterNode param : parameters) {
            sb.append(param.toString(indent + "  "));
        }
        sb.append(body.toString(indent + "  "));
        return sb.toString();
    }
}
