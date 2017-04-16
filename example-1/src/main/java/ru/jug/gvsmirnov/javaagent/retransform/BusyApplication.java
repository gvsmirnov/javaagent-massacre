package ru.jug.gvsmirnov.javaagent.retransform;

import ru.jug.gvsmirnov.toolbox.BottomlessClassLoader;

public class BusyApplication {

    public static volatile Object sink;

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        long loaded = 0;

        while (true) {
            sink = BottomlessClassLoader.loadBigClass();

            if ((loaded++ % 10_000L) == 0L) {
                System.out.println("Loaded " + loaded + " classes");
                Thread.sleep(1);
            }
        }
    }

}
