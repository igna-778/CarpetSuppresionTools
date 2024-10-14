package igna778.suppressiontool.utils;

public class MemUtils {
    private static final int sizeOfBuff = 1024;
    private static byte[][] membuff = new byte[sizeOfBuff][];
    private static final int bchunkSize = 5 * 1024 * 1024; // 5 MB

    private static final double f = 0.75; // Const for upsize calculation

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

    public static void simulateOOM(int bSize) {
        if(bSize <= 0)
            bSize = bchunkSize;
        int i = 0;
        try {
            while (true){
                if(i > membuff.length){
                    throw new OutOfMemoryError("Artificial Out Of Memory due to Array Index out of bounds");
                }
                membuff[i] = new byte[bSize]; // Use 10MB of memory
                i++;
            }
        } catch (OutOfMemoryError err) {
            membuff = null;
            membuff = new byte[sizeOfBuff][];
            throw err;
        }
    }

    public static long getFreeMemory() {
        try {
            Runtime rntime = Runtime.getRuntime();
            long maxmem = rntime.maxMemory();
            long freemem = rntime.freeMemory();
            long totalmem = rntime.totalMemory();

            //The memory that we haven't allocated yet (Max - current total) plus the free amount from the total (+ Free)
            return maxmem - totalmem + freemem;
        } catch (OutOfMemoryError e) {
            throw  new OutOfMemoryError("OutOfMemory while calculating free memory");
        }
    }
}
