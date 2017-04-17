package jug.gvsmirnov.toolbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

public class BadThings {

    public static <T> T wrapCheckedExceptions(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    public interface ThrowyRunnable {
        void run() throws Throwable;
    }

    public static void wrapCheckedExceptions(final ThrowyRunnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    public static volatile Object sink;

    public static void expandHeap() {
        // Doing -XX:+AlwaysPreTouch masks the effect on Internal structures
        // Therefore, we just make the heap expand by becoming very fat

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
