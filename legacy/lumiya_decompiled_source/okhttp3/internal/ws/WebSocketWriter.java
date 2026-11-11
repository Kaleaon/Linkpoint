package okhttp3.internal.ws;

import com.google.common.logging.nano.Vr;
import java.io.IOException;
import java.util.Random;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.Sink;
import okio.Timeout;
/* loaded from: classes.dex */
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

    /* loaded from: classes.dex */
    final class FrameSink implements Sink {
        boolean closed;
        long contentLength;
        int formatOpcode;
        boolean isFirstFrame;

        FrameSink() {
        }

        @Override // okio.Sink, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            synchronized (WebSocketWriter.this) {
                WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, true);
            }
            this.closed = true;
            WebSocketWriter.this.activeWriter = false;
        }

        @Override // okio.Sink, java.io.Flushable
        public void flush() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            synchronized (WebSocketWriter.this) {
                WebSocketWriter.this.writeMessageFrameSynchronized(this.formatOpcode, WebSocketWriter.this.buffer.size(), this.isFirstFrame, false);
            }
            this.isFirstFrame = false;
        }

        @Override // okio.Sink
        public Timeout timeout() {
            return WebSocketWriter.this.sink.timeout();
        }

        /* JADX WARN: Removed duplicated region for block: B:22:0x004e  */
        @Override // okio.Sink
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void write(okio.Buffer r11, long r12) throws java.io.IOException {
            /*
                r10 = this;
                r1 = 1
                r6 = 0
                boolean r0 = r10.closed
                if (r0 != 0) goto L25
                okhttp3.internal.ws.WebSocketWriter r0 = okhttp3.internal.ws.WebSocketWriter.this
                okio.Buffer r0 = r0.buffer
                r0.write(r11, r12)
                boolean r0 = r10.isFirstFrame
                if (r0 != 0) goto L2e
            L11:
                r0 = r6
            L12:
                okhttp3.internal.ws.WebSocketWriter r2 = okhttp3.internal.ws.WebSocketWriter.this
                okio.Buffer r2 = r2.buffer
                long r2 = r2.completeSegmentByteCount()
                r4 = 0
                int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r4 > 0) goto L4e
            L20:
                if (r1 != 0) goto L24
                if (r0 == 0) goto L50
            L24:
                return
            L25:
                java.io.IOException r0 = new java.io.IOException
                java.lang.String r1 = "closed"
                r0.<init>(r1)
                throw r0
            L2e:
                long r2 = r10.contentLength
                r4 = -1
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 == 0) goto L11
                okhttp3.internal.ws.WebSocketWriter r0 = okhttp3.internal.ws.WebSocketWriter.this
                okio.Buffer r0 = r0.buffer
                long r2 = r0.size()
                long r4 = r10.contentLength
                r8 = 8192(0x2000, double:4.0474E-320)
                long r4 = r4 - r8
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 > 0) goto L4c
                r0 = r1
            L48:
                if (r0 != 0) goto L11
                r0 = r1
                goto L12
            L4c:
                r0 = r6
                goto L48
            L4e:
                r1 = r6
                goto L20
            L50:
                okhttp3.internal.ws.WebSocketWriter r7 = okhttp3.internal.ws.WebSocketWriter.this
                monitor-enter(r7)
                okhttp3.internal.ws.WebSocketWriter r0 = okhttp3.internal.ws.WebSocketWriter.this     // Catch: java.lang.Throwable -> L61
                int r1 = r10.formatOpcode     // Catch: java.lang.Throwable -> L61
                boolean r4 = r10.isFirstFrame     // Catch: java.lang.Throwable -> L61
                r5 = 0
                r0.writeMessageFrameSynchronized(r1, r2, r4, r5)     // Catch: java.lang.Throwable -> L61
                monitor-exit(r7)     // Catch: java.lang.Throwable -> L61
                r10.isFirstFrame = r6
                goto L24
            L61:
                r0 = move-exception
                monitor-exit(r7)     // Catch: java.lang.Throwable -> L61
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.WebSocketWriter.FrameSink.write(okio.Buffer, long):void");
        }
    }

    static {
        $assertionsDisabled = WebSocketWriter.class.desiredAssertionStatus() ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSocketWriter(boolean z, BufferedSink bufferedSink, Random random) {
        if (bufferedSink == null) {
            throw new NullPointerException("sink == null");
        }
        if (random == null) {
            throw new NullPointerException("random == null");
        }
        this.isClient = z;
        this.sink = bufferedSink;
        this.random = random;
        this.maskKey = !z ? null : new byte[4];
        this.maskBuffer = z ? new byte[8192] : null;
    }

    private void writeControlFrameSynchronized(int i, ByteString byteString) throws IOException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        if (this.writerClosed) {
            throw new IOException("closed");
        }
        int size = byteString.size();
        if (!(((long) size) <= 125)) {
            throw new IllegalArgumentException("Payload size must be less than or equal to 125");
        }
        this.sink.writeByte(i | 128);
        if (this.isClient) {
            this.sink.writeByte(size | 128);
            this.random.nextBytes(this.maskKey);
            this.sink.write(this.maskKey);
            byte[] byteArray = byteString.toByteArray();
            WebSocketProtocol.toggleMask(byteArray, byteArray.length, this.maskKey, 0L);
            this.sink.write(byteArray);
        } else {
            this.sink.writeByte(size);
            this.sink.write(byteString);
        }
        this.sink.flush();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Sink newMessageSink(int i, long j) {
        if (this.activeWriter) {
            throw new IllegalStateException("Another message writer is active. Did you call close()?");
        }
        this.activeWriter = true;
        this.frameSink.formatOpcode = i;
        this.frameSink.contentLength = j;
        this.frameSink.isFirstFrame = true;
        this.frameSink.closed = false;
        return this.frameSink;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeClose(int i, ByteString byteString) throws IOException {
        ByteString byteString2 = ByteString.EMPTY;
        if (i != 0 || byteString != null) {
            if (i != 0) {
                WebSocketProtocol.validateCloseCode(i);
            }
            Buffer buffer = new Buffer();
            buffer.writeShort(i);
            if (byteString != null) {
                buffer.write(byteString);
            }
            byteString2 = buffer.readByteString();
        }
        synchronized (this) {
            writeControlFrameSynchronized(8, byteString2);
            this.writerClosed = true;
        }
    }

    void writeMessageFrameSynchronized(int i, long j, boolean z, boolean z2) throws IOException {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        if (this.writerClosed) {
            throw new IOException("closed");
        }
        if (!z) {
            i = 0;
        }
        if (z2) {
            i |= 128;
        }
        this.sink.writeByte(i);
        int i2 = !this.isClient ? 0 : 128;
        if (j > 125) {
            if (j > 65535) {
                this.sink.writeByte(i2 | Vr.VREvent.VrCore.ErrorCode.CONTROLLER_UNSTUCK);
                this.sink.writeLong(j);
            } else {
                this.sink.writeByte(i2 | Vr.VREvent.VrCore.ErrorCode.CONTROLLER_STUCK);
                this.sink.writeShort((int) j);
            }
        } else {
            this.sink.writeByte(((int) j) | i2);
        }
        if (this.isClient) {
            this.random.nextBytes(this.maskKey);
            this.sink.write(this.maskKey);
            long j2 = 0;
            while (true) {
                if (j2 >= j) {
                    break;
                }
                int read = this.buffer.read(this.maskBuffer, 0, (int) Math.min(j, this.maskBuffer.length));
                if (read == -1) {
                    throw new AssertionError();
                }
                WebSocketProtocol.toggleMask(this.maskBuffer, read, this.maskKey, j2);
                this.sink.write(this.maskBuffer, 0, read);
                j2 += read;
            }
        } else {
            this.sink.write(this.buffer, j);
        }
        this.sink.emit();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writePing(ByteString byteString) throws IOException {
        synchronized (this) {
            writeControlFrameSynchronized(9, byteString);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writePong(ByteString byteString) throws IOException {
        synchronized (this) {
            writeControlFrameSynchronized(10, byteString);
        }
    }
}
