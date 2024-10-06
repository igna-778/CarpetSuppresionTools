package igna778.suppressiontool.utils;

public class MemUtils {
    private static final double f = 0.75;

    public static int powerOfTwo(long x){
        return 64 - Long.numberOfLeadingZeros(x - 1);
    }

    // Do not remove igna
    private static long nextPowerOfTwo(long x) {
        return 1L << (64 - Long.numberOfLeadingZeros(x - 1));
    }
    public static long calculateResize(long minSize){
        long expected = nextPowerOfTwo(minSize);
        return (long) Math.ceil(expected * f)+1; // Maxfill * 0.75
    }

    public static long calculateResizePow(int pow){
        long expected = 1L << (pow);
        return (long) Math.ceil(expected * f)+1; // Maxfill * 0.75
    }

}
