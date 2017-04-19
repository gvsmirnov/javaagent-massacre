package jug.gvsmirnov.javaagent;

import jug.gvsmirnov.javaagent.measurement.Measurement;
import jug.gvsmirnov.javaagent.os.Hacks;
import jug.gvsmirnov.toolbox.BadThings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jDebugOutputStream;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class Experiment {

    private static final Logger       log = LoggerFactory.getLogger(Experiment.class);
    private static final Logger stdOutLog = LoggerFactory.getLogger(Experiment.class.getName() + ".stdout");
    private static final Logger stdErrLog = LoggerFactory.getLogger(Experiment.class.getName() + ".stderr");

    private final File outputRoot;
    private final List<String> command;
    private final Collection<MeasurementFactory> measurementFactories;

    private final boolean logStdOut;
    private final boolean logStdErr;

    private final int iterations;

    public Experiment(File outputRoot, List<String> command, Collection<MeasurementFactory> measurementFactories,
                      boolean logStdOut, boolean logStdErr, int iterations) {
        this.outputRoot = outputRoot;
        this.command = command;
        this.measurementFactories = measurementFactories;
        this.logStdOut = logStdOut;
        this.logStdErr = logStdErr;
        this.iterations = iterations;
    }

    public void perform() {
        if (outputRoot.mkdirs()) {
            log.debug("Created dir: {}", outputRoot.getAbsolutePath());
        }

        log.debug("Experiment data will be written to " + outputRoot.getAbsolutePath());

        log.info("Starting experiment: {}", String.join(" ", command));

        int exitCode = BadThings.wrapCheckedExceptions(this::performExperiment);

        log.info("Experiment ended with code " + exitCode);
    }

    private int performExperiment() throws InterruptedException, IOException {

        final Process javaProcess = new ProcessExecutor(command)
                .redirectOutput(getStdOutRedirect())
                .redirectError(getStdErrRedirect())
                .start().getProcess();

        Runtime.getRuntime().addShutdownHook(new Thread(javaProcess::destroy));

        final int pid = Hacks.hacks.getPid(javaProcess);

        log.debug("Experiment PID: {}", pid);

        final Collection<Measurement> measurements = measurementFactories.stream()
                .map(factory -> factory.make(pid))
                .collect(toList());

        for(int iteration = 0; iteration < iterations; iteration ++) {
            if (javaProcess.waitFor(1, TimeUnit.SECONDS)) {
                return javaProcess.exitValue();
            }

            for(Measurement measurement : measurements) {
                log.debug("Measurement '{}': {}", measurement.name(), measurement.take(iteration));
            }
        }

        log.info("Terminating experiment");

        javaProcess.destroy();

        return javaProcess.waitFor();
    }

    // TODO: deduplicate
    private OutputStream getStdOutRedirect() throws FileNotFoundException {
        if (this.logStdOut) {
            return new Slf4jDebugOutputStream(stdOutLog);
        } else {
            return new FileOutputStream(new File(outputRoot, "stdout.log"));
        }
    }

    private OutputStream getStdErrRedirect() throws FileNotFoundException {
        if (this.logStdErr) {
            return new Slf4jDebugOutputStream(stdErrLog);
        } else {
            return new FileOutputStream(new File(outputRoot, "stderr.log"));
        }
    }


}
