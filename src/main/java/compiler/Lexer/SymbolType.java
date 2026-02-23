package compiler.Lexer;

public enum SymbolType {

    //Webographie
    //https://medium.com/@enzojade62/step-by-step-building-a-lexer-in-java-for-tokenizing-source-code-ac4f1d91326f
    //https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html

    //list of keywords
    FINAL,
    COLL,
    FOR,
    WHILE,
    IF,
    ELSE,
    RETURN,
    NOT,
    ARRAY,
    DEF,

    //list of types
    INT_TYPE,
    FLOAT_TYPE,
    BOOL_TYPE,
    STRING_TYPE,

    //list of lITERAL
    INT_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    BOOL_LITERAL,

    IDENTIFIER,

    //list of logical Opererators
    ASSIGN,
    EQUAL,
    NOT_EQUAL,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,


    //lis of boolean Operator
    AND,
    OR,

    //list of artihmetical operators

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,

    ARROW,

    //list of special caractere

    LEFT_PAREN,RIGHT_PAREN,
    LEFT_BRACKET,RIGHT_BRACKET,
    LEFT_BRACE,RIGHT_BRACE,
    DOT,SEMILCOLON,COMMA,

    EOF
}
