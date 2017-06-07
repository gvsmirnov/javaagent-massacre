package jug.gvsmirnov.toolbox;

public class BottomlessClassLoader {

    public static Class<?> loadHugeClass() throws ClassNotFoundException {
        return huge.makeAnotherClone();
    }

    public static Class<?> loadTinyClass() throws ClassNotFoundException {
        return tiny.makeAnotherClone();
    }

    private static final ClassCloningFacility huge = new ClassCloningFacility(HugeClass_XXXXXXXXXX.class);
    private static final ClassCloningFacility tiny = new ClassCloningFacility(TinyClass_XXXXXXXXXX.class);

    // Kudos to @tagir_valeev for this approach: https://habrahabr.ru/post/245333/
    public static class HugeClass_XXXXXXXXXX {
        private long a0, a1, a2, a3, a4, a5, a6, a7, a8, a9;
        private long b0, b1, b2, b3, b4, b5, b6, b7, b8, b9;
        private long c0, c1, c2, c3, c4, c5, c6, c7, c8, c9;
        private long d0, d1, d2, d3, d4, d5, d6, d7, d8, d9;
        private long e0, e1, e2, e3, e4, e5, e6, e7, e8, e9;
        private long f0, f1, f2, f3, f4, f5, f6, f7, f8, f9;

        {
            int a;
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
            try {a=0;} finally {
                a=0;
            }}}}}}}}}}
        }
    }

    public static class TinyClass_XXXXXXXXXX {}

}
