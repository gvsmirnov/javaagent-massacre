package jug.gvsmirnov.toolbox;

import java.util.concurrent.Callable;

public class BadThings {

    public static <T> T wrapCheckedExceptions(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
