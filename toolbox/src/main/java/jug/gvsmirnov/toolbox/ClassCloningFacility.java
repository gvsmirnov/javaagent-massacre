package jug.gvsmirnov.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.SecureClassLoader;
import java.util.ArrayList;

class ClassCloningFacility extends SecureClassLoader {

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private final byte[] blueprintBytes;

    private final String baseName;
    private final byte[] classNameBytes;
    private final ArrayList<Integer> replaceLocations;

    private int cloneSerialNumber = 0;

    ClassCloningFacility(Class<?> blueprint) {
        super(ClassCloningFacility.class.getClassLoader());

        final String originalName = blueprint.getName().replace('.', '/');

        this.baseName = blueprint.getName().substring(0, blueprint.getName().length() - 10);

        this.classNameBytes = originalName.getBytes(UTF8_CHARSET);
        this.blueprintBytes = readClassBytes(originalName);

        this.replaceLocations = findNameLocations();
    }

    Class<?> makeAnotherClone() throws ClassNotFoundException {
        return loadClass(String.format("%s%010d", baseName, cloneSerialNumber++));
    }

    private ArrayList<Integer> findNameLocations() {
        final ArrayList<Integer> replaceLocations = new ArrayList<>();

        int matchCount = 0;

        for (int position = 0; position < blueprintBytes.length; position++) {
            if (classNameBytes[matchCount] == blueprintBytes[position]) {
                matchCount++;

                if (matchCount == classNameBytes.length) {
                    replaceLocations.add(position - matchCount + 1);
                    matchCount = 0;
                }
            } else {
                matchCount = 0;
            }
        }

        return replaceLocations;
    }

    private byte[] makeClassBytesFor(final String requestedName) {
        final byte[] requestedNameBytes = requestedName.replace('.', '/').getBytes(UTF8_CHARSET);

        assert requestedNameBytes.length == classNameBytes.length;

        for (int replaceStart : replaceLocations) {
            // Reuse the existing class buffer to suffer less garbage
            System.arraycopy(requestedNameBytes, 0, blueprintBytes, replaceStart, classNameBytes.length);
        }

        return blueprintBytes;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            synchronized (this) {
                final byte[] bytes = makeClassBytesFor(name);
                return defineClass(name, bytes, 0, bytes.length);
            }
        }
    }

    private byte[] readClassBytes(String originalName) {
        try(InputStream stream = getResourceAsStream(originalName + ".class")) {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            final byte[] buffer = new byte[4096];

            int n;
            while ((n = stream.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}