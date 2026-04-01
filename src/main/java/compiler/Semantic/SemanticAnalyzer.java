package compiler.Semantic;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.*;

public class SemanticAnalyzer {
    // https://www.m-zakeri.ir/Compilers/lectures/07_Semantic-Analysis/
    //https://fr.scribd.com/presentation/723728381/8-Semantic-analysis-scope

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
            return symbolTable.getSymbol(id.getName()).getType();
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

        throw new RuntimeException("SemanticError: unknown expression"+expr.getClass().getSimpleName());
    }

    private boolean isAssignable(ExpressionNode target){
        return target instanceof IdentifierNode || target instanceof ArrayAccessNode || target instanceof FieldAccessNode;
    }


    private void checkStatement(StatementNode stmt, SymbolTable symbolTable){
        if (stmt instanceof VariableDeclarationNode var){
            SemanticType declaredType=toSemanticType(var.getType());
            SymbolInfo info = new SymbolInfo(var.getName(), SymbolInfo.Kind.VARIABLE,declaredType);

            symbolTable.addSymbol(var.getName(),info);

            if(var.getInitValue()!=null){
                SemanticType initValueType=inferType(var.getInitValue(),symbolTable);
                if(!declaredType.equals(initValueType)){
                    throw  new RuntimeException("TypeErro: can't assign"+initValueType+" to "+declaredType);
                }
            }
        }else if(stmt instanceof AssignmentStatementNode assign){
            if(!isAssignable(assign.getTarget())) {
                throw new RuntimeException("TypeError: invalid Assignement Target");
            }
            SemanticType leftType=inferType(assign.getTarget(),symbolTable);
            SemanticType rightType=inferType(assign.getValue(),symbolTable);

            if (!leftType.equals(rightType)){
                throw new RuntimeException("TypeError: can't assign "+rightType+" to "+leftType);
            }
            else{
                throw new RuntimeException("SemancticError: unknown statement "+stmt+getClass().getSimpleName());
            }
        }

    }
}
