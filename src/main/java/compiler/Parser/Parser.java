package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.AST.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Lexer lexer;
    private Symbol current;

    public Parser(Lexer lexer){
        this.lexer=lexer;
    }


    //-------------------helpers-------------------

    private void nextSymbol(){
        current=lexer.getNextSymbol();
    }

    private boolean checkAndConsume(Token expectedToken) {
        if (current.getType() == expectedToken) {
            nextSymbol();
            return true;
        }
        return false;
    }

    private void consume(Token expectedToken) {
        if (current.getType() != expectedToken) {
            throw new RuntimeException("Syntax error: expected" + expectedToken + "but found" + current.getType());
        }
        nextSymbol();
    }

    public ProgramNode getAST(){
        nextSymbol();
        return parseProgram();
    }



    //-----------------parse program-----------------------
    private ProgramNode parseProgram(){
        List<ConstantDeclarationNode> constantDeclarations = parseConstantDeclarations();
        List<CollectionDeclarationNode> collectionDeclarations = parseCollectionDeclarations();
        List<VariableDeclarationNode> globalVariableDeclarations = parseGlobalVariableDeclarations();
        List<FunctionDeclarationNode> functionDeclarations = parseFunctionDeclarations();
        consume(Token.EOF);
        return new ProgramNode(constantDeclarations,collectionDeclarations, globalVariableDeclarations,functionDeclarations);
    }

    //--------------------parse constants declarations ---------------

    private List<ConstantDeclarationNode> parseConstantDeclarations() {
        List<ConstantDeclarationNode> constantDeclarations = new ArrayList<>();
        while (current.getType() == Token.FINAL) {
            constantDeclarations.add(parseConstantDeclaration());
        }
        return constantDeclarations;
    }
    private ConstantDeclarationNode parseConstantDeclaration() {
        consume(Token.FINAL);
        TypeNode type = parsePrimitiveType();
        String name = (String) current.getValue();
        consume(Token.IDENTIFIER);
        consume(Token.ASSIGN);
        ExpressionNode value = parseExpression();
        consume(Token.SEMILCOLON);
        return new ConstantDeclarationNode(type, name, value);
    }


    //-----------parse collection declaration---------------
    private List<CollectionDeclarationNode> parseCollectionDeclarations() {
        return new ArrayList<>();
    }



    //-------parse variable declaration-----------
    private List<VariableDeclarationNode> parseGlobalVariableDeclarations() {
        return new ArrayList<>();
    }



    //----------------parse function declaration-----------------
    private List<FunctionDeclarationNode> parseFunctionDeclarations() {
        return new ArrayList<>();
    }


    //------------parse type
    private TypeNode parsePrimitiveType() {

        if (current.getType() == Token.INT_TYPE) {
            consume(Token.INT_TYPE);
            return new TypeNode("INT", false);
        }

        if (current.getType() == Token.FLOAT_TYPE) {
            consume(Token.FLOAT_TYPE);
            return new TypeNode("FLOAT", false);
        }

        if (current.getType() == Token.BOOL_TYPE) {
            consume(Token.BOOL_TYPE);
            return new TypeNode("BOOL", false);
        }

        if (current.getType() == Token.STRING_TYPE) {
            consume(Token.STRING_TYPE);
            return new TypeNode("STRING", false);
        }

        throw new RuntimeException("syntax error: expected a primitive type but found " + current.getType());

    }


    //--------------parse statements-------------




















    //----------------parse expression-----------------------
    private ExpressionNode parseExpression() {
        return null;
    }
}
