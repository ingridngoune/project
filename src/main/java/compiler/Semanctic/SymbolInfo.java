package compiler.Semanctic;

import java.util.List;
import java.util.Map;

public class SymbolInfo {

    public enum Kind{
        VARIABLE,CONSTANT,FUNCTION,COLLECTION
    }

    private final String name;
    private final Kind kind;
    private final SemanticType type;

    private final List<SemanticType>parameterTypes;

    private final Map<String,SemanticType>fields;

    public SymbolInfo(String name, Kind kind, SemanticType type) {
        this.name=name;
        this.kind=kind;
        this.type=type;
        this.parameterTypes=null;
        this.fields=null;
    }

    public SymbolInfo(String name, Kind kind, SemanticType type,
                      List<SemanticType> parameterTypes,
                      Map<String, SemanticType> fields) {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.parameterTypes = parameterTypes;
        this.fields = fields;

    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public SemanticType getType() {
        return type;
    }

    public List<SemanticType> getParameterTypes() {
        return parameterTypes;
    }

    public Map<String, SemanticType> getFields() {
        return fields;
    }
    }
