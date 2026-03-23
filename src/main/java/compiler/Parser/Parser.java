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
    private Symbol next;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }


    //-------------------helpers-------------------
    public ProgramNode getAST() {
        current =lexer.getNextSymbol();
        next=lexer.getNextSymbol();
        return parseProgram();
    }

    private void nextSymbol() {
        current=next;
        next = lexer.getNextSymbol();
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

    private boolean isStatementStart(Token token) {
        return isTypeStart(token)  || token == Token.IDENTIFIER  || token == Token.LEFT_BRACE  || token == Token.IF  || token == Token.WHILE  || token == Token.FOR  || token == Token.RETURN;
    }

    private boolean isExpressionStart(Token token) {
        return token == Token.INT_LITERAL || token == Token.FLOAT_LITERAL || token == Token.STRING_LITERAL || token == Token.BOOL_LITERAL || token == Token.IDENTIFIER || token == Token.LEFT_PAREN || token == Token.NOT || token == Token.MINUS || token == Token.COLLECTION_IDENTIFIER;
    }

    private boolean isBaseTypeStart(Token token) {
        return token == Token.INT_TYPE  || token == Token.FLOAT_TYPE  || token == Token.BOOL_TYPE  || token == Token.STRING_TYPE  || token == Token.COLLECTION_IDENTIFIER;
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
        while (current.getType()==Token.COLL) {
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
        if (isBaseTypeStart(current.getType()) && next.getType() == Token.ARRAY) {
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
        TypeNode returnType = parseOptionalReturn();
        String name = (String) current.getValue();
        consume(Token.IDENTIFIER);
        consume(Token.LEFT_PAREN);
        List<ParameterNode> parameters = parseOptionalParameterList();
        consume(Token.RIGHT_PAREN);
        BlockNode body = parseBlock();
        return new FunctionDeclarationNode(returnType, name, parameters, body);
    }

    private List<ParameterNode> parseOptionalParameterList() {
        if (isTypeStart(current.getType())) {
            return parseParameterList();
        }
        return new ArrayList<>();
    }

    private List<ParameterNode> parseParameterList() {
        List<ParameterNode> parameters = new ArrayList<>();
        parameters.add(parseParameter());
        while (checkAndConsume(Token.COMMA)) {
            parameters.add(parseParameter());
        }
        return parameters;
    }

    private ParameterNode parseParameter() {
        TypeNode type = parseType();
        String name = (String) current.getValue();
        consume(Token.IDENTIFIER);
        return new ParameterNode(type, name);
    }

    private TypeNode parseOptionalReturn() {
        if (isTypeStart(current.getType())) {
            return parseType();
        }
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
            throw new RuntimeException("syntx error  expected a type but found " + current.getType());
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
        consume(Token.LEFT_BRACE);
        List<StatementNode> statements = parseStatementList();
        consume(Token.RIGHT_BRACE);
        return new BlockNode(statements);
    }

    private List<StatementNode> parseStatementList() {
        List<StatementNode> statements = new ArrayList<>();
        while (isStatementStart(current.getType())) {
            statements.add(parseStatement());
        }
        return statements;
    }

    private StatementNode parseStatement() {
        Token token = current.getType();
        if (isTypeStart(token)) {
            return parseVariableDeclaration();
        }
        if (token == Token.LEFT_BRACE) {
            return parseBlock();
        }
        if (token == Token.RETURN) {
            return parseReturnStatement();
        }
        if (token == Token.IF) {
            return parseIfStatement();
        }
        if (token == Token.WHILE) {
            return parseWhileStatement();
        }
        if (token == Token.FOR) {
            return parseForStatement();
        }
        if (token == Token.IDENTIFIER) {
            return parseAssignmentOrExpressionStatement();
        }
        throw new RuntimeException("Syntax error expected a statement but found " + current.getType());
    }

    private StatementNode parseAssignmentOrExpressionStatement() {
        ExpressionNode left = parseAccessExpression();
        if (checkAndConsume(Token.ASSIGN)) {
            ExpressionNode value = parseInitializer();
            consume(Token.SEMILCOLON);
            return new AssignmentStatementNode(left, value);
        }
        consume(Token.SEMILCOLON);
        return new ExpressionStatementNode(left);
    }


    private StatementNode parseForStatement() {
        consume(Token.FOR);
        consume(Token.LEFT_PAREN);
        TypeNode variableType = parseType();
        String variableName = (String) current.getValue();
        consume(Token.IDENTIFIER);
        consume(Token.SEMILCOLON);
        ExpressionNode startExpression = parseExpression();
        consume(Token.ARROW);
        ExpressionNode endExpression = parseExpression();
        consume(Token.SEMILCOLON);
        ExpressionNode stepExpression = parseExpression();
        consume(Token.RIGHT_PAREN);
        BlockNode body = parseBlock();
        return new ForStatementNode(variableType, variableName, startExpression, endExpression, stepExpression, body);
    }

    private StatementNode parseWhileStatement() {
        consume(Token.WHILE);
        consume(Token.LEFT_PAREN);
        ExpressionNode condition = parseExpression();
        consume(Token.RIGHT_PAREN);
        BlockNode body = parseBlock();
        return new WhileStatementNode(condition, body);
    }

    private StatementNode parseIfStatement() {
        consume(Token.IF);
        consume(Token.LEFT_PAREN);
        ExpressionNode condition = parseExpression();
        consume(Token.RIGHT_PAREN);
        BlockNode thenBlock = parseBlock();
        BlockNode elseBlock = null;
        if (checkAndConsume(Token.ELSE)) {
            elseBlock = parseBlock();
        }
        return new IfStatementNode(condition, thenBlock, elseBlock);
    }

    private StatementNode parseReturnStatement() {
        consume(Token.RETURN);
        ExpressionNode value = null;
        if (isExpressionStart(current.getType())) {
            value = parseExpression();
        }
        consume(Token.SEMILCOLON);
        return new ReturnStatementNode(value);

    }

    //----------------parse expression-----------------------
    private ExpressionNode parseExpression() {
        return parseOrExpression();
    }

    private ExpressionNode parseOrExpression() {
        ExpressionNode left = parseAndExpression();
        while (current.getType() == Token.OR) {
            consume(Token.OR);
            ExpressionNode right = parseAndExpression();
            left = new BinaryExpressionNode(left, "OR", right);
        }
        return left;
    }

    private ExpressionNode parseAndExpression() {
        ExpressionNode left = parseComparisonExpression();
        while (current.getType() == Token.AND) {
            consume(Token.AND);
            ExpressionNode right = parseComparisonExpression();
            left = new BinaryExpressionNode(left, "AND", right);
        }
        return left;
    }

    private ExpressionNode parseComparisonExpression() {
        ExpressionNode left = parseAdditiveExpression();
        if (current.getType() == Token.EQUAL) {
            consume(Token.EQUAL);
            return new BinaryExpressionNode(left, "EQUAL", parseAdditiveExpression());
        }
        if (current.getType() == Token.NOT_EQUAL) {
            consume(Token.NOT_EQUAL);
            return new BinaryExpressionNode(left, "NOT_EQUAL", parseAdditiveExpression());
        }
        if (current.getType() == Token.LESS) {
            consume(Token.LESS);
            return new BinaryExpressionNode(left, "LESS", parseAdditiveExpression());
        }
        if (current.getType() == Token.LESS_EQUAL) {
            consume(Token.LESS_EQUAL);
            return new BinaryExpressionNode(left, "LESS_EQUAL", parseAdditiveExpression());
        }
        if (current.getType() == Token.GREATER) {
            consume(Token.GREATER);
            return new BinaryExpressionNode(left, "GREATER", parseAdditiveExpression());
        }
        if (current.getType() == Token.GREATER_EQUAL) {
            consume(Token.GREATER_EQUAL);
            return new BinaryExpressionNode(left, "GREATER_EQUAL", parseAdditiveExpression());
        }
        return left;
    }

    private ExpressionNode parseAdditiveExpression() {
        ExpressionNode left = parseMultiplicativeExpression();
        while (current.getType() == Token.PLUS || current.getType() == Token.MINUS) {
            if (current.getType() == Token.PLUS) {
                consume(Token.PLUS);
                left = new BinaryExpressionNode(left, "PLUS", parseMultiplicativeExpression());
            } else {
                consume(Token.MINUS);
                left = new BinaryExpressionNode(left, "MINUS", parseMultiplicativeExpression());
            }
        }
        return left;
    }

    private ExpressionNode parseMultiplicativeExpression() {
        ExpressionNode left = parseUnaryExpression();
        while (current.getType() == Token.MULTIPLY || current.getType() == Token.DIVIDE || current.getType() == Token.MODULO) {
            if (current.getType() == Token.MULTIPLY) {
                consume(Token.MULTIPLY);
                left = new BinaryExpressionNode(left, "MULTIPLY", parseUnaryExpression());
            } else if (current.getType() == Token.DIVIDE) {
                consume(Token.DIVIDE);
                left = new BinaryExpressionNode(left, "DIVIDE", parseUnaryExpression());
            } else {
                consume(Token.MODULO);
                left = new BinaryExpressionNode(left, "MODULO", parseUnaryExpression());
            }
        }

        return left;
    }

    private ExpressionNode parseUnaryExpression() {
        if (current.getType() == Token.NOT) {
            consume(Token.NOT);
            return new UnaryExpressionNode("NOT", parseUnaryExpression());
        }
        if (current.getType() == Token.MINUS) {
            consume(Token.MINUS);
            return new UnaryExpressionNode("MINUS", parseUnaryExpression());
        }
        return parseAccessExpression();
    }

    private ExpressionNode parseAccessExpression() {
        ExpressionNode left = parsePrimaryExpression();
        while (current.getType() == Token.LEFT_PAREN || current.getType() == Token.LEFT_BRACKET || current.getType() == Token.DOT) {
            if (current.getType() == Token.LEFT_PAREN) {
                consume(Token.LEFT_PAREN);
                List<ExpressionNode> arguments = parseOptionalArgumentList();
                consume(Token.RIGHT_PAREN);
                left = new FunctionCallNode(left, arguments);
            } else if (current.getType() == Token.LEFT_BRACKET) {
                consume(Token.LEFT_BRACKET);
                ExpressionNode index = parseExpression();
                consume(Token.RIGHT_BRACKET);
                left = new ArrayAccessNode(left, index);
            } else {
                consume(Token.DOT);
                String fieldName = (String) current.getValue();
                consume(Token.IDENTIFIER);
                left = new FieldAccessNode(left, fieldName);
            }
        }

        return left;
    }

    private List<ExpressionNode> parseOptionalArgumentList() {
        if (isExpressionStart(current.getType())) {
            return parseArgumentList();
        }
        return new ArrayList<>();
    }

    private List<ExpressionNode> parseArgumentList() {
        List<ExpressionNode> arguments = new ArrayList<>();
        arguments.add(parseExpression());
        while (checkAndConsume(Token.COMMA)) {
            arguments.add(parseExpression());
        }

        return arguments;
    }

    private ExpressionNode parsePrimaryExpression() {
        if (isBaseTypeStart(current.getType()) && next.getType() == Token.ARRAY) {
            return parseArrayCreation();
        }
        if (current.getType() == Token.INT_LITERAL) {
            int value = ((Number) current.getValue()).intValue();
            consume(Token.INT_LITERAL);
            return new IntLiteralNode(value);
        }
        if (current.getType() == Token.FLOAT_LITERAL) {
            float value = ((Number) current.getValue()).floatValue();
            consume(Token.FLOAT_LITERAL);
            return new FloatLiteralNode(value);
        }
        if (current.getType() == Token.STRING_LITERAL) {
            String value = (String) current.getValue();
            consume(Token.STRING_LITERAL);
            return new StringLiteralNode(value);
        }
        if (current.getType() == Token.BOOL_LITERAL) {
            boolean value = (Boolean) current.getValue();
            consume(Token.BOOL_LITERAL);
            return new BoolLiteralNode(value);
        }
        if (current.getType() == Token.IDENTIFIER) {
            String name = (String) current.getValue();
            consume(Token.IDENTIFIER);
            return new IdentifierNode(name);
        }
        if (current.getType() == Token.COLLECTION_IDENTIFIER) {
            String name = (String) current.getValue();
            consume(Token.COLLECTION_IDENTIFIER);
            return new IdentifierNode(name);
        }
        if (current.getType() == Token.LEFT_PAREN) {
            consume(Token.LEFT_PAREN);
            ExpressionNode expression = parseExpression();
            consume(Token.RIGHT_PAREN);
            return expression;
        }

        throw new RuntimeException("syntax errr: expected a primary expression but found " + current.getType());
    }


}
