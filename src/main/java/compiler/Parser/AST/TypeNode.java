package compiler.Parser.AST;

public class TypeNode extends ASTNode {
    private final String name;
    private final boolean array;

    public TypeNode(String name, boolean array) {
        this.name = name;
        this.array = array;
    }

    public String getName() {
        return name;
    }

    public boolean isArray() {
        return array;
    }

    @Override
    public String toString(String indent) {
        String typeName = name;
        if (array) {
            typeName += "[]";
        }
        return indent + "Type, " + typeName + "\n";
    }

}