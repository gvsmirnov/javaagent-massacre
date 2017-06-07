package jug.gvsmirnov.javaagent.jvmti;

import jug.gvsmirnov.toolbox.NativeUtils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.instrument .Instrumentation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JvmtiAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        NativeUtils.load("/libmain.dylib");
        startNativeAgent();
    }

    public static native void startNativeAgent();

public static void onClassLoaded(Class<?> clazz) {
    System.out.println("Hello, " + clazz.getSimpleName());
}

    // So, what happens is (if printing just the clazz):
    //  * we try to load sun.launcher.LauncherHelper
    //  * to print it out, we try to load the PrintStream
    //  * to print it out, we try to load the StringBuilder
    //  * to print it out, we try to load the PrintStream
    //  * UH-OH!

}
