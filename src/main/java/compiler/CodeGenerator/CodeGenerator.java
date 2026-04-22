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
        variableCounter = 1;
        localVariables.clear();
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
    }

    private void generateVariableDeclaration(VariableDeclarationNode varDecl, MethodVisitor mv) {
    }

    private void generateIfStatement(IfStatementNode ifStmt, MethodVisitor mv) {
    }

    private void generateBlock(BlockNode block, MethodVisitor mv) {
    }

    private void generateWhileStatement(WhileStatementNode whileStmt, MethodVisitor mv) {
    }

    private void generateForStatement(ForStatementNode forStmt, MethodVisitor mv) {
    }

    private void generateReturnStatement(ReturnStatementNode returnStmt, MethodVisitor mv) {
    }

    private String getExpressionTypeName(ExpressionNode expr) {
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
        }
        throw new RuntimeException("Unsupported function call");
    }

    private boolean isBuiltIn(String functionName) {
        return true;
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
}
   
