package jug.gvsmirnov.javaagent;

import jug.gvsmirnov.javaagent.measurement.Measurement;

public interface MeasurementFactory {

    Measurement make(int javaPid);

}
