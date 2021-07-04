package com.example.zghplugin.jg;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;


import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

/**
 * @author gavin
 * @date 2019/2/19
 */
public class JGMethodVisitor extends MethodVisitor {
    JGClassVisitor classWriter;
    String name;
    String signature;
    ClassNode classNode;
    String replaceMethodName;
    MethodVisitor replaceMethodVisitor =new EmptyMethodVisitor(0) ;

   static class  EmptyMethodVisitor extends MethodVisitor{

        public EmptyMethodVisitor(int api) {
            super(Opcodes.ASM5);
        }

        public EmptyMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }
    }

    public JGMethodVisitor(JGClassVisitor cv, MethodVisitor mv, String name, ClassNode classNode, String signature) {
        super(Opcodes.ASM5, mv);
        classWriter = cv;
        this.name = name;
        this.classNode = classNode;
        this.signature=signature;
    }


    JGAnnotationVisitor jgAnnotationVisitor;

    String JG_DESC = "Lcom/example/myapplication/Confirm;";

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        System.out.println("visitAnnotation desc=" + desc);
        if (JG_DESC.equals(desc)) {
            jgAnnotationVisitor = new JGAnnotationVisitor();
            //创建修改原有方法的名字
             replaceMethodName = "_confirmed_" + name;
            replaceMethodVisitor = classWriter.visitMethod(ACC_PUBLIC, replaceMethodName, "()V", null, null);
            return jgAnnotationVisitor;
        }

        return super.visitAnnotation(desc, visible);
    }



    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode,owner,name,desc,itf);
        replaceMethodVisitor.visitMethodInsn(opcode,owner,name,desc,itf);

    }


    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        replaceMethodVisitor.visitLabel(label);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode,label);
        replaceMethodVisitor.visitJumpInsn(opcode,label);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack,maxLocals);
        replaceMethodVisitor.visitMaxs(maxStack,maxLocals);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode,var);
        replaceMethodVisitor.visitVarInsn(opcode,var);
    }

    @Override
    public void visitCode() {
        if (jgAnnotationVisitor != null) {
            //使用反射调用方法
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            MethodVisitor methodVisitor = mv;
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/NoSuchMethodException");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            methodVisitor.visitLdcInsn(replaceMethodName);
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            methodVisitor.visitVarInsn(ASTORE, 1);

//            methodVisitor.visitLdcInsn(string2Unicode(jgAnnotationVisitor.warringInfo));
            methodVisitor.visitLdcInsn(jgAnnotationVisitor.warringInfo);
//        methodVisitor.visitLdcInsn("\u786e\u5b9a\u6267\u884c\u7b7e\u53d1\u90ae\u4ef6");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/example/myapplication/ConfirmUtil", "doConfirm", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/reflect/Method;)V", false);
            methodVisitor.visitLabel(label1);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitVarInsn(ASTORE, 1);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/NoSuchMethodException", "printStackTrace", "()V", false);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(3, 2);
            methodVisitor.visitEnd();

        } else {
            super.visitCode();
        }
        replaceMethodVisitor.visitCode();
    }

    /**
     * 字符串转换unicode
     * @param string
     * @return
     */
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        replaceMethodVisitor.visitInsn(opcode);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);
        replaceMethodVisitor.visitLocalVariable(name,desc,signature,start,end,index);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        super.visitLdcInsn(cst);
        replaceMethodVisitor.visitLdcInsn(cst);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
        replaceMethodVisitor.visitTypeInsn(hashCode(),type);
    }



    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        super.visitMethodInsn(opcode,owner,name,desc);
        replaceMethodVisitor.visitMethodInsn(opcode,owner,name,desc);
    }


    @Override
    public void visitEnd() {
        super.visitEnd();
        replaceMethodVisitor.visitEnd();
    }

    private static class JGAnnotationVisitor extends AnnotationVisitor {

        String warringInfo;

        public JGAnnotationVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            this.warringInfo = value.toString();
        }
    }


}
