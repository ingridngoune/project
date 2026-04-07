package compiler.Semantic;

import com.google.errorprone.annotations.Var;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.*;

import java.util.*;

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
        addBuiltFunctions(symbolTable);
        for(VariableDeclarationNode var : program.getGlobalVariableDeclarations()) {
            SemanticType varType = toSemanticType(var.getType());
            SymbolInfo info = new SymbolInfo(var.getName(), SymbolInfo.Kind.VARIABLE, varType);
            symbolTable.addSymbol(var.getName(), info);
        }
        for (CollectionDeclarationNode collection : program.getCollectionDeclarations()) {
            Map<String, SemanticType> fields = checkCollection(collection, symbolTable);
            SymbolInfo info = new SymbolInfo(collection.getName(), SymbolInfo.Kind.COLLECTION, new SemanticType(collection.getName(), false), null, fields);
            symbolTable.addSymbol(collection.getName(), info);
        }
        for(FunctionDeclarationNode function : program.getFunctionDeclarations()) {
            List<SemanticType> parameterTypes = new ArrayList<>();
            for (ParameterNode parameter : function.getParameters()) {
                parameterTypes.add(toSemanticType(parameter.getType()));
            }
            SemanticType returnType = null;
            if (function.getReturnType() != null) {
                returnType = toSemanticType(function.getReturnType());
            }
            SymbolInfo info = new SymbolInfo(function.getName(), SymbolInfo.Kind.FUNCTION, returnType, parameterTypes, null);
            symbolTable.addSymbol(function.getName(), info);
        }
        for (ConstantDeclarationNode constant : program.getConstantDeclarations()) {
            SemanticType constType = toSemanticType(constant.getType());
            SymbolInfo info = new SymbolInfo(constant.getName(), SymbolInfo.Kind.VARIABLE, constType);
            symbolTable.addSymbol(constant.getName(), info);
        }

    }


    private void secondPass(ProgramNode program, SymbolTable symbolTable) {
        for (VariableDeclarationNode var : program.getGlobalVariableDeclarations()) {
            checkVariable(var,symbolTable);
        }
        for (FunctionDeclarationNode function : program.getFunctionDeclarations()) {
            checkFunction(function, symbolTable);
        }
        for (ConstantDeclarationNode constant : program.getConstantDeclarations()) {
            checkConstant(constant,symbolTable);
        }
    }

    private void addBuiltFunctions(SymbolTable symbolTable) {
        symbolTable.addSymbol("read_INT", new SymbolInfo("read_INT", SymbolInfo.Kind.FUNCTION, new SemanticType("INT", false), List.of(), null));
        symbolTable.addSymbol("read_FLOAT", new SymbolInfo("read_FLOAT", SymbolInfo.Kind.FUNCTION, new SemanticType("FLOAT", false), List.of(), null));
        symbolTable.addSymbol("read_STRING", new SymbolInfo("read_STRING", SymbolInfo.Kind.FUNCTION, new SemanticType("STRING", false), List.of(), null));
        symbolTable.addSymbol("print",new SymbolInfo("print",SymbolInfo.Kind.FUNCTION, null, List.of(), null));
        symbolTable.addSymbol("println", new SymbolInfo("println",SymbolInfo.Kind.FUNCTION, null, List.of(), null));
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
                throw new RuntimeException("ScopeError: undefined identifier "+id.getName());
            }
           return info.getType();
        }
        // for binary expression

        if (expr instanceof BinaryExpressionNode bin) {
            SemanticType leftType = inferType(bin.getLeft(), symbolTable);
            SemanticType rightType = inferType(bin.getRight(), symbolTable);
            String operator = bin.getOperator();

            if (operator.equals("PLUS")) {
                if (leftType.getName().equals("STRING") && !leftType.isArray() && rightType.getName().equals("STRING") && !rightType.isArray()) {
                    return new SemanticType("STRING", false);
                }
                if (!leftType.isNumeric() || !rightType.isNumeric()) {
                    throw new RuntimeException("OperatorError: " + operator + " required numeric operands");
                }
                if (leftType.getName().equals("FLOAT") || rightType.getName().equals("FLOAT")) {
                    return new SemanticType("FLOAT", false);
                }
                return new SemanticType("INT", false);
            }

            if (operator.equals("MINUS") || operator.equals("MULTIPLY") || operator.equals("DIVIDE") || operator.equals("MODULO")) {
                if (!leftType.isNumeric() || !rightType.isNumeric()) {
                    throw new RuntimeException("OperatorError: " + operator + " required numeric operands");
                }
                if (leftType.getName().equals("FLOAT") || rightType.getName().equals("FLOAT")) {
                    return new SemanticType("FLOAT", false);
                }
                return new SemanticType("INT", false);
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
            SemanticType indexType=inferType(arrayAccess.getIndex(),symbolTable);
                if (!indexType.equals(new SemanticType("INT", false))) {
                    throw new RuntimeException("TypeError: array index must be INT");
                }
                if (!arrayType.isArray()) {
                    throw new RuntimeException("TypeError: trying to index a non-array expression");
                }
                return new SemanticType(arrayType.getName(), false);
        }

        if(expr instanceof ArrayCreationNode arraycreation){
            SemanticType sizeType=inferType(arraycreation.getSize(),symbolTable);
                if(!sizeType.equals(new SemanticType("INT",false))){
                    throw new RuntimeException("TypeError:array size must be of type Int");
                }
                TypeNode typenode=arraycreation.getElementType();
                return new SemanticType(typenode.getName(),true);
            }

        //for fields Access
        if (expr instanceof FieldAccessNode fieldAccess) {
            SemanticType targetType = inferType(fieldAccess.getTarget(), symbolTable);
            SymbolInfo info = symbolTable.getSymbol(targetType.getName());
            if (info == null || info.getKind() != SymbolInfo.Kind.COLLECTION) {
                throw new RuntimeException("TypeError: can't access field on non-collection " + targetType);
            }
            SemanticType fieldType = info.getFields().get(fieldAccess.getFieldName());
            if (fieldType == null) {
                throw new RuntimeException("ScopeError: unknown field " + fieldAccess.getFieldName());
            }
            return fieldType;
        }
        if (expr instanceof FunctionCallNode call) {
            ExpressionNode functionExpr = call.getFunction();
            if (!(functionExpr instanceof IdentifierNode id)) {
                throw new RuntimeException("SemanticError: invalid function call target");
            }
            return inferFunctionType(id.getName(), call.getArguments(), symbolTable);
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

        }else if(stmt instanceof ExpressionStatementNode exprStmt){
            checkExpressionStmt(exprStmt,symbolTable);
        }
        else if(stmt instanceof BlockNode block){
            checkBlock(block,symbolTable);

        } else if (stmt instanceof IfStatementNode ifstmt){
            checkIf(ifstmt,symbolTable);

        }else if (stmt instanceof  WhileStatementNode whileStmt){
            checkWhile(whileStmt,symbolTable);
        }
        else if (stmt instanceof  ForStatementNode forStmt){
            checkFor(forStmt,symbolTable);
        }
        else if (stmt instanceof ReturnStatementNode returnStmt) {
            checkReturn(returnStmt, symbolTable);
        }
        else{
            throw new RuntimeException("SemancticError: unknown statement "+stmt+getClass().getSimpleName());
        }

    }

    private void checkExpressionStmt(ExpressionStatementNode exprStmt, SymbolTable symbolTable) {
        inferType(exprStmt.getExpression(),symbolTable);
    }


    private void checkFor(ForStatementNode forStmt, SymbolTable symbolTable) {
       SymbolInfo info=symbolTable.getSymbol(forStmt.getVariableName());
       if(info==null){
           throw new RuntimeException("ScopeError : undefined loop varibale "+forStmt.getVariableName());
       }
       SemanticType intType=new SemanticType("INT",false);
       if(!info.getType().equals(intType)){
           throw  new RuntimeException("TypeError: for loop variable must be INT");
       }

        SemanticType startType = inferType(forStmt.getStartValue(), symbolTable);
        SemanticType endType = inferType(forStmt.getEndValue(), symbolTable);
        SemanticType stepType = inferType(forStmt.getStepValue(), symbolTable);

        if (!startType.equals(intType) || !endType.equals(intType) || !stepType.equals(intType)) {
            throw new RuntimeException("TypeError: for start, end and step must be INT");
        }

        checkBlock(forStmt.getBody(), symbolTable);

    }

    private void checkVariableDeclaration(VariableDeclarationNode var,SymbolTable symbolTable){
        SemanticType declaredType=toSemanticType(var.getType());
        if(var.getInitValue()!=null){
            SemanticType initValueType=inferType(var.getInitValue(),symbolTable);
            if(!declaredType.equals(initValueType)) {
                throw new RuntimeException("TypeError: can't assign" + initValueType + " to " + declaredType);
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
            if(returnStmt.getExpression()!=null){
                throw  new RuntimeException("ReturnError : void function cannot return a value " );
            }
        }
        if(returnStmt.getExpression()==null){
            throw  new RuntimeException("ReturnError: function must return a value");

        }
        SemanticType returnedType=inferType(returnStmt.getExpression(),symbolTable);

        if (!currentReturnType.equals(returnedType)) {
            throw new RuntimeException("ReturnError: expected " + currentReturnType + " but found " + returnedType);
        }
    }


    private void checkArguments(String name, List<SemanticType> expectedTypes, List<ExpressionNode> arguments, SymbolTable symbolTable) {
        if (expectedTypes.size() != arguments.size()) {
            throw new RuntimeException("ArgumentError: wrong number of arguments for " + name);
        }
        for (int i = 0; i < arguments.size(); i++) {
            SemanticType expectedType = expectedTypes.get(i);
            SemanticType actualType = inferType(arguments.get(i), symbolTable);
            if (!expectedType.equals(actualType)) {
                throw new RuntimeException("ArgumentError: argument " + (i+1) + " of " +name+ " should be " +expectedType+ " but found " +actualType);
            }
        }
    }

    private SemanticType inferFunctionType(String name, List<ExpressionNode> arguments, SymbolTable symbolTable) {
        SymbolInfo info = symbolTable.getSymbol(name);
        if (info == null) {
            throw new RuntimeException("ScopeError: undefined function or collection " + name);
        }
        if (info.getKind() == SymbolInfo.Kind.FUNCTION) {
            if (name.equals("print") || name.equals("println")) {
                if (arguments.size() > 1) {
                    throw new RuntimeException("ArgumentError: wrong number of arguments for " + name);
                }
                if (arguments.size() == 1) {
                    inferType(arguments.get(0), symbolTable);
                }
                return null;
            }
            checkArguments(name, info.getParameterTypes(), arguments, symbolTable);
            return info.getType();
        }
        if (info.getKind() == SymbolInfo.Kind.COLLECTION) {
            List<SemanticType> expectedTypes = new ArrayList<>(info.getFields().values());
            checkArguments(name, expectedTypes, arguments, symbolTable);
            return info.getType();
        }
        throw new RuntimeException("ArgumentError: " + name + " is not a functiion");
    }

    private void checkFunction(FunctionDeclarationNode function, SymbolTable symbolTable) {
        SymbolTable localTable = new SymbolTable(symbolTable);
        currentReturnType = null;
        if (function.getReturnType() != null) {
            currentReturnType = toSemanticType(function.getReturnType());
        }
        for (ParameterNode parameter : function.getParameters()) {
            SemanticType parameterType = toSemanticType(parameter.getType());
            SymbolInfo info = new SymbolInfo(parameter.getName(), SymbolInfo.Kind.VARIABLE, parameterType);
            localTable.addSymbol(parameter.getName(), info);
        }
        checkBlock(function.getBody(), localTable);
        currentReturnType = null;
    }

    private Map<String, SemanticType> checkCollection(CollectionDeclarationNode collection, SymbolTable symbolTable) {
        String collectionName = collection.getName();
        if (collectionName.equals("INT") || collectionName.equals("FLOAT") || collectionName.equals("STRING") || collectionName.equals("BOOL")) {
            throw new RuntimeException("CollectionError: invalid collection name " + collectionName);
        }
        if (symbolTable.exists(collectionName)) {
            throw new RuntimeException("CollectionError: collection " + collectionName + " already defined");
        }
        Map<String, SemanticType> fields = new LinkedHashMap<>();
        for (FieldDeclarationNode field : collection.getFields()) {
            String fieldName = field.getName();
            if (fields.containsKey(fieldName)) {
                throw new RuntimeException("CollectionError: duplicate field " + fieldName + " in collection " + collectionName);
            }
            fields.put(fieldName, toSemanticType(field.getType()));
        }
        return fields;
    }

    private void checkVariable(VariableDeclarationNode var,SymbolTable symbolTable){
        if (var.getInitValue() != null) {
            SemanticType varType = toSemanticType(var.getType());
            SemanticType valueType = inferType(var.getInitValue(), symbolTable);
            if (!varType.equals(valueType)) {
                throw new RuntimeException("TypeError : can't assign " + valueType + " to " + varType);
            }
        }

    }
    private void checkConstant(ConstantDeclarationNode constant,SymbolTable symbolTable){
        SemanticType declaredType = toSemanticType(constant.getType());
        SemanticType valueType = inferType(constant.getValue(), symbolTable);
        if (!declaredType.equals(valueType)) {
            throw new RuntimeException("TypeError: can't assign " + valueType + " to " + declaredType);
        }

    }


}
