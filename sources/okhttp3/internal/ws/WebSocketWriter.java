package okhttp3.internal.ws;

import com.google.common.logging.nano.Vr;
import java.io.IOException;
import java.util.Random;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.Sink;
import okio.Timeout;

final class WebSocketWriter {
    static final /* synthetic */ boolean $assertionsDisabled;
    boolean activeWriter;
    final Buffer buffer = new Buffer();
    final FrameSink frameSink = new FrameSink();
    final boolean isClient;
    final byte[] maskBuffer;
    final byte[] maskKey;
    final Random random;
    final BufferedSink sink;
    boolean writerClosed;

    final class FrameSink implements Sink {
        boolean closed;
        long contentLength;
        int formatOpcode;
        boolean isFirstFrame;

        FrameSink() {
        }

        public void close() throws IOException {
            if (!this.closed) {
                synchronized (WebSocketWriter.this) {
                    WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, true);
                }
                this.closed = true;
                WebSocketWriter.this.activeWriter = false;
                return;
            }
            throw new IOException("closed");
        }

        public void flush() throws IOException {
            if (!this.closed) {
                synchronized (WebSocketWriter.this) {
                    WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, false);
                }
                this.isFirstFrame = false;
                return;
            }
            throw new IOException("closed");
        }

        public Timeout timeout() {
            return WebSocketWriter.this.sink.timeout();
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x004e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void write(okio.Buffer r11, long r12) throws java.io.IOException {
            /*
                r10 = this;
                r1 = 1
                r6 = 0
                boolean r0 = r10.closed
                if (r0 != 0) goto L_0x0025
                okhttp3.internal.ws.WebSocketWriter r0 = okhttp3.internal.ws.WebSocketWriter.this
                okio.Buffer r0 = r0.buffer
                r0.write((okio.Buffer) r11, (long) r12)
                boolean r0 = r10.isFirstFrame
                if (r0 != 0) goto L_0x002e
            L_0x0011:
                r0 = r6
            L_0x0012:
                okhttp3.internal.ws.WebSocketWriter r2 = okhttp3.internal.ws.WebSocketWriter.this
                okio.Buffer r2 = r2.buffer
                long r2 = r2.completeSegmentByteCount()
                r4 = 0
                int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r4 > 0) goto L_0x004e
            L_0x0020:
                if (r1 != 0) goto L_0x0024
                if (r0 == 0) goto L_0x0050
            L_0x0024:
                return
            L_0x0025:
                java.io.IOException r0 = new java.io.IOException
                java.lang.String r1 = "closed"
                r0.<init>(r1)
                throw r0
            L_0x002e:
                long r2 = r10.contentLength
                r4 = -1
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 == 0) goto L_0x0011
                okhttp3.internal.ws.WebSocketWriter r0 = okhttp3.internal.ws.WebSocketWriter.this
                okio.Buffer r0 = r0.buffer
                long r2 = r0.size()
                long r4 = r10.contentLength
                r8 = 8192(0x2000, double:4.0474E-320)
                long r4 = r4 - r8
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 > 0) goto L_0x004c
                r0 = r1
            L_0x0048:
                if (r0 != 0) goto L_0x0011
                r0 = r1
                goto L_0x0012
            L_0x004c:
                r0 = r6
                goto L_0x0048
            L_0x004e:
                r1 = r6
                goto L_0x0020
            L_0x0050:
                okhttp3.internal.ws.WebSocketWriter r7 = okhttp3.internal.ws.WebSocketWriter.this
                monitor-enter(r7)
                okhttp3.internal.ws.WebSocketWriter r0 = okhttp3.internal.ws.WebSocketWriter.this     // Catch:{ all -> 0x0061 }
                int r1 = r10.formatOpcode     // Catch:{ all -> 0x0061 }
                boolean r4 = r10.isFirstFrame     // Catch:{ all -> 0x0061 }
                r5 = 0
                r0.writeMessageFrameSynchronized(r1, r2, r4, r5)     // Catch:{ all -> 0x0061 }
                monitor-exit(r7)     // Catch:{ all -> 0x0061 }
                r10.isFirstFrame = r6
                goto L_0x0024
            L_0x0061:
                r0 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x0061 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.WebSocketWriter.FrameSink.write(okio.Buffer, long):void");
        }
    }

    static {
        boolean z = false;
        if (!WebSocketWriter.class.desiredAssertionStatus()) {
            z = true;
        }
        $assertionsDisabled = z;
    }

    WebSocketWriter(boolean z, BufferedSink bufferedSink, Random random2) {
        byte[] bArr = null;
        if (bufferedSink == null) {
            throw new NullPointerException("sink == null");
        } else if (random2 != null) {
            this.isClient = z;
            this.sink = bufferedSink;
            this.random = random2;
            this.maskKey = !z ? null : new byte[4];
            this.maskBuffer = z ? new byte[8192] : bArr;
        } else {
            throw new NullPointerException("random == null");
        }
    }

    private void writeControlFrameSynchronized(int i, ByteString byteString) throws IOException {
        boolean z = false;
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (!this.writerClosed) {
            int size = byteString.size();
            if (((long) size) <= 125) {
                z = true;
            }
            if (!z) {
                throw new IllegalArgumentException("Payload size must be less than or equal to 125");
            }
            this.sink.writeByte(i | 128);
            if (!this.isClient) {
                this.sink.writeByte(size);
                this.sink.write(byteString);
            } else {
                this.sink.writeByte(size | 128);
                this.random.nextBytes(this.maskKey);
                this.sink.write(this.maskKey);
                byte[] byteArray = byteString.toByteArray();
                WebSocketProtocol.toggleMask(byteArray, (long) byteArray.length, this.maskKey, 0);
                this.sink.write(byteArray);
            }
            this.sink.flush();
        } else {
            throw new IOException("closed");
        }
    }

    /* access modifiers changed from: package-private */
    public Sink newMessageSink(int i, long j) {
        if (!this.activeWriter) {
            this.activeWriter = true;
            this.frameSink.formatOpcode = i;
            this.frameSink.contentLength = j;
            this.frameSink.isFirstFrame = true;
            this.frameSink.closed = false;
            return this.frameSink;
        }
        throw new IllegalStateException("Another message writer is active. Did you call close()?");
    }

    /* access modifiers changed from: package-private */
    public void writeClose(int i, ByteString byteString) throws IOException {
        ByteString byteString2 = ByteString.EMPTY;
        if (!(i == 0 && byteString == null)) {
            if (i != 0) {
                WebSocketProtocol.validateCloseCode(i);
            }
            Buffer buffer2 = new Buffer();
            buffer2.writeShort(i);
            if (byteString != null) {
                buffer2.write(byteString);
            }
            byteString2 = buffer2.readByteString();
        }
        synchronized (this) {
            try {
                writeControlFrameSynchronized(8, byteString2);
                this.writerClosed = true;
            } catch (Throwable th) {
                this.writerClosed = true;
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeMessageFrameSynchronized(int i, long j, boolean z, boolean z2) throws IOException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (!this.writerClosed) {
            if (!z) {
                i = 0;
            }
            if (z2) {
                i |= 128;
            }
            this.sink.writeByte(i);
            int i2 = !this.isClient ? 0 : 128;
            if (!(j > 125)) {
                this.sink.writeByte(((int) j) | i2);
            } else {
                if (!(j > 65535)) {
                    this.sink.writeByte(i2 | Vr.VREvent.VrCore.ErrorCode.CONTROLLER_STUCK);
                    this.sink.writeShort((int) j);
                } else {
                    this.sink.writeByte(i2 | Vr.VREvent.VrCore.ErrorCode.CONTROLLER_UNSTUCK);
                    this.sink.writeLong(j);
                }
            }
            if (this.isClient) {
                this.random.nextBytes(this.maskKey);
                this.sink.write(this.maskKey);
                long j2 = 0;
                while (true) {
                    if (j2 >= j) {
                        break;
                    }
                    int read = this.buffer.read(this.maskBuffer, 0, (int) Math.min(j, (long) this.maskBuffer.length));
                    if (read != -1) {
                        WebSocketProtocol.toggleMask(this.maskBuffer, (long) read, this.maskKey, j2);
                        this.sink.write(this.maskBuffer, 0, read);
                        j2 += (long) read;
                    } else {
                        throw new AssertionError();
                    }
                }
            } else {
                this.sink.write(this.buffer, j);
            }
            this.sink.emit();
        } else {
            throw new IOException("closed");
        }
    }

    /* access modifiers changed from: package-private */
    public void writePing(ByteString byteString) throws IOException {
        synchronized (this) {
            writeControlFrameSynchronized(9, byteString);
        }
    }

    /* access modifiers changed from: package-private */
    public void writePong(ByteString byteString) throws IOException {
        synchronized (this) {
            writeControlFrameSynchronized(10, byteString);
        }
    }
}
