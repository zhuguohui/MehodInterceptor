package com.example.zghplugin.jg;

import com.example.zghplugin.extension.MethodInterceptorConfig;

import org.json.simple.JSONObject;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


import static org.objectweb.asm.Opcodes.*;


/**
 * @author gavin
 * @date 2019/2/19
 */
public class MIMethodVisitor2 extends MethodVisitor {
    MIClassVisitor classWriter;
    String name;
    String signature;
    String replaceMethodName;
    MethodVisitor replaceMethodVisitor = new EmptyMethodVisitor(0);
    int methodArgNumber = 0;
    String[] exceptions;
    String desc;
    private static int methodIndex;
    MethodInterceptorConfig miConfig;

    static class EmptyMethodVisitor extends MethodVisitor {

        public EmptyMethodVisitor(int api) {
            super(Opcodes.ASM5);
        }

        public EmptyMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }
    }

    public MIMethodVisitor2(MIClassVisitor cv, MethodVisitor mv, String name, String signature, String[] exceptions, String desc, MethodInterceptorConfig miConfig) {
        super(Opcodes.ASM5, mv);
        classWriter = cv;
        this.name = name;
        this.miConfig = miConfig;
        this.signature = signature;
        this.exceptions = exceptions;
        this.desc = desc;
        methodIndex++;//使用这个参数可以区分相同名字的方法

    }


    JGAnnotationVisitor jgAnnotationVisitor;

    public boolean haveDealMethod() {
        return jgAnnotationVisitor != null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        System.out.println("visitAnnotation desc=" + desc);
        if (jgAnnotationVisitor==null&&miConfig.handlers.keySet().contains(desc)) {
            //如果jgAnnotationVisitor 不等于空，表示已经有一个methodInter需要处理了。
            if (this.desc != null) {
                String[] split = this.desc.split(";");
                methodArgNumber = split.length - 1;
            }
            jgAnnotationVisitor = new JGAnnotationVisitor(desc);
            //创建修改原有方法的名字
            replaceMethodName = "_" + jgAnnotationVisitor.simpleName + "_index" + methodIndex + "_" + name;
            replaceMethodVisitor = classWriter.visitMethod(ACC_PRIVATE, replaceMethodName, this.desc, signature, exceptions);
            replaceMethodVisitor=new MIMethodVisitor2(classWriter,replaceMethodVisitor,replaceMethodName,this.signature,this.exceptions,this.desc,miConfig);
            return jgAnnotationVisitor;
        }
        AnnotationVisitor annotationVisitor = super.visitAnnotation(desc, visible);
        if (annotationVisitor != null) {
            if (jgAnnotationVisitor != null) {
                AnnotationVisitor copy = replaceMethodVisitor.visitAnnotation(desc, visible);
                return new CopyAnnotationVisitor(annotationVisitor, copy);
            } else {
                return annotationVisitor;
            }
        } else {
            return null;
        }
    }

    private static class CopyAnnotationVisitor extends AnnotationVisitor {

        AnnotationVisitor copyAV;

        public CopyAnnotationVisitor(AnnotationVisitor av, AnnotationVisitor copyAV) {
            super(Opcodes.ASM5, av);
            this.copyAV = copyAV;
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            copyAV.visit(name, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            AnnotationVisitor annotationVisitor = super.visitArray(name);
            if (annotationVisitor != null) {
                AnnotationVisitor copyArray = copyAV.visitArray(name);
                return new CopyAnnotationVisitor(annotationVisitor, copyArray);
            } else {
                return null;
            }
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            super.visitEnum(name, desc, value);
            copyAV.visitEnum(name, desc, value);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            AnnotationVisitor annotationVisitor = super.visitAnnotation(name, desc);
            if (annotationVisitor != null) {
                AnnotationVisitor copy = copyAV.visitAnnotation(name, desc);
                return new CopyAnnotationVisitor(annotationVisitor, copy);
            } else {
                return null;
            }
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            copyAV.visitEnd();
        }
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        replaceMethodVisitor.visitMethodInsn(opcode, owner, name, desc, itf);

    }


    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        replaceMethodVisitor.visitLabel(label);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        replaceMethodVisitor.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
        replaceMethodVisitor.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);
        replaceMethodVisitor.visitVarInsn(opcode, var);
    }

    /**
     * 生成的方法如下
     * <pre>
     *      protected void realCall(View arg0,Integer arg1,Integer arg2,Boolean arg3,Float arg4,Double arg5,View arg6,View arg7,View arg8,View arg9,View arg10) {
     *         try {
     *             Method method=null;
     *             String methodName="delete";
     *             String annotationValue="确定执行刪除操作";
     *             Method[] declaredMethods = getClass().getDeclaredMethods();
     *             for(int i=0;i<declaredMethods.length;i++){
     *                 if(declaredMethods[i].getName().equals(methodName)){
     *                     method=declaredMethods[i];
     *                     break;
     *                 }
     *             }
     *             if(method==null){
     *                 throw new RuntimeException("don't fond method  ["+methodName+"] in class ["+this.getClass().getName()+"]");
     *             }
     *             ConfirmUtil.doConfirm(annotationValue,this,method,arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10);
     *         } catch (Exception e) {
     *             try {
     *                 ConfirmUtil.callError(this,e);
     *             }catch (Exception e2) {
     *                 e2.printStackTrace();
     *             }
     *
     *         }
     *     }
     * </pre>
     */
    @Override
    public void visitCode() {
        if (jgAnnotationVisitor != null) {
            //使用反射调用方法
            MethodVisitor methodVisitor = mv;
            int i = methodArgNumber;
//            methodVisitor = classWriter.visitMethod(ACC_PROTECTED, "realCall", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            Label label3 = new Label();
            Label label4 = new Label();
            Label label5 = new Label();
            methodVisitor.visitTryCatchBlock(label3, label4, label5, "java/lang/Exception");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitVarInsn(ASTORE, 1 + i);
            methodVisitor.visitLdcInsn(replaceMethodName);
            methodVisitor.visitVarInsn(ASTORE, 2 + i);
            methodVisitor.visitLdcInsn(jgAnnotationVisitor.getInfo());
            methodVisitor.visitVarInsn(ASTORE, 3 + i);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethods", "()[Ljava/lang/reflect/Method;", false);
            methodVisitor.visitVarInsn(ASTORE, 4 + i);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ISTORE, 5 + i);
            Label label6 = new Label();
            methodVisitor.visitLabel(label6);
            methodVisitor.visitVarInsn(ILOAD, 5 + i);
            methodVisitor.visitVarInsn(ALOAD, 4 + i);
            methodVisitor.visitInsn(ARRAYLENGTH);
            Label label7 = new Label();
            methodVisitor.visitJumpInsn(IF_ICMPGE, label7);
            methodVisitor.visitVarInsn(ALOAD, 4 + i);
            methodVisitor.visitVarInsn(ILOAD, 5 + i);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
            methodVisitor.visitVarInsn(ALOAD, 2 + i);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label label8 = new Label();
            methodVisitor.visitJumpInsn(IFEQ, label8);
            methodVisitor.visitVarInsn(ALOAD, 4 + i);
            methodVisitor.visitVarInsn(ILOAD, 5 + i);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitVarInsn(ASTORE, 1 + i);
            methodVisitor.visitJumpInsn(GOTO, label7);
            methodVisitor.visitLabel(label8);
            methodVisitor.visitIincInsn(5 + i, 1);
            methodVisitor.visitJumpInsn(GOTO, label6);
            methodVisitor.visitLabel(label7);
            methodVisitor.visitVarInsn(ALOAD, 1 + i);
            Label label9 = new Label();
            methodVisitor.visitJumpInsn(IFNONNULL, label9);
            methodVisitor.visitTypeInsn(NEW, "java/lang/RuntimeException");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            methodVisitor.visitLdcInsn("don't fond method  [");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ALOAD, 2 + i);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitLdcInsn("] in class [");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitLdcInsn("]");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
            methodVisitor.visitInsn(ATHROW);
            methodVisitor.visitLabel(label9);
            methodVisitor.visitVarInsn(ALOAD, 3 + i);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1 + i);
