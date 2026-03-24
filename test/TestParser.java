import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.AST.ProgramNode;
import org.junit.Test;

import java.io.StringReader;

public class TestParser {

    @Test
    public void test() {
        String input = "INT x = 2;";
        StringReader reader = new StringReader(input);
        Parser parser = new Parser(new Lexer(reader));
        assertNotNull(parser.getAST());
    }

    @Test
    public void testSimpleVariableDeclaration() {
        Parser parser = new Parser(new Lexer(new StringReader("INT x = 2;")));
        ProgramNode ast = parser.getAST();
        assertNotNull(ast);
        assertEquals(0, ast.getConstantDeclarations().size());
        assertEquals(0, ast.getCollectionDeclarations().size());
        assertEquals(1, ast.getGlobalVariableDeclarations().size());
        assertEquals(0, ast.getFunctionDeclarations().size());
    }

    @Test
    public void testFunctionDeclaration() {
        Parser parser = new Parser(new Lexer(new StringReader("def INT add(INT a, INT b) { return a + b; }")));
        ProgramNode ast = parser.getAST();
        assertNotNull(ast);
        assertEquals(0, ast.getConstantDeclarations().size());
        assertEquals(0, ast.getCollectionDeclarations().size());
        assertEquals(0, ast.getGlobalVariableDeclarations().size());
        assertEquals(1, ast.getFunctionDeclarations().size());
    }

    @Test
    public void testCollectionDeclaration() {
        Parser parser = new Parser(new Lexer(new StringReader("coll Person { INT age; STRING name; }")));
        ProgramNode ast = parser.getAST();
        assertNotNull(ast);
        assertEquals(0, ast.getConstantDeclarations().size());
        assertEquals(1, ast.getCollectionDeclarations().size());
        assertEquals(0, ast.getGlobalVariableDeclarations().size());
        assertEquals(0, ast.getFunctionDeclarations().size());
    }

    @Test
    public void testMultipleDeclarations() {
        Parser parser = new Parser(new Lexer(new StringReader("final INT a = 1; INT x = 2; def INT f() { return x; }")));
        ProgramNode ast = parser.getAST();
        assertNotNull(ast);
        assertEquals(1, ast.getConstantDeclarations().size());
        assertEquals(0, ast.getCollectionDeclarations().size());
        assertEquals(1, ast.getGlobalVariableDeclarations().size());
        assertEquals(1, ast.getFunctionDeclarations().size());
    }

    @Test(expected = RuntimeException.class)
    public void testMissingSemicolon() {
        Parser parser = new Parser(new Lexer(new StringReader("INT x = 2")));
        parser.getAST();
    }

    @Test(expected = RuntimeException.class)
    public void testMissingAssign() {
        Parser parser = new Parser(new Lexer(new StringReader("INT x 2;")));
        parser.getAST();
    }
    @Test(expected = RuntimeException.class)
    public void testUnterminatedBlock() {
        Parser parser = new Parser(new Lexer(new StringReader("def INT add(INT a, INT b) { return a + b; ")));
        parser.getAST();
    }
}