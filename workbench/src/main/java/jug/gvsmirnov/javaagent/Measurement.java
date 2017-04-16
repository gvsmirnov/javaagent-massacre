package jug.gvsmirnov.javaagent;

import jug.gvsmirnov.javaagent.os.Hacks;

public interface Measurement {

    String name();

    String take(int pid);

    public class ResidentSetSize implements Measurement {

        @Override
        public String name() {
            return "OS/RSS";
        }

        @Override
        public String take(int pid) {
            return Hacks.hacks.getResidentSetSize(pid) + " bytes";
        }
    }

}
