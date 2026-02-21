package compiler.Lexer;

public class Symbol {
    String type;
    String lex;
    Object literal // change type probably to see 
    int line;

    Symbol(String type, String lex, Object literal, int line){
        this.type=type;
        this.lex=lex;
        this.literal=literal;
        this.line=line;
    }

    public String toString(){
        return type +" " + lex +" "+ literal;
    }
}
