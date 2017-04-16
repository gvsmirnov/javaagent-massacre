package jug.gvsmirnov.javaagent;

import jug.gvsmirnov.javaagent.os.Hacks;
import jug.gvsmirnov.toolbox.BadThings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Experiment {

    private static final Logger log = LoggerFactory.getLogger(Experiment.class);

    private final File outputRoot;
    private final List<String> command;
    private final Collection<Measurement> measurements;

    public Experiment(File outputRoot, List<String> command, Collection<Measurement> measurements) {
        this.outputRoot = outputRoot;
        this.command = command;
        this.measurements = measurements;
    }

    public void perform() {
        log.info("Will perform measurements: {}", measurements);

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

        for(int i = 0; i < 10; i ++) {
            experiment.waitFor(1, TimeUnit.SECONDS);

            for(Measurement measurement : measurements) {
                log.debug("Measurement {}: {}", measurement.name(), measurement.take(pid));
            }
        }

        log.info("Terminating experiment");

        experiment.destroy();

        return experiment.waitFor();
    }
}
