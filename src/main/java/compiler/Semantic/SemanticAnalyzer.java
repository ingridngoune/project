package compiler.Semantic;

import com.google.errorprone.annotations.Var;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.*;

public class SemanticAnalyzer {
    // https://www.m-zakeri.ir/Compilers/lectures/07_Semantic-Analysis/
    //https://fr.scribd.com/presentation/723728381/8-Semantic-analysis-scope
private SemanticType currentReturnType;
    public void analyze(ProgramNode program) {
        SymbolTable symbolTable = new SymbolTable(null);
        firstPass(program, symbolTable);
        secondPass(program, symbolTable);

    }

    private void firstPass(ProgramNode program, SymbolTable symbolTable) {
        for (VariableDeclarationNode var : program.getGlobalVariableDeclarations()) {
            SemanticType varType = toSemanticType(var.getType());
            SymbolInfo info = new SymbolInfo(var.getName(), SymbolInfo.Kind.VARIABLE, varType);
            symbolTable.addSymbol(var.getName(), info);
        }
    }

    private void secondPass(ProgramNode program, SymbolTable globalTable) {
        for (VariableDeclarationNode var : program.getGlobalVariableDeclarations()) {
            if (var.getInitValue() != null) {
                SemanticType varType = toSemanticType(var.getType());
                SemanticType valueType = inferType(var.getInitValue(), globalTable);
                if (!varType.equals(valueType)) {
                    throw new RuntimeException("TypeError : can't assign " + valueType + " to " + varType);
                }
            }
        }
    }

    private SemanticType toSemanticType(TypeNode typeNode) {
        return new SemanticType(typeNode.getName(), typeNode.isArray());
    }

    private SemanticType inferType(ExpressionNode expr, SymbolTable symbolTable) {
        if (expr instanceof IntLiteralNode) {
            return new SemanticType("INT", false);
        }
        if (expr instanceof FloatLiteralNode) {
            return new SemanticType("FLOAT", false);
        }
        if (expr instanceof StringLiteralNode) {
            return new SemanticType("STRING", false);
        }
        if (expr instanceof BoolLiteralNode) {
            return new SemanticType("BOOL", false);
        }
        if (expr instanceof IdentifierNode id) {
            SymbolInfo info=symbolTable.getSymbol(id.getName());
            if(info==null){
                throw new RuntimeException("NameError: undefined identifier "+id.getName());
            }
           return info.getType();
        }
        // for binary expression

        if (expr instanceof BinaryExpressionNode bin) {
            SemanticType leftType = inferType(bin.getLeft(), symbolTable);
            SemanticType rightType = inferType(bin.getRight(), symbolTable);
            String operator = bin.getOperator();

            if (operator.equals("PLUS") || operator.equals("MINUS") || operator.equals("MULTIPLY") || operator.equals("DIVIDE") || operator.equals("MODULO")) {
                if (!leftType.isNumeric() || !rightType.isNumeric()) {
                    throw new RuntimeException("OperatorError: " + operator + " required numeric operands");
                }
            if (!leftType.equals(rightType)) {
                throw new RuntimeException("OperatorError: operands of " + operator + " must have the same type");
            }
            return leftType;
        }

        if (operator.equals("AND") || operator.equals("OR")) {
            if (!leftType.isBool() || !rightType.isBool()) {
                throw new RuntimeException("OperatorError: " + operator + " requires BOOL operands");
            }
            return new SemanticType("BOOL", false);
        }
        if (operator.equals("EQUAL") || operator.equals("NOT_EQUAL")) {
            if (!leftType.equals(rightType)) {
                throw new RuntimeException("OperatorError: " + operator + " requires operands of the same type");
            }
            return new SemanticType("BOOL", false);
        }

        if (operator.equals("LESS") || operator.equals("LESS_EQUAL") || operator.equals("GREATER") || operator.equals("GREATER_EQUAL")) {
            if (!leftType.isNumeric() || !rightType.isNumeric()) {
                throw new RuntimeException("OperatorError: " + operator + " requires numeric operands");
            }
            if (!leftType.equals(rightType)) {
                throw new RuntimeException("OperatorError: " + operator + " requires operands of the same type"
                );
            }
            return new SemanticType("BOOL", false);
        }
        throw new RuntimeException("OperatorError: unknown binary operator " + operator);
    }
        // for unary expression
        if(expr instanceof UnaryExpressionNode unary){
            SemanticType expressionType=inferType(unary.getExpression(),symbolTable);
            String operator= unary.getOperator();

            if(operator.equals("NOT")){
                    if(!expressionType.isBool()){
                        throw new RuntimeException("OperatorError: NOT require a Bool operand");
                    }
                    return new SemanticType("BOOL",false);
            }
            if (operator.equals("MINUS")) {
                if (!expressionType.isNumeric()) {
                    throw new RuntimeException("OperatorError:MINUS requires a numeric operand");
                }
                return expressionType;
            }

        }
        if(expr instanceof ArrayAccessNode arrayAccess){
            SemanticType arrayType=inferType(arrayAccess.getArray(),symbolTable);
            SemanticType indexArray=inferType(arrayAccess.getIndex(),symbolTable);
        }

        throw new RuntimeException("SemanticError: unknown expression"+expr.getClass().getSimpleName());
    }


