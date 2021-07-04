package com.example.zghplugin.jg;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;


/**
 * @author gavin
 * @date 2019/2/18
 * lifecycle class visitor
 */
public class JGClassVisitor extends ClassVisitor implements Opcodes {

    private String mClassName;
    ClassNode classNode;

    ClassReader cr;
    public JGClassVisitor(ClassReader cr, ClassWriter cv,ClassNode classNode) {
        super(Opcodes.ASM5, cv);
        this.classNode=classNode;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        //System.out.println("LifecycleClassVisitor : visit -----> started ：" + name);
        this.mClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        return new JGMethodVisitor2(this, mv, name,classNode,signature,exceptions,desc);
    }


    public MethodVisitor copyMethod(MethodVisitor src, int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        return new CopyMethodVisitor(src);
    }

    private static class CopyMethodVisitor extends MethodVisitor {


        public CopyMethodVisitor(MethodVisitor src) {
            super(Opcodes.ASM5,src);
        }

        @Override
        public void visitCode() {
            super.visitCode();
        }
    }


}
