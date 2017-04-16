package jug.gvsmirnov.javaagent;


import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ExperimentBuilder {

    private final String name;
    private String agentJarPath;
    private String applicationJarPath;

    private boolean trackResidentSetSize = false;

    public ExperimentBuilder(String name) {
        this.name = name;
    }

    public ExperimentBuilder applicationJar(String path) {
        this.applicationJarPath = path;
        return this;
    }

    public ExperimentBuilder withAgent(String jarPath) {
        this.agentJarPath = jarPath;
        return this;
    }

    public ExperimentBuilder withoutAgent() {
        this.agentJarPath = null;
        return this;
    }

    public ExperimentBuilder trackResidentSetSize(boolean trackResidentSetSize) {
        this.trackResidentSetSize = trackResidentSetSize;
        return this;
    }

    public Experiment build() {
        validate();
        return new Experiment(getOutputRoot(), buildCommand(), buildMeasurements());
    }

    // TODO: private static final String rootDir            = System.getProperty("root.dir", "build/experiment");
    private File getOutputRoot() {
        return new File(new File("build", name), now());
    }

    private List<String> buildCommand() {
        final List<String> result = new ArrayList<>();

        result.add("java");
        result.add("-jar");

        if (agentJarPath != null) {
            result.add("-javaagent:" + agentJarPath);
        }

        result.add(applicationJarPath);

        return result;
    }

    private Collection<Measurement> buildMeasurements() {
        final List<Measurement> result = new ArrayList<>();

        if (trackResidentSetSize) {
            result.add(new Measurement.ResidentSetSize());
        }

        return result;
    }

    private void validate() {
        Objects.requireNonNull(name, "Experiment must have a name");

        // TODO: throw if app jar doe not exist
        // TODO: throw if agent jar doe not exist
    }

    private static String now() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    }
}
