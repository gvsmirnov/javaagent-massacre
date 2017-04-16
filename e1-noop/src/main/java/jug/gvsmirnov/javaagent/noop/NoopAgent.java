package jug.gvsmirnov.javaagent.noop;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class NoopAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(NoopAgent::transform, true);
    }

    private static byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return classfileBuffer;
    }

}
