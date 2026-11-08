package okhttp3.internal.cache2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import okio.Buffer;
/* loaded from: classes.dex */
final class FileOperator {
    private static final int BUFFER_SIZE = 8192;
    private final byte[] byteArray = new byte[8192];
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(this.byteArray);
    private final FileChannel fileChannel;

    public FileOperator(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x004e, code lost:
        throw new java.io.EOFException();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void read(long r9, okio.Buffer r11, long r12) throws java.io.IOException {
        /*
            r8 = this;
            r6 = 0
            r1 = 1
            r2 = 0
            int r0 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r0 < 0) goto L11
            r0 = r1
        L9:
            if (r0 != 0) goto L13
            java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException
            r0.<init>()
            throw r0
        L11:
            r0 = r2
            goto L9
        L13:
            int r0 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r0 > 0) goto L47
            r0 = r1
        L18:
            if (r0 != 0) goto L56
            java.nio.ByteBuffer r0 = r8.byteBuffer     // Catch: java.lang.Throwable -> L4f
            r4 = 8192(0x2000, double:4.0474E-320)
            long r4 = java.lang.Math.min(r4, r12)     // Catch: java.lang.Throwable -> L4f
            int r3 = (int) r4     // Catch: java.lang.Throwable -> L4f
            r0.limit(r3)     // Catch: java.lang.Throwable -> L4f
            java.nio.channels.FileChannel r0 = r8.fileChannel     // Catch: java.lang.Throwable -> L4f
            java.nio.ByteBuffer r3 = r8.byteBuffer     // Catch: java.lang.Throwable -> L4f
            int r0 = r0.read(r3, r9)     // Catch: java.lang.Throwable -> L4f
            r3 = -1
            if (r0 == r3) goto L49
            java.nio.ByteBuffer r0 = r8.byteBuffer     // Catch: java.lang.Throwable -> L4f
            int r0 = r0.position()     // Catch: java.lang.Throwable -> L4f
            byte[] r3 = r8.byteArray     // Catch: java.lang.Throwable -> L4f
            r4 = 0
            r11.write(r3, r4, r0)     // Catch: java.lang.Throwable -> L4f
            long r4 = (long) r0
            long r9 = r9 + r4
            long r4 = (long) r0
            long r12 = r12 - r4
            java.nio.ByteBuffer r0 = r8.byteBuffer
            r0.clear()
            goto L13
        L47:
            r0 = r2
            goto L18
        L49:
            java.io.EOFException r0 = new java.io.EOFException     // Catch: java.lang.Throwable -> L4f
            r0.<init>()     // Catch: java.lang.Throwable -> L4f
            throw r0     // Catch: java.lang.Throwable -> L4f
        L4f:
            r0 = move-exception
            java.nio.ByteBuffer r1 = r8.byteBuffer
            r1.clear()
            throw r0
        L56:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.FileOperator.read(long, okio.Buffer, long):void");
    }

    public void write(long j, Buffer buffer, long j2) throws IOException {
        if (!(j2 < 0)) {
            if (j2 <= buffer.size()) {
                long j3 = j;
                while (true) {
                    if (j2 <= 0) {
                        return;
                    }
                    try {
                        int min = (int) Math.min(8192L, j2);
                        buffer.read(this.byteArray, 0, min);
                        this.byteBuffer.limit(min);
                        do {
                            j3 += this.fileChannel.write(this.byteBuffer, j3);
                        } while (this.byteBuffer.hasRemaining());
                        j2 -= min;
                    } finally {
                        this.byteBuffer.clear();
                    }
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
