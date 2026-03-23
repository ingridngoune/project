package compiler.Parser.AST;

import java.util.List;

public class ProgramNode extends ASTNode{
    private final List<ConstantDeclarationNode> constantDeclarations;
    private final List<CollectionDeclarationNode> collectionDeclarations;
    private final List<VariableDeclarationNode> globalVariableDeclarations;
    private final List<FunctionDeclarationNode> functionDeclarations;

    public ProgramNode(
            List<ConstantDeclarationNode> constantDeclarations,
            List<CollectionDeclarationNode> collectionDeclarations,
            List<VariableDeclarationNode> globalVariableDeclarations,
            List<FunctionDeclarationNode> functionDeclarations) {

        this.constantDeclarations = constantDeclarations;
        this.collectionDeclarations = collectionDeclarations;
        this.globalVariableDeclarations = globalVariableDeclarations;
        this.functionDeclarations = functionDeclarations;
    }

    public List<ConstantDeclarationNode> getConstantDeclarations() {
        return constantDeclarations;
    }

    public List<CollectionDeclarationNode> getCollectionDeclarations() {
        return collectionDeclarations;
    }

    public List<VariableDeclarationNode> getGlobalVariableDeclarations() {
        return globalVariableDeclarations;
    }

    public List<FunctionDeclarationNode> getFunctionDeclarations() {
        return functionDeclarations;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Program\n");
        String childIndent = indent + "  ";
        for (ConstantDeclarationNode constant : constantDeclarations) {
            sb.append(constant.toString(childIndent));
        }
        for (CollectionDeclarationNode collection : collectionDeclarations) {
            sb.append(collection.toString(childIndent));
        }
        for (VariableDeclarationNode variable : globalVariableDeclarations) {
            sb.append(variable.toString(childIndent));
        }
        for (FunctionDeclarationNode function : functionDeclarations) {
            sb.append(function.toString(childIndent));
        }
        return sb.toString();
    }



}
