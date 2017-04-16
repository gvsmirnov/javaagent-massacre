package jug.gvsmirnov.javaagent.measurement;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jug.gvsmirnov.toolbox.BadThings.wrapCheckedExceptions;

public class SampleNativeMemoryTracking implements Measurement {

    private final File experimentRoot;
    private final int pid;


    public SampleNativeMemoryTracking(File experimentRoot, int pid) {
        this.experimentRoot = experimentRoot;
        this.pid = pid;
    }

    @Override
    public String name() {
        return "nmt";
    }

    @Override
    public String take(int iteration) {
        return wrapCheckedExceptions(() -> {
            final File outputFile = new File(experimentRoot, "VM.native_memory." + iteration + ".txt");

            final ProcessResult processResult = new ProcessExecutor()
                    .command("jcmd", Integer.toString(pid), "VM.native_memory", "summary")
                    .redirectOutput(new FileOutputStream(outputFile))
                    .execute();

            if (processResult.getExitValue() != 0) {
                throw new RuntimeException(
                        "Failed to get native memory usage summary: " + processResult.getExitValue()
                );
            }

//            return parseNmtFile(outputFile).toString();
            return outputFile.getPath();
        });
    }

    public static class NativeMemoryTrackingSample  {
        private long classReserved, classCommitted;
        private long internalReserved, internalCommitted;

        private long totalReserved, totalCommitted;

        @Override
        public String toString() {
            return String.format(
                    "%, 12d KB (Committed); %, 12d KB (Reserved)", totalCommitted / 1024, totalReserved / 1024
//                    "%, 12d KB (Class); %, 12d KB (Internal)", classCommitted / 1024, internalCommitted / 1024
            );
        }
    }

    private static NativeMemoryTrackingSample parseNmtFile(File file) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(file));

        final NativeMemoryTrackingSample sample = new NativeMemoryTrackingSample();
        String line;

        while((line = reader.readLine()) != null) {
            if (line.startsWith("Total: ")) {
                final long[] total = extractMemoryBytes(line);

                sample.totalReserved  = total[0];
                sample.totalCommitted = total[1];
                continue;
            }

            line = line.replaceAll("^-\\s+", "");

            if (line.startsWith("Class")) {
                final long[] total = extractMemoryBytes(line);

                sample.classReserved  = total[0];
                sample.classCommitted = total[1];
            } else if (line.startsWith("Internal")) {
                final long[] total = extractMemoryBytes(line);

                sample.internalReserved  = total[0];
                sample.internalCommitted = total[1];
            }
        }

        return sample;
    }

    private static final Pattern pattern = Pattern.compile(".*reserved=(\\d+)KB, committed=(\\d+)KB");

    private static long[] extractMemoryBytes(final String line) {
        final Matcher matcher = pattern.matcher(line);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid memory usage line: " + line);
        }

        final long reserved  = Long.valueOf(matcher.group(1)) * 1024;
        final long committed = Long.valueOf(matcher.group(2)) * 1024;

        return new long[] { reserved, committed };
    }
}