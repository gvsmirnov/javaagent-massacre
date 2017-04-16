package jug.gvsmirnov.javaagent.measurement;

public interface Measurement {

    String name();

    String take(int iteration);

}
