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

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }


    //-------------------helpers-------------------

    private void nextSymbol() {
        current = lexer.getNextSymbol();
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
            throw new RuntimeException("syntax error: expected" + expectedToken + "but found" + current.getType());
        }
        nextSymbol();
    }

    private boolean isTypeStart(Token token) {
        return token == Token.INT_TYPE || token == Token.FLOAT_TYPE || token == Token.BOOL_TYPE || token == Token.STRING_TYPE || token == Token.COLLECTION_IDENTIFIER;
    }

    public ProgramNode getAST() {
        nextSymbol();
        return parseProgram();
    }


    //-----------------parse program-----------------------
    private ProgramNode parseProgram() {
        List<ConstantDeclarationNode> constantDeclarations = parseConstantDeclarations();
        List<CollectionDeclarationNode> collectionDeclarations = parseCollectionDeclarations();
        List<VariableDeclarationNode> globalVariableDeclarations = parseGlobalVariableDeclarations();
        List<FunctionDeclarationNode> functionDeclarations = parseFunctionDeclarations();
        consume(Token.EOF);
        return new ProgramNode(constantDeclarations, collectionDeclarations, globalVariableDeclarations, functionDeclarations);
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
        List<CollectionDeclarationNode> collectionDeclarations = new ArrayList<>();
        while (current.getType() == Token.COLL) {
            collectionDeclarations.add(parseCollectionDeclaration());
        }
        return collectionDeclarations;
    }

    private CollectionDeclarationNode parseCollectionDeclaration() {
        consume(Token.COLL);
        String name = (String) current.getValue();
        consume(Token.COLLECTION_IDENTIFIER);
        consume(Token.LEFT_BRACE);
        List<FieldDeclarationNode> fields = parseFieldDeclarations();
        consume(Token.RIGHT_BRACE);
        return new CollectionDeclarationNode(name, fields);
    }

    private List<FieldDeclarationNode> parseFieldDeclarations() {
        List<FieldDeclarationNode> fields = new ArrayList<>();
        while (isTypeStart(current.getType())) {
            fields.add(parseFieldDeclaration());
        }
        return fields;
    }

    private FieldDeclarationNode parseFieldDeclaration() {
        TypeNode type = parseType();
        String name = (String) current.getValue();
        consume(Token.IDENTIFIER);
        consume(Token.SEMILCOLON);
        return new FieldDeclarationNode(type, name);
    }


    //-------parse variable declaration-----------
    private List<VariableDeclarationNode> parseGlobalVariableDeclarations() {
        List<VariableDeclarationNode> globalVariables = new ArrayList<>();

        while (isTypeStart(current.getType())) {
            globalVariables.add(parseVariableDeclaration());
        }

        return globalVariables;
    }

    private VariableDeclarationNode parseVariableDeclaration() {
        TypeNode type = parseType();
        String name = (String) current.getValue();
        consume(Token.IDENTIFIER);
        ExpressionNode initializer = null;
        if (checkAndConsume(Token.ASSIGN)) {
            initializer = parseInitializer();
        }
        consume(Token.SEMILCOLON);
        return new VariableDeclarationNode(type, name, initializer);
    }

    private ExpressionNode parseInitializer() {
        if (isTypeStart(current.getType())) {
            return parseArrayCreation();
        }
        return parseExpression();
    }

    private ExpressionNode parseArrayCreation() {
        TypeNode baseType = parseBaseType();
        consume(Token.ARRAY);
        consume(Token.LEFT_BRACKET);
        ExpressionNode size = parseExpression();
        consume(Token.RIGHT_BRACKET);
        return new ArrayCreationNode(baseType, size);
    }

    private TypeNode parseBaseType() {
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
        if (current.getType() == Token.COLLECTION_IDENTIFIER) {
            String name = (String) current.getValue();
            consume(Token.COLLECTION_IDENTIFIER);
            return new TypeNode(name, false);
        }
        throw new RuntimeException(
                "Syntax error: expected a base type but found " + current.getType()
        );
    }


    //----------------parse function declaration-----------------
    private List<FunctionDeclarationNode> parseFunctionDeclarations() {
        List<FunctionDeclarationNode> functionDeclarations = new ArrayList<>();
        while (current.getType() == Token.DEF) {
            functionDeclarations.add(parseFunctionDeclaration());
        }
        return functionDeclarations;
    }

    private FunctionDeclarationNode parseFunctionDeclaration() {
        consume(Token.DEF);
        TypeNode returnType = null;
        String name;
        // cas de  fonction sans type de retour
        if (current.getType() == Token.IDENTIFIER) {
            name = (String) current.getValue();
            consume(Token.IDENTIFIER);
        }
        // cas fonction avec type de retour
        else {
            returnType = parseType();
            name = (String) current.getValue();
            consume(Token.IDENTIFIER);
        }
        consume(Token.LEFT_PAREN);
        List<ParameterNode> parameters = parseOptionalParameters();
        consume(Token.RIGHT_PAREN);
        BlockNode body = parseBlock();
        return new FunctionDeclarationNode(returnType, name, parameters, body);
    }

    private List<ParameterNode> parseOptionalParameters() {
        return null;
    }


    //------------parse type-----------
    private TypeNode parseType() {
        String name;
        if (current.getType() == Token.INT_TYPE) {
            name = "INT";
            consume(Token.INT_TYPE);
        } else if (current.getType() == Token.FLOAT_TYPE) {
            name = "FLOAT";
            consume(Token.FLOAT_TYPE);
        } else if (current.getType() == Token.BOOL_TYPE) {
            name = "BOOL";
            consume(Token.BOOL_TYPE);
        } else if (current.getType() == Token.STRING_TYPE) {
            name = "STRING";
            consume(Token.STRING_TYPE);
        } else if (current.getType() == Token.COLLECTION_IDENTIFIER) {
            name = (String) current.getValue();
            consume(Token.COLLECTION_IDENTIFIER);
        } else {
            throw new RuntimeException("syntx error, expected a type but found " + current.getType());
        }
        boolean isArray = false;
        if (checkAndConsume(Token.LEFT_BRACKET)) {
            consume(Token.RIGHT_BRACKET);
            isArray = true;
        }
        return new TypeNode(name, isArray);
    }

    //-------------parse primitive type -------------
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
        throw new RuntimeException("syntax error:  expected a primitive type but found " + current.getType());

    }


    //--------------parse statements-------------


    private BlockNode parseBlock() {
        return null;
    }





    //----------------parse expression-----------------------
    private ExpressionNode parseExpression() {
        return null;
    }
}
