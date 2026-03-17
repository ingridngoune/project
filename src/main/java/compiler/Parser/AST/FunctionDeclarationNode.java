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
}
