package jug.gvsmirnov.javaagent.os;

import java.lang.reflect.Field;

import static jug.gvsmirnov.toolbox.BadThings.wrapCheckedExceptions;

public abstract class Hacks {

    public static final Hacks hacks = getHacks();

    public abstract int getPid(Process process);

    public abstract long getResidentSetSize(int pid);

    public static Field hackField(final String className, final String fieldName) {
        return wrapCheckedExceptions(() -> {
            final Field field = Class.forName(className).getDeclaredField(fieldName);
            field.setAccessible(true);

            return field;
        });
    }

    private static Hacks getHacks() {
        return new MacOsHacks(); // FIXME: implement others
    }

    Hacks() {}
}
