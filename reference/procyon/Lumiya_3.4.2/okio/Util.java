// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.nio.charset.Charset;

final class Util
{
    public static final Charset UTF_8;
    
    static {
        UTF_8 = Charset.forName("UTF-8");
    }
    
    private Util() {
    }
    
    public static boolean arrayRangeEquals(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) {
        for (int i = 0; i < n3; ++i) {
            if (array[i + n] != array2[i + n2]) {
                return false;
            }
        }
        return true;
    }
    
    public static void checkOffsetAndCount(final long l, final long i, final long j) {
        int n;
        if ((i | j) < 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            int n2;
            if (i > l) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                int n3;
                if (l - i >= j) {
                    n3 = 1;
                }
                else {
                    n3 = 0;
                }
                if (n3 != 0) {
                    return;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException(String.format("size=%s offset=%s byteCount=%s", l, i, j));
    }
    
    public static int reverseBytesInt(final int n) {
        return (0xFF000000 & n) >>> 24 | (0xFF0000 & n) >>> 8 | (0xFF00 & n) << 8 | (n & 0xFF) << 24;
    }
    
    public static long reverseBytesLong(final long n) {
        return (0xFF00000000000000L & n) >>> 56 | (0xFF000000000000L & n) >>> 40 | (0xFF0000000000L & n) >>> 24 | (0xFF00000000L & n) >>> 8 | (0xFF000000L & n) << 8 | (0xFF0000L & n) << 24 | (0xFF00L & n) << 40 | (0xFFL & n) << 56;
    }
    
    public static short reverseBytesShort(final short n) {
        final int n2 = 0xFFFF & n;
        return (short)((n2 & 0xFF) << 8 | (0xFF00 & n2) >>> 8);
    }
    
    public static void sneakyRethrow(final Throwable t) {
        sneakyThrow2(t);
    }
    
    private static <T extends Throwable> void sneakyThrow2(final Throwable t) throws T, Throwable {
        throw t;
    }
}
