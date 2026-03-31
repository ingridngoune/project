package compiler.Semanctic;

import compiler.Lexer.Symbol;
import compiler.Parser.AST.*;

public class SemanticAnalyzer {
    // https://www.m-zakeri.ir/Compilers/lectures/07_Semantic-Analysis/
    //https://fr.scribd.com/presentation/723728381/8-Semantic-analysis-scope

    public void analyze(ProgramNode program){
        SymbolTable globalTable=new SymbolTable(null);
        for (VariableDeclarationNode var:program.getGlobalVariableDeclarations()){
            SemanticType varType=toSemanticType(var.getType());

            SymbolInfo info=new SymbolInfo(var.getName(),SymbolInfo.Kind.VARIABLE,varType);

            globalTable.addSymbol(var.getName(),info);

            if(var.getInitValue()!=null){
                SemanticType exprType=checkExpression(var.getInitValue(),globalTable);

                if(!varType.equals(exprType)){
                    throw new RuntimeException("Type Error : can't assign "+exprType+" to "+varType);
                }
            }
        }



    }
    private SemanticType toSemanticType(TypeNode typeNode) {
        return new SemanticType(typeNode.getName(), typeNode.isArray());
    }

    private SemanticType checkExpression(ExpressionNode expr,SymbolTable table){
        if(expr instanceof IntLiteralNode){
            return new SemanticType("INT",false);
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
            return table.getSymbol(id.getName()).getType();
        }
        throw new RuntimeException(
                "semantic error: unknown expression" + expr.getClass().getSimpleName()
        );

    }
}
