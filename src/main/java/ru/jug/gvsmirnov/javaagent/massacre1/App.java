package ru.jug.gvsmirnov.javaagent.massacre1;

import ru.jug.gvsmirnov.javaagent.perversions.Greeter;

public class App {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i ++) {
            Greeter.greet();
            Thread.sleep(1000);
        }
    }

}
