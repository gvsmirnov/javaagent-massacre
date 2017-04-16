package jug.gvsmirnov.javaagent.os;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static jug.gvsmirnov.toolbox.BadThings.wrapCheckedExceptions;

public class MacOsHacks extends Hacks {

    private static final String JAVA_LANG_UNIXPROCESS = "java.lang.UNIXProcess";

    private static final Field pidField = hackField(JAVA_LANG_UNIXPROCESS, "pid");

    @Override
    public int getPid(Process process) {
        if (!process.getClass().getName().equals(JAVA_LANG_UNIXPROCESS)) {
            throw new IllegalArgumentException(
                    process.getClass() + " is not a " + JAVA_LANG_UNIXPROCESS
            );
        }

        return wrapCheckedExceptions(() -> pidField.getInt(process));
    }

    @Override
    public long getResidentSetSize(int pid) {
        return wrapCheckedExceptions(() -> {
            ProcessResult processResult = new ProcessExecutor("ps", "-o", "rss", "-p", Integer.toString(pid))
                    .readOutput(true)
                    .execute();

            for (String line : processResult.getOutput().getLines()) {
                System.out.println("PROCESS << " + line);
            }

            return 0;
        });
    }


}
