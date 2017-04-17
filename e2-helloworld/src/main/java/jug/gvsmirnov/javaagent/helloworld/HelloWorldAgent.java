package jug.gvsmirnov.javaagent.helloworld;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.PrintStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Type.getType;
import static org.objectweb.asm.commons.Method.getMethod;

public class HelloWorldAgent implements ClassFileTransformer {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Hello, preworld!");
        inst.addTransformer(new HelloWorldAgent());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {

//        if(!className.equals(HelloWorldApplication.class.getName())) {
        if(!className.equals("jug/gvsmirnov/javaagent/helloworld/HelloWorldApplication")) {
            return null;
        }

        System.out.println("Transforming class " + className);

        return transformBytes(classfileBuffer);
    }

    private static byte[] transformBytes(byte[] originalClassBytes) {
        final ClassReader cr  = new ClassReader(originalClassBytes);
        final ClassWriter cw  = new ClassWriter(0);

        final ClassVisitor cv = new ClassVisitor(ASM5, cw) {
            @Override
            public MethodVisitor visitMethod(
                    int access, String methodName, String desc, String signature, String[] exceptions
            ) {
                if (!methodName.equals("main")) {
                    return super.visitMethod(access, methodName, desc, signature, exceptions);
                }

                System.out.println("Replacing method " + methodName);

                return new GeneratorAdapter(
                        ACC_PUBLIC + ACC_STATIC, getMethod("void main (String[])"), null, null, cw
                ) {{
                    getStatic(getType(System.class), "out", getType(PrintStream.class));
                    push("Hello, transformed world!");
                    invokeVirtual(getType(PrintStream.class), getMethod("void println (String)"));
                    returnValue();
                    endMethod();
                }};
            }
        };

        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}