    //check if a expr is assignable
    private boolean isAssignable(ExpressionNode target){
        return target instanceof IdentifierNode || target instanceof ArrayAccessNode || target instanceof FieldAccessNode;
    }


    //check the statements
    private void checkStatement(StatementNode stmt, SymbolTable symbolTable){
        if (stmt instanceof VariableDeclarationNode var){
                checkVariableDeclaration(var,symbolTable);

        } else if(stmt instanceof AssignmentStatementNode assign){
            checkAssigment(assign,symbolTable);

        }else if(stmt instanceof BlockNode block){
            checkBlock(block,symbolTable);

        } else if (stmt instanceof IfStatementNode ifstmt){
            checkIf(ifstmt,symbolTable);

        }else if (stmt instanceof  WhileStatementNode whileStmt){
            checkWhile(whileStmt,symbolTable);
        }
        else if (stmt instanceof  ForStatementNode forStmt){
            checkFor(forStmt,symbolTable);
        }
        else if (stmt instanceof ReturnStatementNode returnStmt){
            checkReturn(returnStmt,symbolTable);
        }
        else{
            throw new RuntimeException("SemancticError: unknown statement "+stmt+getClass().getSimpleName());
        }

    }

    private void checkFor(ForStatementNode forStmt, SymbolTable symbolTable) {

    }

    private void checkVariableDeclaration(VariableDeclarationNode var,SymbolTable symbolTable){
        SemanticType declaredType=toSemanticType(var.getType());
        if(var.getInitValue()!=null){
            SemanticType initValueType=inferType(var.getInitValue(),symbolTable);
            if(!declaredType.equals(initValueType)) {
                throw new RuntimeException("TypeErro: can't assign" + initValueType + " to " + declaredType);
            }
        }
        SymbolInfo info = new SymbolInfo(var.getName(), SymbolInfo.Kind.VARIABLE,declaredType);
        symbolTable.addSymbol(var.getName(),info);

    }

    private void checkAssigment(AssignmentStatementNode assign,SymbolTable symbolTable){
        if(!isAssignable(assign.getTarget())) {
            throw new RuntimeException("TypeError: invalid Assignement Target");
        }
        SemanticType leftType=inferType(assign.getTarget(),symbolTable);
        SemanticType rightType=inferType(assign.getValue(),symbolTable);

        if (!leftType.equals(rightType)){
            throw new RuntimeException("TypeError: can't assign "+rightType+" to "+leftType);
        }
    }

    private void  checkIf(IfStatementNode ifStmt,SymbolTable symbolTable){
        SemanticType conditionType=inferType(ifStmt.getCondition(),symbolTable);
        if(!conditionType.isBool()){
            throw new RuntimeException("MissingConditionError: if condition must be BOOL");
        }
        checkBlock(ifStmt.getThenBlock(),symbolTable);

        if(ifStmt.getElseBlock() !=null){
            checkBlock(ifStmt.getElseBlock(),symbolTable);
        }

    }

    private void checkWhile(WhileStatementNode whileStmt,SymbolTable symbolTable){
        SemanticType conditionType=inferType(whileStmt.getCondition(),symbolTable);

        if(!conditionType.isBool()){
            throw  new RuntimeException("MissingConditionError : while condition must be BOOL");
        }
        checkBlock(whileStmt.getBody(),symbolTable);

    }

    //check the block
    private void checkBlock(BlockNode block,SymbolTable parentTable){
        SymbolTable localTable=new SymbolTable(parentTable);
        for (StatementNode stmt:block.getStatements()){
            checkStatement(stmt,localTable);
        }
    }

    private void checkReturn(ReturnStatementNode returnStmt,SymbolTable symbolTable){
        if(currentReturnType == null){
            throw  new RuntimeException("ReturnError : return is called outside a function" );
        }

        if(returnStmt.getExpression()==null){
            throw  new RuntimeException("ReturnError: function must return a value");

        }
        SemanticType returnedType=inferType(returnStmt.getExpression(),symbolTable);

        if (!currentReturnType.equals(returnedType)) {
            throw new RuntimeException("ReturnError: expected " + currentReturnType + " but found " + returnedType);
        }
    }
}
