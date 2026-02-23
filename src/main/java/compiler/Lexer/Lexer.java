package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final Reader input;
    private int currentChar;
    private static final Map<String, SymbolType> WORDS =new HashMap<>();
    private static final Map<String, SymbolType> OPERATORS =new HashMap<>();
    private static final Map<Character, SymbolType> SPECIALS_CHARACTER =new HashMap<>();


    static {
        //for keywords
        WORDS.put("final",SymbolType.FINAL);
        WORDS.put("coll",SymbolType.COLL);
        WORDS.put("for",SymbolType.FOR);
        WORDS.put("while",SymbolType.WHILE);
        WORDS.put("if",SymbolType.IF);
        WORDS.put("else",SymbolType.ELSE);
        WORDS.put("return",SymbolType.RETURN);
        WORDS.put("not",SymbolType.NOT);
        WORDS.put("array",SymbolType.ARRAY);

        //for types

        WORDS.put("INT",SymbolType.INT_TYPE);
        WORDS.put("FLOAT",SymbolType.FLOAT_TYPE);
        WORDS.put("BOOL",SymbolType.BOOL_TYPE);
        WORDS.put("STRING",SymbolType.STRING_TYPE);

        //for boolean literals
        WORDS.put("true",SymbolType.BOOL_LITERAL);
        WORDS.put("false",SymbolType.BOOL_LITERAL);

        //for operators

        OPERATORS.put("+",SymbolType.PLUS);
        OPERATORS.put("-",SymbolType.MINUS);
        OPERATORS.put("*",SymbolType.MULTIPLY);
        OPERATORS.put("/",SymbolType.DIVIDE);
        OPERATORS.put("%",SymbolType.MODULO);
        OPERATORS.put("=",SymbolType.ASSIGN);
        OPERATORS.put("==",SymbolType.EQUAL);
        OPERATORS.put("=/=",SymbolType.NOT_EQUAL);
        OPERATORS.put("<",SymbolType.LESS);
        OPERATORS.put("<=",SymbolType.LESS_EQUAL);
        OPERATORS.put(">",SymbolType.GREATER);
        OPERATORS.put(">=",SymbolType.GREATER_EQUAL);
        OPERATORS.put("&&",SymbolType.AND);
        OPERATORS.put("||",SymbolType.OR);
        OPERATORS.put("->",SymbolType.ARROW);

        //for special charactar

        SPECIALS_CHARACTER.put('(',SymbolType.LEFT_PAREN);
        SPECIALS_CHARACTER.put(')',SymbolType.RIGHT_PAREN);
        SPECIALS_CHARACTER.put('{',SymbolType.LEFT_BRACE);
        SPECIALS_CHARACTER.put('}',SymbolType.RIGHT_BRACE);
        SPECIALS_CHARACTER.put('[',SymbolType.LEFT_BRACKET);
        SPECIALS_CHARACTER.put(']',SymbolType.RIGHT_BRACKET);
        SPECIALS_CHARACTER.put(',',SymbolType.COMMA);
        SPECIALS_CHARACTER.put(';',SymbolType.SEMILCOLON);
        SPECIALS_CHARACTER.put('.',SymbolType.DOT);

    }

    public Lexer(Reader input) {
        this.input=input;
        try{
            nextRead();
        }catch(IOException e){
            throw new RuntimeException("LexerError: not input");
        }

    }
    private void nextRead() throws IOException{
            currentChar=input.read();
    }

    private void skipComOrSpace (){
    }
    
    public Symbol getNextSymbol() {
        return null;
    }
}
