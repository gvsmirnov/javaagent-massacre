package jug.gvsmirnov.javaagent.measurement;

import jug.gvsmirnov.javaagent.os.Hacks;

public class ResidentSetSize implements Measurement {

    private final int pid;

    public ResidentSetSize(int pid) {
        this.pid = pid;
    }

    @Override
    public String name() {
        return "rss";
    }

    @Override
    public String take(int iteration) {
        return String.format("%, 12d KB", Hacks.hacks.getResidentSetSize(pid) / 1024);
    }
}
