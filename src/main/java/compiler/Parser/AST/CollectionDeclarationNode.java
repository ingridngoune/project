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
}
