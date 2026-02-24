package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final Reader sourceFile;
    private int currentChar;
    private static final Map<String, Token> WORDS =new HashMap<>();
    private static final Map<String, Token> OPERATORS =new HashMap<>();
    private static final Map<Character, Token> SPECIALS_CHARACTER =new HashMap<>();


    static {
        //for keywords
        WORDS.put("final", Token.FINAL);
        WORDS.put("coll", Token.COLL);
        WORDS.put("for", Token.FOR);
        WORDS.put("while", Token.WHILE);
        WORDS.put("if", Token.IF);
        WORDS.put("else", Token.ELSE);
        WORDS.put("return", Token.RETURN);
        WORDS.put("not", Token.NOT);
        WORDS.put("def", Token.DEF);
        WORDS.put("ARRAY", Token.ARRAY);

        //for types

        WORDS.put("INT", Token.INT_TYPE);
        WORDS.put("FLOAT", Token.FLOAT_TYPE);
        WORDS.put("BOOL", Token.BOOL_TYPE);
        WORDS.put("STRING", Token.STRING_TYPE);

        //for boolean literals
        WORDS.put("true", Token.BOOL_LITERAL);
        WORDS.put("false", Token.BOOL_LITERAL);

        //for operators

        OPERATORS.put("+", Token.PLUS);
        OPERATORS.put("-", Token.MINUS);
        OPERATORS.put("*", Token.MULTIPLY);
        OPERATORS.put("/", Token.DIVIDE);
        OPERATORS.put("%", Token.MODULO);
        OPERATORS.put("=", Token.ASSIGN);
        OPERATORS.put("==", Token.EQUAL);
        OPERATORS.put("=/=", Token.NOT_EQUAL);
        OPERATORS.put("<", Token.LESS);
        OPERATORS.put("<=", Token.LESS_EQUAL);
        OPERATORS.put(">", Token.GREATER);
        OPERATORS.put(">=", Token.GREATER_EQUAL);
        OPERATORS.put("&&", Token.AND);
        OPERATORS.put("||", Token.OR);
        OPERATORS.put("->", Token.ARROW);

        //for special charactar

        SPECIALS_CHARACTER.put('(', Token.LEFT_PAREN);
        SPECIALS_CHARACTER.put(')', Token.RIGHT_PAREN);
        SPECIALS_CHARACTER.put('{', Token.LEFT_BRACE);
        SPECIALS_CHARACTER.put('}', Token.RIGHT_BRACE);
        SPECIALS_CHARACTER.put('[', Token.LEFT_BRACKET);
        SPECIALS_CHARACTER.put(']', Token.RIGHT_BRACKET);
        SPECIALS_CHARACTER.put(',', Token.COMMA);
        SPECIALS_CHARACTER.put(';', Token.SEMILCOLON);
        SPECIALS_CHARACTER.put('.', Token.DOT);

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

        Token token=WORDS.get(sb.toString());

        if(token==null){
            return  new Symbol(Token.IDENTIFIER,sb.toString());

        }
        else if(token== Token.BOOL_LITERAL){
            return new Symbol(Token.BOOL_LITERAL,Boolean.parseBoolean(sb.toString()));
        }
        return new Symbol(token,null);

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
            if(isFloat){
                return new Symbol(Token.FLOAT_LITERAL,Double.parseDouble(sb.toString()));
            }
            return new Symbol(Token.INT_LITERAL,Integer.parseInt(sb.toString()));

    }

    private Symbol getString() throws IOException{
        nextRead();
        StringBuilder sb=new StringBuilder();
        while(currentChar !=-1 && currentChar!='"'){
            if(currentChar=='\\'){
                nextRead();
                if(currentChar==-1){
                    throw  new RuntimeException("LexicalError: the string is malformed");
                }
                switch(currentChar){
                    case 'n':sb.append('\n');break;
                    case '\\':sb.append('\\');break;
                    case '"':sb.append('"');break;
                    default:throw new RuntimeException("LexicalError:problem with escape character");
                }
                nextRead();
            }else{
                sb.append((char)currentChar);
                nextRead();
            }
        }
        if(currentChar !='"'){
            throw new RuntimeException("LexicalError:string unterminated");
        }
        nextRead();
        return new Symbol(Token.STRING_LITERAL,sb.toString());

    }

    private Symbol getSpecial() throws IOException{
        Token type=SPECIALS_CHARACTER.get((char)currentChar);
        if(type==null) return null;
        nextRead();
        return new Symbol(type,null);
    }

    private Symbol getOperator() throws IOException{
        switch(currentChar){
            case '=':
            case '<':
            case '>':
            case '|':
            case '&':




        }
    }
    
    public Symbol getNextSymbol() {
        return null;
    }
}
