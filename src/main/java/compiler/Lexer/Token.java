package compiler.Lexer;

public enum Token {


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
    COLLECTION_IDENTIFIER,

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
