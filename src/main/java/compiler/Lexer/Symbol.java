package compiler.Lexer;

public class Symbol {
    private final Token type;
    private final Object attribute;

    public Symbol(Token type, Object attribute){
        this.type=type;
        this.attribute=attribute;
    }

    public Token getType() {
        return type;
    }

    public Object getValue() {
        return attribute;
    }

    @Override
    public String toString() {
        if(attribute==null) {
            return "<" + type + ">";
        }
        return "<" + type +
                "," + attribute +
                ">";
    }
}
