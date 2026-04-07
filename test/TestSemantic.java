import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.AST.ProgramNode;
import compiler.Semantic.SemanticAnalyzer;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestSemantic {

    private void assertSemanticError(String input, String expectedKeyword) {
        try {
            Parser parser = new Parser(new Lexer(new StringReader(input)));
            ProgramNode ast = parser.getAST();
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(ast);
            fail("Expected semantic error containing: " + expectedKeyword);
        } catch (RuntimeException e) {
            assertTrue("Actual message was: " + e.getMessage(),
                    e.getMessage().contains(expectedKeyword));
        }
    }

    private void assertNoSemanticError(String input) {
        try {
            Parser parser = new Parser(new Lexer(new StringReader(input)));
            ProgramNode ast = parser.getAST();
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(ast);
        } catch (RuntimeException e) {
            fail("Unexpected semantic error: " + e.getMessage());
        }
    }

    @Test
    public void testValidProgram() {
        String input = """
                def INT add(INT a, INT b) {
                    return a + b;
                }

                def main() {
                    INT x = add(2, 3);
                }
                """;

        assertNoSemanticError(input);
    }

    @Test
    public void testTypeError() {
        String input = """
                def main() {
                    INT value = "Bonjour";
                }
                """;

        assertSemanticError(input, "TypeError");
    }

    @Test
    public void testCollectionError() {
        String input = """ 
                coll Point {
                    INT x;
                    INT y;
                }

                coll Point {
                    BOOL z;
                }

                def main() {
                }
                """;

        assertSemanticError(input, "CollectionError");
    }

    @Test
    public void testOperatorError() {
        String input = """
                def main() {
                    INT value = 1;
                    BOOL value2 = true;
                    INT test = value + value2;
                }
                """;

        assertSemanticError(input, "OperatorError");
    }

    @Test
    public void testArgumentError() {
        String input = """
                def INT square(INT v) {
                    return v * v;
                }

                def main() {
                    INT value = 2;
                    BOOL test = true;
                    INT result = square(test);
                }
                """;

        assertSemanticError(input, "ArgumentError");
    }

    @Test
    public void testMissingConditionError() {
        String input = """
                def INT square(INT v) {
                    return v * v;
                }

                def main() {
                    INT value = 2;
                    INT result = square(value);
                    STRING message = "Bonjour";
                    while (message) {
                        result = result + 1;
                    }
                }
                """;

        assertSemanticError(input, "MissingConditionError");
    }

    @Test
    public void testReturnError() {
        String input = """
                def BOOL square(INT v) {
                    return v * v;
                }

                def main() {
                    INT value = 2;
                    BOOL result = square(value);
                    STRING message = "Bonjour";
                }
                """;

        assertSemanticError(input, "ReturnError");
    }

    @Test
    public void testScopeError() {
        String input = """
                def INT square(INT v) {
                    INT misdirection = v;
                    return v * v;
                }

                def main() {
                    INT value = 2;
                    INT result = square(value);
                    INT misdirection2 = misdirection;
                }
                """;

        assertSemanticError(input, "ScopeError");
    }
}