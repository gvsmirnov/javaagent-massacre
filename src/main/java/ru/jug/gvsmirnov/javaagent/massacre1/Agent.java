package ru.jug.gvsmirnov.javaagent.massacre1;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Agent {

    public static void premain(final String agentArgs, final Instrumentation inst) {
        System.out.println("Hello, preworld!");

        inst.addTransformer(Agent::transform, true);

        new Thread(() -> {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
            retransform(inst);
        }).start();
    }

    private static byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (!className.contains("jug")) {
            return null;
        }

        if (classBeingRedefined == null) {
            System.out.printf("Transforming: [loader: %s] [name: %s] (%d bytes originally)%n",
                    loader, className, classfileBuffer.length
            );
        } else {

            System.out.println("Retransforming " + classBeingRedefined.getSimpleName());
        }

        return null;
    }

    private static void retransform(final Instrumentation inst) {

        System.out.println("Running retransformation");

        try {
            for (Class clazz : inst.getAllLoadedClasses()) {
                if (clazz.getName().contains("jug")) {
                    inst.retransformClasses(clazz);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
