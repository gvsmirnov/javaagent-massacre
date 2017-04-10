package ru.jug.gvsmirnov.javaagent.massacre1;

import ru.jug.gvsmirnov.javaagent.perversions.BottomlessClassLoader;

public class App {

    public static volatile Object sink;

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        long loaded = 0;

        while (true) {
            sink = BottomlessClassLoader.loadBigClass();

            if ((loaded++ % 10_000L) == 0L) {
                System.out.println("Loaded " + sink);
                Thread.sleep(1);
            }
        }
    }

}
