package jug.gvsmirnov.javaagent.retransform;

import jug.gvsmirnov.toolbox.BottomlessClassLoader;

import java.util.ArrayList;
import java.util.Collection;

public class BusyApplication {

    public static volatile Collection<Object> classInstanceSink = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        expandHeap();

        long loaded = 0;

        while (true) {
            final Class<?> clazz = BottomlessClassLoader.loadBigClass();
            classInstanceSink.add(clazz.newInstance());

            if ((loaded++ % 10_000L) == 0L) {

                System.out.println("Loaded " + loaded + " classes, latest:" + clazz.getName());
                Thread.sleep(1);
            }
        }
    }

    public static volatile Object sink;

    private static void expandHeap() {
        // Doing -XX:+AlwaysPreTouch masks the effect on Internal structures
        // Therefore, we just produce garbage for as long as we can

        try {
            final Collection<byte[]> garbage = new ArrayList<>();

            while (true) {
                final byte[] bytes = new byte[64 * 1024];

                sink = bytes;
                garbage.add(bytes);
            }
        } catch (OutOfMemoryError ignored) {}
    }

}
