package ru.jug.gvsmirnov.javaagent.massacre1;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class NoopAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(NoopAgent::transform, true);
    }

    private static byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return null;
    }

    /*
        No agent:
        Total: reserved=42,110,983KB, committed=36,722,415KB
            Internal (reserved=17,804KB,    committed=17,804KB)

        With Agent:
        Total: reserved=51,311,858KB, committed=47,826,782KB
            Internal (reserved=21,352,318KB, committed=21,352,318KB)

        With Agent (no retransformation):
        Total: reserved=39,430,385KB, committed=34,997,789KB
            Internal (reserved=17,247KB,    committed=17,247KB)

        With Agent (retransformation, return null)
        Total: reserved=39446335KB, committed=35548543KB
            Internal (reserved=17250KB, committed=17250KB)

        // TODO: figure out the crazy numbers of virtual memory usage. Why no swapping? Why commit and not use?
     */

}
