package jug.gvsmirnov.javaagent.jvmti;

import jug.gvsmirnov.toolbox.NativeUtils;

import java.lang.instrument.Instrumentation;

public class JvmtiAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
//        System.out.println("Loading native agent...");
        NativeUtils.load("/libmain.dylib");
        startNativeAgent();
    }

    public static native void startNativeAgent();

    public static void onClassLoaded(Class<?> clazz) {
        System.out.println("Class loaded: " + clazz.getSimpleName());
    }

}
