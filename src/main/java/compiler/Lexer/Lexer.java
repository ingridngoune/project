package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;


//Webographie :
//https://craftinginterpreters.com/scanning.html
// https://www.baeldung.com/java-lexical-analysis-compilation
//https://medium.com/@enzojade62/step-by-step-building-a-lexer-in-java-for-tokenizing-source-code-ac4f1d91326f
//https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html

public class Lexer {
    private final Reader sourceFile;
    private int currentChar;
    private static final Map<String, Token> KEYWORDS =new HashMap<>();
    private static final Map<String, Token> OPERATORS =new HashMap<>();
    private static final Map<String, Token> BOOLS =new HashMap<>();
    private static final Map<String, Token> TYPES =new HashMap<>();


    private static final Map<Character, Token> SPECIALS_CHARACTER =new HashMap<>();


    static {
        //for keywords
        KEYWORDS.put("final", Token.FINAL);
        KEYWORDS.put("coll", Token.COLL);
        KEYWORDS.put("for", Token.FOR);
        KEYWORDS.put("while", Token.WHILE);
        KEYWORDS.put("if", Token.IF);
        KEYWORDS.put("else", Token.ELSE);
        KEYWORDS.put("return", Token.RETURN);
        KEYWORDS.put("not", Token.NOT);
        KEYWORDS.put("def", Token.DEF);

        //for boolean literals
        BOOLS.put("true", Token.BOOL_LITERAL);
        BOOLS.put("false", Token.BOOL_LITERAL);
        //for types

        TYPES.put("INT", Token.INT_TYPE);
        TYPES.put("FLOAT", Token.FLOAT_TYPE);
        TYPES.put("BOOL", Token.BOOL_TYPE);
        TYPES.put("STRING", Token.STRING_TYPE);
        TYPES.put("ARRAY", Token.ARRAY);

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
            throw new RuntimeException("not input given");
        }

    }
    private void nextRead() throws IOException{
            currentChar=sourceFile.read();
    }

    private void skipComOrSpace() throws IOException {
        while(currentChar!=-1){
            if(currentChar==' ' || currentChar =='\t' || currentChar =='\n' || currentChar=='\r'){
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
        while((currentChar>='a' && currentChar <='z')||
                (currentChar >='A' && currentChar<='Z')||
                (currentChar>='0' && currentChar <='9') ||
                currentChar=='_'){
            sb.append((char) currentChar);
            nextRead();
        }
        String word=sb.toString();
        if(TYPES.containsKey(word)){
            return new Symbol(TYPES.get(word),null);
        }
        else if (KEYWORDS.containsKey(word)){
            return new Symbol(KEYWORDS.get(word),null);
        }
        else if (BOOLS.containsKey(word)){
            return new Symbol(Token.BOOL_LITERAL,Boolean.parseBoolean(word));
        }
        char c=word.charAt(0);
        if(Character.isUpperCase(c)) {
            return new Symbol(Token.COLLECTION_IDENTIFIER,word);
        }
        return new Symbol(Token.IDENTIFIER,word);

    }

    private Symbol getNumber() throws IOException{
        StringBuilder sb=new StringBuilder();
        boolean isFloat=false;
        // case we have .1235
        if(currentChar=='.'){
            nextRead();
            if(currentChar==-1 || !Character.isDigit(currentChar)){
                return new Symbol(Token.DOT,null);
            }
            sb.append("0.");
            while(currentChar!=-1 && Character.isDigit(currentChar)){
                sb.append((char) currentChar);
                    nextRead();
            }
            return new Symbol(Token.FLOAT_LITERAL,Double.parseDouble(sb.toString()));
        }

        //case of 452 or 145.235
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
            case '+':
            case '-':
            case '*':
            case '/':
            case '%':
            case '=':
            case '<':
            case '>':
            case '|':
            case '&':{
                char c1=(char) currentChar;
                nextRead();
                if(c1=='=' && currentChar=='/'){
                    nextRead();
                    if(currentChar=='='){
                        nextRead();
                        return new Symbol(Token.NOT_EQUAL,null);
                    }
                    throw new RuntimeException("LexicalError : malformed operator '=/='");

                }
                if(currentChar!=-1){
                    String c2=""+c1+(char)currentChar;
                    Token type2=OPERATORS.get(c2);
                    if(type2!=null){
                        nextRead();
                        return new Symbol(type2,null);
                    }
                }

                Token type1=OPERATORS.get(String.valueOf(c1));
                if(type1!=null){
                    return new Symbol(type1,null);
                }

                throw new RuntimeException("LexicalError : unknown operator use");

            }
            default:
                return null;

        }
    }

    private boolean isAscciChar(int c){
        return (c=='\n') || (c=='\t') ||(c=='\r') ||(c>=32 && c<=126);
    }
    public Symbol getNextSymbol() {
        try{
            skipComOrSpace();
            if(currentChar==-1){
                return new Symbol(Token.EOF,null);
            }
            if(!isAscciChar(currentChar)){throw new RuntimeException("LexicalError : unknwon printable ascii character"+currentChar);
            }
            if((currentChar>='a' && currentChar <='z')||
                    (currentChar >='A' && currentChar<='Z')|| currentChar=='_'){
                return getWord();
            }
            if(Character.isDigit(currentChar) ||currentChar=='.'){
                return getNumber();
            }
            if(currentChar=='"'){
                return getString();
            }
            Symbol op=getOperator();
            if(op!=null) return op;
            Symbol spec=getSpecial();
            if(spec!=null)return spec;
            throw new RuntimeException("LexicalError : unknown character'"+(char)currentChar+"'");
        }catch(IOException e){
            throw new RuntimeException("cannot read the input");
        }

    }
}
