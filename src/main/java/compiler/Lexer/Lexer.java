package compiler.Lexer;
import java.io.Reader;
import java.util.*;

public class Lexer {

    Reader input;
    List<Symbol> symbols = new ArrayList<>();

    int start=0;
    int current=0;
    int line=1;

    enum SymbolType{
        // Single-character tokens.
        LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
        COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

        // One or two character tokens.
        BANG, BANG_EQUAL,
        EQUAL, EQUAL_EQUAL,
        GREATER, GREATER_EQUAL,
        LESS, LESS_EQUAL,

        // Literals.
        IDENTIFIER, STRING, NUMBER,

        // Keywords.
        FINAL,COLL, DEF, FOR, WHILE, IF, ELSE, RETURN, NOT, ARRAY,

        //BOOLEAN
        TRUE, FALSE,

        EOF
    }

    // taker  care of errors example of error handling 

    static void error(int line, String message) {
    report(line, "", message);
    }

    private static void report(int line, String where,
                             String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
    }

    // Lexer initialization
    }
    public Lexer(Reader input) {
        this.input= input;
    }
    // Helper Methods 
    public Symbol getNextSymbol() {
        return null;
    }
    public boolean hasNextSymbol(){
        return current >= input.length();
    }

    List<Symbol> scanSymbols(){
        while(hasNextSymbol){
            start=current;
            scanSymbols();
        }
        symbols.add(new Symbol(Type, " " null, line)) // ajouter variables de symbol
        return symbols;
    }
    // for input
    char advance(){
        return input.charAt(current++);
    }
    // for output
    void addSymbol(SymbolType type){
        addSymbol(type,null);
    }
    void addSymbol(SymbolType type, Object literal){
        String text= input.substring(start,current);
        symbols.add(new Symbol(type,text,literal,line));

    }
    public boolean match(char expected) {
    if (isAtEnd()) return false;
    if (input.charAt(current) != expected) return false;
    current++;
    return true;
  }
  // for comments for example to look ahead without actuallu consuming the char
   public char peek() {
    if (isAtEnd()) return '\0';
    return input.charAt(current);
  }

  // for Strings
  public void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      error(line, "Unterminated string.");
      return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes.
    String value = input.substring(start + 1, current - 1);
    addSymbol(STRING, value);
  }
  // to know if char is a digit
    public boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  } 
  // to handle numbers
  public  void number() {
    while (isDigit(peek())) advance();

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while (isDigit(peek())) advance();
    }

    addSymbol(NUMBER,
        Double.parseDouble(input.substring(start, current)));
  }

  public char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return input.charAt(current + 1);
  } 

// pour les identifiers et les keywords
    public void identifier() {
    while (isAlphaNumeric(peek())) advance();
    String text = input.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addSymbol(IDENTIFIER);
  }

  public boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
           (c >= 'A' && c <= 'Z') ||
            c == '_';
  }

  public boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

    // KEYWORDs
  public static final Map<String, SymbolType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("for",    FOR);
    keywords.put("while",  WHILE);
    keywords.put("if",     IF);
    keywords.put("else",   ELSE);
    keywords.put("return", RETURN);
    keywords.put("false",  FALSE);
    keywords.put("true",   TRUE);

    keywords.put("final", FINAL);
    keywords.put("coll", COLL);
    keywords.put("def", DEF);
    keywords.put("not", NOT);
    keywords.put("ARRAY", ARRAY);
  }

  
    // Scanning 
    void scanSymbol(){
        char c= advance();

        switch(c){
            //Operators & Special Symbols
            case '(': addSymbol(LEFT_PAREN); break;
            case ')': addSymbol(RIGHT_PAREN); break;
            case '{': addSymbol(LEFT_BRACE); break;
            case '}': addSymbol(RIGHT_BRACE); break;
            case ',': addSymbol(COMMA); break;
            case '.': addSymbol(DOT); break;
            case '-': addSymbol(MINUS); break;
            case '+': addSymbol(PLUS); break;
            case ';': addSymbol(SEMICOLON); break;
            case '*': addSymbol(STAR); break;
            case '/': addSymbol(SLASH); break;

            // symbols where its meaning may depend on the symbol before
            case '!':
                    addToken(match('=') ? BANG_EQUAL : BANG);
                    break;
            case '=':
                    addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                    break;
            case '<':
                    addToken(match('=') ? LESS_EQUAL : LESS);
                    break;
            case '>':
                    addToken(match('=') ? GREATER_EQUAL : GREATER);
                    break;
            // Newlines + whitespaces
            case ' ':
            // '\r': // pas dans notre lexer je ctois?
            case '\t':
                        // Ignore whitespace.
                        break;
            case '\n':
                    line++;
                    break;
            // comments 
            case '#': 
                    while (peek() != '\n' && !isAtEnd()) advance();
                    break;

            case '"': string(); break;

            default:
                if(isDigit(c)){
                    number();
                }else if(isAlpha(c)){
                    identifier();
                }else{
                // throw new error 
                error(line, "Unexpected character");
                }
                break;

                }

} 

