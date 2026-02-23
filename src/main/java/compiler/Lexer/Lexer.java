package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final Reader sourceFile;
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
        WORDS.put("ARRAY",SymbolType.ARRAY);

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

    public Lexer(Reader sourceFile) {
        this.sourceFile=sourceFile;
        try{
            nextRead();
        }catch(IOException e){
            throw new RuntimeException("LexerError: not input given");
        }

    }
    private void nextRead() throws IOException{
            currentChar=sourceFile.read();
    }

    private void skipComOrSpace() throws IOException {
        while(currentChar!=-1){
            if(Character.isWhitespace(currentChar)){
                nextRead();
            }
            else if(currentChar=='#'){
                while (currentChar!=-1 && currentChar!='\n'){
                    nextRead();
                }
            }else{
                break;
            }
        }
    }

    private Symbol getWord() throws IOException{
        StringBuilder sb=new StringBuilder();
        while(Character.isLetterOrDigit(currentChar)||currentChar=='_'){
            sb.append((char) currentChar);
            nextRead();
        }

        String word=sb.toString();
        SymbolType symbolType=WORDS.get(word);

        if(symbolType==null){
            return  new Symbol(SymbolType.IDENTIFIER,word);

        }
        else if(symbolType==SymbolType.BOOL_LITERAL){
            return new Symbol(SymbolType.BOOL_LITERAL,(word));
        }
        return new Symbol(symbolType,null);

    }


    private Symbol getNumber() throws IOException{
        StringBuilder sb=new StringBuilder();
        boolean isFloat=false;
        while(currentChar!=-1 && Character.isDigit(currentChar)){
            sb.append((char) currentChar);
            nextRead();
        }
        if(currentChar=='.') {
            isFloat = true;
            sb.append((char) currentChar);
            nextRead();

            if (currentChar == -1 || !Character.isDigit(currentChar)) {
                throw new RuntimeException("LexicalError :the float is malformed");
            }
            while (currentChar != -1 && Character.isDigit(currentChar)) {
                sb.append((char) currentChar);
                nextRead();
            }
        }
            String number=sb.toString();
            if(isFloat){
                return new Symbol(SymbolType.FLOAT_LITERAL,number);
            }
            return new Symbol(SymbolType.INT_LITERAL,number);

    }
    
    public Symbol getNextSymbol() {
        return null;
    }
}
