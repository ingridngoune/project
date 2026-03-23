package compiler.Parser.AST;

import java.util.List;

public class CollectionDeclarationNode extends Declaration{
    private final String name;
    private final List<FieldDeclarationNode> fields;

    public CollectionDeclarationNode(String name, List<FieldDeclarationNode> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<FieldDeclarationNode> getFields() {
        return fields;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Collection, ").append(name).append("\n");
        for (FieldDeclarationNode field : fields) {
            sb.append(field.toString(indent + "  "));
        }
        return sb.toString();
    }
}
