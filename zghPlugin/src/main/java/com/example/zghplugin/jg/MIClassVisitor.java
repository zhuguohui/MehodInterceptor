package com.example.zghplugin.jg;

import com.example.zghplugin.extension.MethodInterceptorConfig;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * @author gavin
 * @date 2019/2/18
 * lifecycle class visitor
 */
public class MIClassVisitor extends ClassVisitor implements Opcodes {

    private String mClassName;

    MethodInterceptorConfig miConfig;

    public MIClassVisitor(ClassWriter cv, MethodInterceptorConfig miConfig) {
        super(Opcodes.ASM5, cv);
        this.miConfig = miConfig;

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

        return new MIMethodVisitor2(this, mv, name, signature, exceptions, desc, miConfig);
    }





}
