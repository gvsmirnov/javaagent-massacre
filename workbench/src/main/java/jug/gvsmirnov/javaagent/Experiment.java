package jug.gvsmirnov.javaagent;

import jug.gvsmirnov.javaagent.measurement.Measurement;
import jug.gvsmirnov.javaagent.os.Hacks;
import jug.gvsmirnov.toolbox.BadThings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class Experiment {

    private static final Logger log = LoggerFactory.getLogger(Experiment.class);

    private final File outputRoot;
    private final List<String> command;
    private final Collection<MeasurementFactory> measurementFactories;

    public Experiment(File outputRoot, List<String> command, Collection<MeasurementFactory> measurementFactories) {
        this.outputRoot = outputRoot;
        this.command = command;
        this.measurementFactories = measurementFactories;
    }

    public void perform() {
        if (outputRoot.mkdirs()) {
            log.debug("Created dir: {}", outputRoot.getAbsolutePath());
        }

        log.debug("Experiment output will be piped to " + outputRoot.getAbsolutePath());

        log.info("Starting experiment: {}", command);

        int exitCode = BadThings.wrapCheckedExceptions(this::performExperiment);

        log.info("Experiment ended with code " + exitCode);
    }

    private int performExperiment() throws InterruptedException, IOException {
        final Process experiment = new ProcessBuilder(command)
                .redirectError (new File(outputRoot, "stderr.log"))
                .redirectOutput(new File(outputRoot, "stdout.log"))
                .start();

        final int pid = Hacks.hacks.getPid(experiment);

        log.debug("Experiment PID: {}", pid);

        final Collection<Measurement> measurements = measurementFactories.stream()
                .map(factory -> factory.make(pid))
                .collect(toList());

        for(int i = 0; i < 15; i ++) {
            experiment.waitFor(1, TimeUnit.SECONDS);

            for(Measurement measurement : measurements) {
                log.debug("Measurement '{}': {}", measurement.name(), measurement.take(i));
            }
        }

        log.info("Terminating experiment");

        experiment.destroy();

        return experiment.waitFor();
    }
}
