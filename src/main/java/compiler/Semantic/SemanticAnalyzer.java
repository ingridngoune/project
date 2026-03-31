package compiler.Semantic;

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
                SemanticType exprType = checkExpression(var.getInitValue(), globalTable);
                if (!varType.equals(exprType)) {
                    throw new RuntimeException("TypeError : can't assign " + exprType + " to " + varType);
                }
            }
        }
    }

    private SemanticType toSemanticType(TypeNode typeNode) {
        return new SemanticType(typeNode.getName(), typeNode.isArray());
    }

    private SemanticType checkExpression(ExpressionNode expr, SymbolTable symbolTable) {
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

        if (expr instanceof BinaryExpressionNode bin) {
            SemanticType leftType = checkExpression(bin.getLeft(), symbolTable);
            SemanticType rightType = checkExpression(bin.getRight(), symbolTable);
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
        throw new RuntimeException("SemanticError: unknown expression"+expr.getClass().getSimpleName());
    }
}
