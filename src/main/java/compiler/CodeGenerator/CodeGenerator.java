package compiler.CodeGenerator;

import compiler.Parser.AST.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Label;

public class CodeGenerator implements Opcodes {

    private ProgramNode currentProgram;
    private int variableCounter;
    private Map<String, LocalVariableInfo> localVariables = new HashMap<>();

    public void generate(ProgramNode program, String outputfile) throws IOException {
        String className = "Main";
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC, className, null, "java/lang/Object", null);
        generateDefaultConstructor(cw);
        generateMainMethod(program, cw);
        generateFunctions(program, cw);
        generateCollections(program);
        cw.visitEnd();
        byte[] bytecode = cw.toByteArray();
        try (FileOutputStream fos = new FileOutputStream(outputfile)) {
            fos.write(bytecode);
        }
    }

    private void generateCollections(ProgramNode program) {
    }

    private void generateFunctions(ProgramNode program, ClassWriter cw) {
        for (FunctionDeclarationNode function : program.getFunctionDeclarations()) {
            if (!function.getName().equals("main")) {
                generateFunction(function, cw);
            }
        }
    }

    private void generateFunction(FunctionDeclarationNode function, ClassWriter cw) {
        BlockNode body = function.getBody();
        String descriptor = getMethodDescriptor(function);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, function.getName(), descriptor, null, null);
        mv.visitCode();
        variableCounter = 0;
        localVariables.clear();
        for (ParameterNode param : function.getParameters()) {
            String name = param.getName();
            TypeNode type = param.getType();
            int slot = variableCounter;
            variableCounter += getTypeSize(type);
            localVariables.put(name, new LocalVariableInfo(slot, type));
        }
        generateBlock(body, mv);
        if (function.getReturnType() == null) {
            mv.visitInsn(RETURN);
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateDefaultConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateMainMethod(ProgramNode program, ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        FunctionDeclarationNode mainFunction = getMainFunction(program);
        BlockNode body = mainFunction.getBody();
        List<StatementNode> statements = body.getStatements();
        for (StatementNode stmt : statements) {
            generateStatement(stmt, mv);
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

    }

    private FunctionDeclarationNode getMainFunction(ProgramNode program) {
        for (FunctionDeclarationNode function : program.getFunctionDeclarations()) {
            if (function.getName().equals("main")) {
                return function;
            }
        }
        throw new RuntimeException("No main function found.");
    }

    private void generateStatement(StatementNode stmt, MethodVisitor mv) {
        if (stmt instanceof ExpressionStatementNode exprStmt) {
            ExpressionNode expr = exprStmt.getExpression();
            generateExpression(expr, mv);
            String type = getExpressionTypeName(expr);
            if (!type.equals("VOID")) {
                mv.visitInsn(POP);
            }
            return;
        }
        if (stmt instanceof VariableDeclarationNode varDecl) {
            generateVariableDeclaration(varDecl, mv);
            return;
        }
        if (stmt instanceof AssignmentStatementNode assignStmt) {
            generateAssignment(assignStmt, mv);
            return;
        }
        if (stmt instanceof IfStatementNode ifStmt) {
            generateIfStatement(ifStmt, mv);
            return;
        }
        if (stmt instanceof BlockNode block) {
            generateBlock(block, mv);
            return;
        }
        if (stmt instanceof WhileStatementNode whileStmt) {
            generateWhileStatement(whileStmt, mv);
            return;
        }
        if (stmt instanceof ForStatementNode forStmt) {
            generateForStatement(forStmt, mv);
            return;
        }
        if (stmt instanceof ReturnStatementNode returnStmt) {
            generateReturnStatement(returnStmt, mv);
            return;
        }
        throw new RuntimeException("Unsupported statement: " + stmt);
    }

    private void generateAssignment(AssignmentStatementNode assignStmt, MethodVisitor mv) {
        ExpressionNode target = assignStmt.getTarget();
        ExpressionNode value = assignStmt.getValue();
        if (target instanceof IdentifierNode id) {
            String name = id.getName();
            LocalVariableInfo info = localVariables.get(name);
            int slot = info.getSlot();
            TypeNode type = info.getType();
            generateExpression(value, mv);
            switch (type.getName()) {
                case "INT":
                case "BOOL":
                    mv.visitVarInsn(ISTORE, slot);break;
                case "FLOAT":
                    mv.visitVarInsn(FSTORE, slot);break;
                default:
                    mv.visitVarInsn(ASTORE, slot);break;
            }
            return;
        }
        if (target instanceof ArrayAccessNode arrayAccess) {
            ExpressionNode array = arrayAccess.getArray();
            ExpressionNode index = arrayAccess.getIndex();

            generateExpression(array, mv);
            generateExpression(index, mv);
            generateExpression(value, mv);

            String elementType = getArrayElementTypeName(array);

            switch (elementType) {
                case "INT":
                case "BOOL": mv.visitInsn(IASTORE);break;
                case "FLOAT": mv.visitInsn(FASTORE);break;
                default: mv.visitInsn(AASTORE);break;
            }
            return;
        }
        if (target instanceof FieldAccessNode fieldAccess) {
            ExpressionNode object = fieldAccess.getTarget();
            String fieldName = fieldAccess.getFieldName();

            generateExpression(object, mv);
            generateExpression(value, mv);
            String className = getExpressionTypeName(object);
            TypeNode fieldType = findFieldType(className, fieldName);

            mv.visitFieldInsn(PUTFIELD, className, fieldName, getTypeDescriptor(fieldType)
            );
            return;
        }
    }

    private void generateVariableDeclaration(VariableDeclarationNode varDecl, MethodVisitor mv) {
        String name = varDecl.getName();
        ExpressionNode initValue = varDecl.getInitValue();
        TypeNode type = varDecl.getType();
        if (initValue != null) {
            generateExpression(initValue, mv);
        } else {
            String typeName = type.getName();
            switch (typeName) {
                case "INT": mv.visitLdcInsn(0);break;
                case "FLOAT": mv.visitLdcInsn(0.0f);break;
                case "BOOL": mv.visitInsn(ICONST_0);break;
                default: mv.visitInsn(ACONST_NULL);break;
            }
        }
        int slot = variableCounter;
        variableCounter += getTypeSize(type);
        localVariables.put(name, new LocalVariableInfo(slot, type));
        String typeName = type.getName();
        switch (typeName) {
            case "INT":
            case "BOOL":
                mv.visitVarInsn(ISTORE, slot);
                break;
            case "FLOAT": mv.visitVarInsn(FSTORE, slot);break;
            default: mv.visitVarInsn(ASTORE, slot);break;
        }
    }
    private int getTypeSize(TypeNode type) {
        String name = type.getName();
        switch (name) {
            case "LONG":
            case "DOUBLE":
                return 2;
            default:
                return 1;
        }
    }

    private void generateIfStatement(IfStatementNode ifStmt, MethodVisitor mv) {
        ExpressionNode condition = ifStmt.getCondition();
        StatementNode thenBlock= ifStmt.getThenBlock();
        StatementNode elseBlock = ifStmt.getElseBlock();

        Label elseLabel = new Label();
        Label endLabel = new Label();

        generateExpression(condition, mv);
        mv.visitJumpInsn(IFEQ, elseLabel);

        generateStatement(thenBlock, mv);
        mv.visitJumpInsn(GOTO, endLabel);

        mv.visitLabel(elseLabel);

        if (elseBlock != null) {
            generateStatement(elseBlock, mv);
        }

        mv.visitLabel(endLabel);
    }

    private void generateBlock(BlockNode block, MethodVisitor mv) {
        List<StatementNode> statements = block.getStatements();
        for (StatementNode stmt : statements) {
            generateStatement(stmt, mv);
        }
    }

    private void generateWhileStatement(WhileStatementNode whileStmt, MethodVisitor mv) {
        ExpressionNode condition = whileStmt.getCondition();
        StatementNode body = whileStmt.getBody();
        Label startLabel = new Label();
        Label endLabel = new Label();
        mv.visitLabel(startLabel);
        generateExpression(condition, mv);
        mv.visitJumpInsn(IFEQ, endLabel);
        generateStatement(body, mv);
        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    private void generateForStatement(ForStatementNode forStmt, MethodVisitor mv) {
        String name = forStmt.getVariableName();
        ExpressionNode start = forStmt.getStartValue();
        ExpressionNode end = forStmt.getEndValue();
        ExpressionNode step = forStmt.getStepValue();
        BlockNode body = forStmt.getBody();

        generateExpression(start, mv);

        LocalVariableInfo info = localVariables.get(name);
        int slot = info.getSlot();
        mv.visitVarInsn(ISTORE, slot);
        Label startLabel = new Label();
        Label endLabel = new Label();
        mv.visitLabel(startLabel);
        mv.visitVarInsn(ILOAD, slot);
        generateExpression(end, mv);
        generateComparison(IF_ICMPLT, mv);
        mv.visitJumpInsn(IFEQ, endLabel);
        generateBlock(body, mv);
        mv.visitVarInsn(ILOAD, slot);
        generateExpression(step, mv);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, slot);
        mv.visitJumpInsn(GOTO, startLabel);
        mv.visitLabel(endLabel);
    }

    private void generateComparison(int operator, MethodVisitor mv) {
        Label trueLabel = new Label();
        Label falseLabel = new Label();
        mv.visitJumpInsn(operator, trueLabel);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, falseLabel);
        mv.visitLabel(trueLabel);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(falseLabel);
    }

    private void generateReturnStatement(ReturnStatementNode returnStmt, MethodVisitor mv) {
    }

    private String getExpressionTypeName(ExpressionNode expr) {
        return null;
    }


    private void generateExpression(ExpressionNode expr, MethodVisitor mv) {
        if (expr instanceof FunctionCallNode call) {
            generateFunctionCall(call, mv);
            return;
        }
        if (expr instanceof BoolLiteralNode boolNode) {
            if (boolNode.getValue()) {
                mv.visitInsn(ICONST_1);
            } else {
                mv.visitInsn(ICONST_0);
            }
            return;
        }
        Object value = getLiteralValue(expr);
        if (value != null) {
            mv.visitLdcInsn(value);
            return;
        }
        if (expr instanceof IdentifierNode iden) {
            generateIdentifier(iden, mv);
            return;
        }
        if (expr instanceof BinaryExpressionNode bin) {
            generateBinaryExpression(bin, mv);
            return;
        }
        if (expr instanceof ArrayCreationNode arrayCreation) {
            generateArrayCreation(arrayCreation, mv);
            return;
        }
        if (expr instanceof ArrayAccessNode arrayAccess) {
            generateArrayAccess(arrayAccess, mv);
            return;
        }
        if (expr instanceof FieldAccessNode fieldAccess) {
            generateFieldAccess(fieldAccess, mv);
            return;
        }
        if (expr instanceof UnaryExpressionNode unary) {
            generateUnaryExpression(unary, mv);
            return;
        }
        throw new RuntimeException("Unsupported expression: " + expr);

    }

    private void generateUnaryExpression(UnaryExpressionNode unary, MethodVisitor mv) {
    }

    private void generateFieldAccess(FieldAccessNode fieldAccess, MethodVisitor mv) {
    }

    private void generateArrayAccess(ArrayAccessNode arrayAccess, MethodVisitor mv) {
    }

    private void generateArrayCreation(ArrayCreationNode arrayCreation, MethodVisitor mv) {
    }

    private void generateBinaryExpression(BinaryExpressionNode bin, MethodVisitor mv) {
    }

    private void generateIdentifier(IdentifierNode iden, MethodVisitor mv) {
    }


    private Object getLiteralValue(ExpressionNode expr) {
        if (expr instanceof IntLiteralNode n) return n.getValue();
        if (expr instanceof FloatLiteralNode n) return n.getValue();
        if (expr instanceof StringLiteralNode n) return n.getValue();
        return null;
    }

    private void generateFunctionCall(FunctionCallNode call, MethodVisitor mv) {
        ExpressionNode function = call.getFunction();
        List<ExpressionNode> arguments = call.getArguments();
        if (function instanceof IdentifierNode id) {
            String functionName = id.getName();
            if (isBuiltIn(functionName)) {
                generateBuiltin(functionName, arguments, mv);
                return;
            }
            if (isCollectionName(functionName)) {
                mv.visitTypeInsn(NEW, functionName);
                mv.visitInsn(DUP);
                for (ExpressionNode arg : arguments) {
                    generateExpression(arg, mv);
                }
                String descriptor = getCollectionConstructorDescriptor(functionName);
                mv.visitMethodInsn(INVOKESPECIAL, functionName, "<init>", descriptor, false);
                return;
            }
            FunctionDeclarationNode functionDecl = findFunctionByName(functionName);
            for (ExpressionNode arg : arguments) {
                generateExpression(arg, mv);
            }
            String descriptor = getMethodDescriptor(functionDecl);
            mv.visitMethodInsn(INVOKESTATIC, "Main", functionName, descriptor, false);
            return;
        }
        throw new RuntimeException("Unsupported function call");

    }

    private FunctionDeclarationNode findFunctionByName(String functionName) {
        for (FunctionDeclarationNode f : currentProgram.getFunctionDeclarations()) {
            if (f.getName().equals(functionName)) {
                return f;
            }
        }
        throw new RuntimeException("Function not found: " + functionName);
    }

    private String getCollectionConstructorDescriptor(String collectionName) {
        for (CollectionDeclarationNode collection : currentProgram.getCollectionDeclarations()) {
            if (collection.getName().equals(collectionName)) {
                return buildConstructorDescriptor(collection);
            }
        }
        throw new RuntimeException("Collection not found: " + collectionName);
    }

    private String buildConstructorDescriptor(CollectionDeclarationNode collection) {
        StringBuilder sb = new StringBuilder("(");

        for (FieldDeclarationNode field : collection.getFields()) {
            sb.append(getTypeDescriptor(field.getType()));
        }
        sb.append(")V");
        return sb.toString();
    }

    private boolean isCollectionName(String collectionName) {
        for (CollectionDeclarationNode collection : currentProgram.getCollectionDeclarations()) {
            if (collection.getName().equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBuiltIn(String functionName) {
        return functionName.equals("read_INT") || functionName.equals("read_FLOAT") || functionName.equals("read_STRING") || functionName.equals("print_INT") || functionName.equals("print_FLOAT") || functionName.equals("print") || functionName.equals("println");
    }


    private void generateBuiltin(String functionName, List<ExpressionNode> arguments, MethodVisitor mv) {
        if (functionName.equals("println")) {
            if (arguments.size() != 1) {
                throw new RuntimeException("println expects one argument");
            }
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            ExpressionNode argument = arguments.get(0);
            generateExpression(argument, mv);
            String typeName = getExpressionTypeName(argument);
            String descriptor = "(" + getTypeDescriptor(typeName) + ")V";
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false);
            return;
        }
        throw new RuntimeException("Unknown builtin: " + functionName);
    }

    private String getTypeDescriptor(String typeName) {
        switch (typeName) {
            case "INT":
                return "I";
            case "FLOAT":
                return "F";
            case "BOOL":
                return "Z";
            case "STRING":
                return "Ljava/lang/String;";
            default:
                return "Ljava/lang/Object;";
        }
    }
    private String getTypeDescriptor(TypeNode type) {
        String baseDescriptor = getTypeDescriptor(type.getName());
        if (type.isArray()) {
            return "[" + baseDescriptor;
        }
        return baseDescriptor;
    }
    private TypeNode findFieldType(String collectionName, String fieldName) {
        for (CollectionDeclarationNode collection : currentProgram.getCollectionDeclarations()) {
            if (collection.getName().equals(collectionName)) {
                for (FieldDeclarationNode field : collection.getFields()) {
                    if (field.getName().equals(fieldName)) {
                        return field.getType();
                    }
                }
            }
        }
        throw new RuntimeException("Field not found: " + collectionName + "." + fieldName);
    }

    private String getMethodDescriptor(FunctionDeclarationNode function) {
        StringBuilder sb = new StringBuilder("(");
        for (ParameterNode param : function.getParameters()) {
            sb.append(getTypeDescriptor(param.getType()));
        }
        sb.append(")");
        TypeNode returnType = function.getReturnType();
        if (returnType == null) {
            sb.append("V");
        } else {
            sb.append(getTypeDescriptor(returnType));
        }
        return sb.toString();
    }
    private String getArrayElementTypeName(ExpressionNode arrayExpr) {
        if (arrayExpr instanceof IdentifierNode id) {
            LocalVariableInfo info = localVariables.get(id.getName());
            TypeNode type = info.getType();
            return type.getName();
        }
        throw new RuntimeException("Unsupported array expression");
    }

    private static class LocalVariableInfo {
        private final int slot;
        private final TypeNode type;
        public LocalVariableInfo(int slot, TypeNode type) {
            this.slot = slot;
            this.type = type;
        }
        public int getSlot() {
            return slot;
        }
        public TypeNode getType() {
            return type;
        }
    }
}
   
