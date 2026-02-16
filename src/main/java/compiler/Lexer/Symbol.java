package compiler.Lexer;

public class Symbol {
    private final SymbolType type;
    private final String value;

    public Symbol(SymbolType type,String value){
        this.type=type;
        this.value=value;
    }

    public SymbolType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if(value==null) {
            return "<" + type + ">";
        }
        return "<" + type +
                "," + value +
                ">";
    }
}
