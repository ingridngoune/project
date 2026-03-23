import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import org.junit.Test;

import java.io.StringReader;
import compiler.Lexer.Lexer;

public class TestLexer {

    @Test
    public void test() {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNotNull(lexer.getNextSymbol());
    }

    @Test
    public void testSimpleDeclaration() {
        Lexer lexer = new Lexer(new StringReader("INT x = 2;"));
        assertEquals(Token.INT_TYPE, lexer.getNextSymbol().getType());
        Symbol id = lexer.getNextSymbol();
        assertEquals(Token.IDENTIFIER, id.getType());
        assertEquals("x", id.getValue());
        assertEquals(Token.ASSIGN, lexer.getNextSymbol().getType());
        Symbol number = lexer.getNextSymbol();
        assertEquals(Token.INT_LITERAL, number.getType());
        assertEquals(2, number.getValue());
        assertEquals(Token.SEMILCOLON, lexer.getNextSymbol().getType());
        assertEquals(Token.EOF, lexer.getNextSymbol().getType());
    }


    //  float test (.234 case)
    @Test
    public void testFloatStartingWithDot() {
        Lexer lexer = new Lexer(new StringReader(".234"));
        Symbol s = lexer.getNextSymbol();
        assertEquals(Token.FLOAT_LITERAL, s.getType());
        assertEquals(0.234, (Double) s.getValue(), 0.0001);
        assertEquals(Token.EOF, lexer.getNextSymbol().getType());
    }

    // string with escape

    @Test
    public void testStringEscape() {
        Lexer lexer = new Lexer(new StringReader("\"Hello\\nWorld\""));
        Symbol s = lexer.getNextSymbol();
        assertEquals(Token.STRING_LITERAL, s.getType());
        assertEquals("Hello\nWorld", s.getValue());
        assertEquals(Token.EOF, lexer.getNextSymbol().getType());
    }

    //  Boolean test

    @Test
    public void testBooleanLiteral() {
        Lexer lexer = new Lexer(new StringReader("true"));
        Symbol s = lexer.getNextSymbol();
        assertEquals(Token.BOOL_LITERAL, s.getType());
        assertEquals(true, s.getValue());
    }

    // Operator test

    @Test
    public void testOperators() {
        Lexer lexer = new Lexer(new StringReader("== =/= <= >= && ||"));
        assertEquals(Token.EQUAL, lexer.getNextSymbol().getType());
        assertEquals(Token.NOT_EQUAL, lexer.getNextSymbol().getType());
        assertEquals(Token.LESS_EQUAL, lexer.getNextSymbol().getType());
        assertEquals(Token.GREATER_EQUAL, lexer.getNextSymbol().getType());
        assertEquals(Token.AND, lexer.getNextSymbol().getType());
        assertEquals(Token.OR, lexer.getNextSymbol().getType());
    }

    // Whitespace tes
    @Test
    public void testWhitespaceEq() {

        Lexer l1 = new Lexer(new StringReader("INT x = 2;"));
        Lexer l2 = new Lexer(new StringReader("INT   x=2;"));
        while (true) {
            Symbol s1 = l1.getNextSymbol();
            Symbol s2 = l2.getNextSymbol();
            assertEquals(s1.getType(), s2.getType());
            if (s1.getType() == Token.EOF)
                break;
        }
    }

   //invalid character test
    @Test(expected = RuntimeException.class)
    public void testInvalidCharacter() {

        Lexer lexer = new Lexer(new StringReader("@"));
        lexer.getNextSymbol();
    }

  //unterminatd string
    @Test(expected = RuntimeException.class)
    public void testUnterminatedString() {

        Lexer lexer = new Lexer(new StringReader("\"Hello"));
        lexer.getNextSymbol();
    }
}
