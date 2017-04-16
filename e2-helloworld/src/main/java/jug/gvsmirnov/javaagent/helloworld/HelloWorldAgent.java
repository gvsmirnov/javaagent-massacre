package jug.gvsmirnov.javaagent.helloworld;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class HelloWorldAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Hello, preworld!");
//        inst.addTransformer(HelloWorldAgent::transform);
    }

    private static byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return null;
    }
}
