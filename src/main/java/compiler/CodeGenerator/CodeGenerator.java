package compiler.CodeGenerator;

import compiler.Parser.AST.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CodeGenerator implements Opcodes {


    public void generate(ProgramNode program, String outputfile) throws IOException{
        String className="Main";
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8,ACC_PUBLIC,className,null,"java/lang/Object",null);
        generateDefaultConstructor(cw);
        generateMainMethod(program, cw);
        cw.visitEnd();
        byte[] bytecode = cw.toByteArray();
        try (FileOutputStream fos = new FileOutputStream(outputfile)) {
            fos.write(bytecode);
        }
    }

    private void generateDefaultConstructor(ClassWriter cw) {
        MethodVisitor mv=cw.visitMethod(ACC_PUBLIC,"<init>","()V",null,null);
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
        FunctionDeclarationNode mainFunction=getMainFunction(program);
        BlockNode body=mainFunction.getBody();
        List<StatementNode> statements=body.getStatements();
        for(StatementNode stmt:statements){
            generateStatement(stmt,mv);
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
        if(stmt instanceof ExpressionStatementNode exprStmt){
            generateExpression(exprStmt.getExpression(), mv);
            return;
        }
        throw new RuntimeException("Unsupported statement"+stmt);
    }

    private void generateExpression(ExpressionNode expr, MethodVisitor mv) {

        if(expr instanceof FunctionCallNode call){
            generateFunctionCall(call,mv);
            return;
        }
        if (expr instanceof StringLiteralNode) {
            StringLiteralNode str = (StringLiteralNode) expr;
            String value = str.getValue();
            mv.visitLdcInsn(value);
            return;
        }
        throw new RuntimeException("Unsupported expression: " + expr);

    }

    private void generateFunctionCall(FunctionCallNode call,MethodVisitor mv){
        ExpressionNode function = call.getFunction();
        List<ExpressionNode> arguments = call.getArguments();
        if(function instanceof IdentifierNode id) {
            String functionName = id.getName();
            if(isBuiltIn(functionName)){
                generateBuiltin(functionName,arguments,mv);
                return;
            }
            throw new RuntimeException("function"+ functionName+"not implemented yet");
        }
        throw new RuntimeException("Unsupported function call");
    }

    private void generateBuiltin(String functionName, List<ExpressionNode> arguments, MethodVisitor mv) {
        if (functionName.equals("println")) {
            if (arguments.size() != 1) {
                throw new RuntimeException("println expects one argument");
            }
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            ExpressionNode argument = arguments.get(0);
            generateExpression(argument, mv);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            return;
        }
        throw new RuntimeException("Unknown builtin: " + functionName);
    }

    private boolean isBuiltIn(String functionName) {
        return functionName.equals("println");
    }


}
