package jug.gvsmirnov.javaagent.noop;

import jug.gvsmirnov.toolbox.BadThings;
import jug.gvsmirnov.toolbox.BottomlessClassLoader;

import java.util.ArrayList;
import java.util.Collection;

public class BusyApplication {

    public static volatile Collection<Object> classInstanceSink = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        BadThings.expandHeap();

        long loaded = 0;

        while (true) {
            final Class<?> clazz = BottomlessClassLoader.loadHugeClass();
            classInstanceSink.add(clazz.newInstance());

            if ((loaded++ % 10_000L) == 0L) {

                // TODO: calling clazz.getSimpleName resuts in an IncompatibeClassChangeError
                System.out.println("Loaded " + loaded + " classes, latest:" + clazz.getName());
                Thread.sleep(1);
            }
        }
    }

}