//            methodVisitor.visitInsn(ICONST_0);//array的数量
            insertNumber(methodVisitor, i);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            //插入arg
            insertArg(methodVisitor, i);
            methodVisitor.visitMethodInsn(INVOKESTATIC, jgAnnotationVisitor.processName, "onMethodIntercepted", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)V", false);
            methodVisitor.visitLabel(label1);
            Label label10 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label10);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitVarInsn(ASTORE, 1 + i);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1 + i);
            methodVisitor.visitMethodInsn(INVOKESTATIC, jgAnnotationVisitor.processName, "onMethodInterceptedError", "(Ljava/lang/Object;Ljava/lang/Exception;)V", false);
            methodVisitor.visitLabel(label4);
            methodVisitor.visitJumpInsn(GOTO, label10);
            methodVisitor.visitLabel(label5);
            methodVisitor.visitVarInsn(ASTORE, 2 + i);
            methodVisitor.visitVarInsn(ALOAD, 2 + i);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            methodVisitor.visitLabel(label10);
            methodVisitor.visitInsn(RETURN);
            if (i == 0) {
                methodVisitor.visitMaxs(4, 6);//操作数值，规则不一样
            } else {
                methodVisitor.visitMaxs(7, 6 + i);
            }
            methodVisitor.visitEnd();

        } else {
            super.visitCode();
        }
        replaceMethodVisitor.visitCode();
    }


    private void insertNumber(MethodVisitor methodVisitor, int number) {
        int[] code = new int[]{ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5};
        if (number <= 5) {
            //小于5使用这种命令
            methodVisitor.visitInsn(code[number]);
        } else {
            //大于5使用如下命令
            methodVisitor.visitIntInsn(BIPUSH, number);
        }

    }

    private void insertArg(MethodVisitor methodVisitor, int n) {
        for (int i = 0; i < n; i++) {
            methodVisitor.visitInsn(DUP);
            insertNumber(methodVisitor, i);
            methodVisitor.visitVarInsn(ALOAD, i + 1);
            methodVisitor.visitInsn(AASTORE);
        }
    }

    /**
     * 字符串转换unicode
     *
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
        replaceMethodVisitor.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        super.visitLdcInsn(cst);
        replaceMethodVisitor.visitLdcInsn(cst);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
        replaceMethodVisitor.visitTypeInsn(opcode, type);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        super.visitMethodInsn(opcode, owner, name, desc);
        replaceMethodVisitor.visitMethodInsn(opcode, owner, name, desc);
    }


    @Override
    public void visitEnd() {
        super.visitEnd();
        replaceMethodVisitor.visitEnd();
    }

//    visitParameter


    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
        replaceMethodVisitor.visitParameter(name, access);
    }

    //visitAttribute


    @Override
    public void visitAttribute(Attribute attr) {
        super.visitAttribute(attr);
        replaceMethodVisitor.visitAttribute(attr);
    }

    //visitFrame


    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super.visitFrame(type, nLocal, local, nStack, stack);
        replaceMethodVisitor.visitFrame(type, nLocal, local, nStack, stack);
    }

    //visitIntInsn


    @Override
    public void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand);
        replaceMethodVisitor.visitIntInsn(opcode, operand);
    }

    //visitFieldInsn


    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc);
        replaceMethodVisitor.visitFieldInsn(opcode, owner, name, desc);
    }

    //visitInvokeDynamicInsn


    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        replaceMethodVisitor.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    //visitIincInsn


    @Override
    public void visitIincInsn(int var, int increment) {
        super.visitIincInsn(var, increment);
        replaceMethodVisitor.visitIntInsn(var, increment);
    }

    //visitTableSwitchInsn


    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        replaceMethodVisitor.visitTableSwitchInsn(min, max, dflt, labels);
    }


    //visitLookupSwitchInsn


    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        replaceMethodVisitor.visitLookupSwitchInsn(dflt, keys, labels);
    }


    //visitMultiANewArrayInsn


    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        super.visitMultiANewArrayInsn(desc, dims);
        replaceMethodVisitor.visitMultiANewArrayInsn(desc, dims);
    }

    //visitTryCatchBlock


    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        replaceMethodVisitor.visitTryCatchBlock(start, end, handler, type);
    }

    //visitLineNumber


    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line, start);
        replaceMethodVisitor.visitLineNumber(line, start);
    }


    private class JGAnnotationVisitor extends AnnotationVisitor {


        JSONObject jsonObject = new JSONObject();

        String simpleName;
        String processName;

        public JGAnnotationVisitor(String desc) {
            super(Opcodes.ASM5);
            simpleName = desc.substring(desc.lastIndexOf("/") + 1, desc.length() - 1).toLowerCase();
            processName = miConfig.handlers.get(desc);
        }


        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            jsonObject.put(name, value.toString());
        }

        public String getInfo() {
            String info = jsonObject.toJSONString();
            return info;
        }
    }


}
