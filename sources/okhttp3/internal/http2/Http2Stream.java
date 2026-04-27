package okhttp3.internal.http2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.BufferedSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public final class Http2Stream {
    static final /* synthetic */ boolean $assertionsDisabled;
    long bytesLeftInWriteWindow;
    final Http2Connection connection;
    ErrorCode errorCode = null;
    final int id;
    final StreamTimeout readTimeout = new StreamTimeout();
    private final List<Header> requestHeaders;
    private List<Header> responseHeaders;
    final FramedDataSink sink;
    private final FramedDataSource source;
    long unacknowledgedBytesRead = 0;
    final StreamTimeout writeTimeout = new StreamTimeout();

    final class FramedDataSink implements Sink {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long EMIT_BUFFER_SIZE = 16384;
        boolean closed;
        boolean finished;
        private final Buffer sendBuffer = new Buffer();

        static {
            boolean z = false;
            if (!Http2Stream.class.desiredAssertionStatus()) {
                z = true;
            }
            $assertionsDisabled = z;
        }

        FramedDataSink() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
            r8.this$0.writeTimeout.exitAndThrowIfTimedOut();
            r8.this$0.checkOutNotClosed();
            r4 = java.lang.Math.min(r8.this$0.bytesLeftInWriteWindow, r8.sendBuffer.size());
            r8.this$0.bytesLeftInWriteWindow -= r4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0084, code lost:
            if (r4 == r8.sendBuffer.size()) goto L_0x0051;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void emitDataFrame(boolean r9) throws java.io.IOException {
            /*
                r8 = this;
                r2 = 1
                r3 = 0
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r1)
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0079 }
                okhttp3.internal.http2.Http2Stream$StreamTimeout r0 = r0.writeTimeout     // Catch:{ all -> 0x0079 }
                r0.enter()     // Catch:{ all -> 0x0079 }
            L_0x000c:
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0070 }
                long r4 = r0.bytesLeftInWriteWindow     // Catch:{ all -> 0x0070 }
                r6 = 0
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 <= 0) goto L_0x005e
                r0 = r2
            L_0x0017:
                if (r0 != 0) goto L_0x001d
                boolean r0 = r8.finished     // Catch:{ all -> 0x0070 }
                if (r0 == 0) goto L_0x0060
            L_0x001d:
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0079 }
                okhttp3.internal.http2.Http2Stream$StreamTimeout r0 = r0.writeTimeout     // Catch:{ all -> 0x0079 }
                r0.exitAndThrowIfTimedOut()     // Catch:{ all -> 0x0079 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0079 }
                r0.checkOutNotClosed()     // Catch:{ all -> 0x0079 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0079 }
                long r4 = r0.bytesLeftInWriteWindow     // Catch:{ all -> 0x0079 }
                okio.Buffer r0 = r8.sendBuffer     // Catch:{ all -> 0x0079 }
                long r6 = r0.size()     // Catch:{ all -> 0x0079 }
                long r4 = java.lang.Math.min(r4, r6)     // Catch:{ all -> 0x0079 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0079 }
                long r6 = r0.bytesLeftInWriteWindow     // Catch:{ all -> 0x0079 }
                long r6 = r6 - r4
                r0.bytesLeftInWriteWindow = r6     // Catch:{ all -> 0x0079 }
                monitor-exit(r1)     // Catch:{ all -> 0x0079 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Stream$StreamTimeout r0 = r0.writeTimeout
                r0.enter()
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0087 }
                okhttp3.internal.http2.Http2Connection r0 = r0.connection     // Catch:{ all -> 0x0087 }
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0087 }
                int r1 = r1.id     // Catch:{ all -> 0x0087 }
                if (r9 != 0) goto L_0x007c
            L_0x0050:
                r2 = r3
            L_0x0051:
                okio.Buffer r3 = r8.sendBuffer     // Catch:{ all -> 0x0087 }
                r0.writeData(r1, r2, r3, r4)     // Catch:{ all -> 0x0087 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Stream$StreamTimeout r0 = r0.writeTimeout
                r0.exitAndThrowIfTimedOut()
                return
            L_0x005e:
                r0 = r3
                goto L_0x0017
            L_0x0060:
                boolean r0 = r8.closed     // Catch:{ all -> 0x0070 }
                if (r0 != 0) goto L_0x001d
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0070 }
                okhttp3.internal.http2.ErrorCode r0 = r0.errorCode     // Catch:{ all -> 0x0070 }
                if (r0 != 0) goto L_0x001d
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0070 }
                r0.waitForIo()     // Catch:{ all -> 0x0070 }
                goto L_0x000c
            L_0x0070:
                r0 = move-exception
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x0079 }
                okhttp3.internal.http2.Http2Stream$StreamTimeout r2 = r2.writeTimeout     // Catch:{ all -> 0x0079 }
                r2.exitAndThrowIfTimedOut()     // Catch:{ all -> 0x0079 }
                throw r0     // Catch:{ all -> 0x0079 }
            L_0x0079:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0079 }
                throw r0
            L_0x007c:
                okio.Buffer r6 = r8.sendBuffer     // Catch:{ all -> 0x0087 }
                long r6 = r6.size()     // Catch:{ all -> 0x0087 }
                int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r6 != 0) goto L_0x0050
                goto L_0x0051
            L_0x0087:
                r0 = move-exception
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Stream$StreamTimeout r1 = r1.writeTimeout
                r1.exitAndThrowIfTimedOut()
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramedDataSink.emitDataFrame(boolean):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0018, code lost:
            r1 = r8.this$0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
            monitor-enter(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
            r8.closed = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x001e, code lost:
            monitor-exit(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x001f, code lost:
            r8.this$0.connection.flush();
            r8.this$0.cancelStreamIfNecessary();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x002b, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0047, code lost:
            if (r8.sendBuffer.size() > 0) goto L_0x005d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0049, code lost:
            r0 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x004a, code lost:
            if (r0 != false) goto L_0x0061;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0054, code lost:
            if (r8.sendBuffer.size() > 0) goto L_0x005f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0056, code lost:
            r0 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0057, code lost:
            if (r0 != false) goto L_0x0018;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x0059, code lost:
            emitDataFrame(true);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x005d, code lost:
            r0 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x005f, code lost:
            r0 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0061, code lost:
            r8.this$0.connection.writeData(r8.this$0.id, true, (okio.Buffer) null, 0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
            if (r8.this$0.sink.finished == false) goto L_0x003f;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void close() throws java.io.IOException {
            /*
                r8 = this;
                r4 = 0
                r2 = 1
                r1 = 0
                boolean r0 = $assertionsDisabled
                if (r0 == 0) goto L_0x002c
            L_0x0008:
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r3)
                boolean r0 = r8.closed     // Catch:{ all -> 0x003c }
                if (r0 != 0) goto L_0x003a
                monitor-exit(r3)     // Catch:{ all -> 0x003c }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Stream$FramedDataSink r0 = r0.sink
                boolean r0 = r0.finished
                if (r0 == 0) goto L_0x003f
            L_0x0018:
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r1)
                r0 = 1
                r8.closed = r0     // Catch:{ all -> 0x006e }
                monitor-exit(r1)     // Catch:{ all -> 0x006e }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r0 = r0.connection
                r0.flush()
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                r0.cancelStreamIfNecessary()
                return
            L_0x002c:
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                boolean r0 = java.lang.Thread.holdsLock(r0)
                if (r0 == 0) goto L_0x0008
                java.lang.AssertionError r0 = new java.lang.AssertionError
                r0.<init>()
                throw r0
            L_0x003a:
                monitor-exit(r3)     // Catch:{ all -> 0x003c }
                return
            L_0x003c:
                r0 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x003c }
                throw r0
            L_0x003f:
                okio.Buffer r0 = r8.sendBuffer
                long r6 = r0.size()
                int r0 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
                if (r0 > 0) goto L_0x005d
                r0 = r2
            L_0x004a:
                if (r0 != 0) goto L_0x0061
            L_0x004c:
                okio.Buffer r0 = r8.sendBuffer
                long r6 = r0.size()
                int r0 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
                if (r0 > 0) goto L_0x005f
                r0 = r2
            L_0x0057:
                if (r0 != 0) goto L_0x0018
                r8.emitDataFrame(r2)
                goto L_0x004c
            L_0x005d:
                r0 = r1
                goto L_0x004a
            L_0x005f:
                r0 = r1
                goto L_0x0057
            L_0x0061:
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r0 = r0.connection
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this
                int r1 = r1.id
                r3 = 0
                r0.writeData(r1, r2, r3, r4)
                goto L_0x0018
            L_0x006e:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x006e }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramedDataSink.close():void");
        }

        public void flush() throws IOException {
            if (!$assertionsDisabled && Thread.holdsLock(Http2Stream.this)) {
                throw new AssertionError();
            }
            synchronized (Http2Stream.this) {
                Http2Stream.this.checkOutNotClosed();
            }
            while (true) {
                if (!(this.sendBuffer.size() <= 0)) {
                    emitDataFrame(false);
                    Http2Stream.this.connection.flush();
                } else {
                    return;
                }
            }
        }

        public Timeout timeout() {
            return Http2Stream.this.writeTimeout;
        }

        public void write(Buffer buffer, long j) throws IOException {
            if (!$assertionsDisabled && Thread.holdsLock(Http2Stream.this)) {
                throw new AssertionError();
            }
            this.sendBuffer.write(buffer, j);
            while (true) {
                if (!(this.sendBuffer.size() < 16384)) {
                    emitDataFrame(false);
                } else {
                    return;
                }
            }
        }
    }

    private final class FramedDataSource implements Source {
        static final /* synthetic */ boolean $assertionsDisabled;
        boolean closed;
        boolean finished;
        private final long maxByteCount;
        private final Buffer readBuffer = new Buffer();
        private final Buffer receiveBuffer = new Buffer();

        static {
            boolean z = false;
            if (!Http2Stream.class.desiredAssertionStatus()) {
                z = true;
            }
            $assertionsDisabled = z;
        }

        FramedDataSource(long j) {
            this.maxByteCount = j;
        }

        private void checkNotClosed() throws IOException {
            if (this.closed) {
                throw new IOException("stream closed");
            } else if (Http2Stream.this.errorCode != null) {
                throw new StreamResetException(Http2Stream.this.errorCode);
            }
        }

        private void waitUntilReadable() throws IOException {
            Http2Stream.this.readTimeout.enter();
            while (this.readBuffer.size() == 0 && !this.finished) {
                try {
                    if (this.closed || Http2Stream.this.errorCode != null) {
                        break;
                    }
                    Http2Stream.this.waitForIo();
                } finally {
                    Http2Stream.this.readTimeout.exitAndThrowIfTimedOut();
                }
            }
        }

        public void close() throws IOException {
            synchronized (Http2Stream.this) {
                this.closed = true;
                this.readBuffer.clear();
                Http2Stream.this.notifyAll();
            }
            Http2Stream.this.cancelStreamIfNecessary();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0083, code lost:
            r2 = r10.this$0.connection;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0087, code lost:
            monitor-enter(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
            r10.this$0.connection.unacknowledgedBytesRead += r4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x00a6, code lost:
            if (r10.this$0.connection.unacknowledgedBytesRead >= ((long) (r10.this$0.connection.okHttpSettings.getInitialWindowSize() / 2))) goto L_0x00c7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x00a8, code lost:
            if (r0 != false) goto L_0x00c0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00aa, code lost:
            r10.this$0.connection.writeWindowUpdateLater(0, r10.this$0.connection.unacknowledgedBytesRead);
            r10.this$0.connection.unacknowledgedBytesRead = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x00c0, code lost:
            monitor-exit(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00c1, code lost:
            return r4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c7, code lost:
            r0 = false;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public long read(okio.Buffer r11, long r12) throws java.io.IOException {
            /*
                r10 = this;
                r0 = 1
                r6 = 0
                r1 = 0
                int r2 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
                if (r2 < 0) goto L_0x0025
                r2 = r0
            L_0x0009:
                if (r2 != 0) goto L_0x0027
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "byteCount < 0: "
                java.lang.StringBuilder r1 = r1.append(r2)
                java.lang.StringBuilder r1 = r1.append(r12)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            L_0x0025:
                r2 = r1
                goto L_0x0009
            L_0x0027:
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this
                monitor-enter(r3)
                r10.waitUntilReadable()     // Catch:{ all -> 0x00c4 }
                r10.checkNotClosed()     // Catch:{ all -> 0x00c4 }
                okio.Buffer r2 = r10.readBuffer     // Catch:{ all -> 0x00c4 }
                long r4 = r2.size()     // Catch:{ all -> 0x00c4 }
                int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r2 != 0) goto L_0x003e
                monitor-exit(r3)     // Catch:{ all -> 0x00c4 }
                r0 = -1
                return r0
            L_0x003e:
                okio.Buffer r2 = r10.readBuffer     // Catch:{ all -> 0x00c4 }
                okio.Buffer r4 = r10.readBuffer     // Catch:{ all -> 0x00c4 }
                long r4 = r4.size()     // Catch:{ all -> 0x00c4 }
                long r4 = java.lang.Math.min(r12, r4)     // Catch:{ all -> 0x00c4 }
                long r4 = r2.read(r11, r4)     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c4 }
                long r6 = r2.unacknowledgedBytesRead     // Catch:{ all -> 0x00c4 }
                long r6 = r6 + r4
                r2.unacknowledgedBytesRead = r6     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c4 }
                long r6 = r2.unacknowledgedBytesRead     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Connection r2 = r2.connection     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Settings r2 = r2.okHttpSettings     // Catch:{ all -> 0x00c4 }
                int r2 = r2.getInitialWindowSize()     // Catch:{ all -> 0x00c4 }
                int r2 = r2 / 2
                long r8 = (long) r2     // Catch:{ all -> 0x00c4 }
                int r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r2 >= 0) goto L_0x00c2
                r2 = r0
            L_0x006b:
                if (r2 != 0) goto L_0x0082
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Connection r2 = r2.connection     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Stream r6 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c4 }
                int r6 = r6.id     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Stream r7 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c4 }
                long r8 = r7.unacknowledgedBytesRead     // Catch:{ all -> 0x00c4 }
                r2.writeWindowUpdateLater(r6, r8)     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c4 }
                r6 = 0
                r2.unacknowledgedBytesRead = r6     // Catch:{ all -> 0x00c4 }
            L_0x0082:
                monitor-exit(r3)     // Catch:{ all -> 0x00c4 }
                okhttp3.internal.http2.Http2Stream r2 = okhttp3.internal.http2.Http2Stream.this
                okhttp3.internal.http2.Http2Connection r2 = r2.connection
                monitor-enter(r2)
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Connection r3 = r3.connection     // Catch:{ all -> 0x00c9 }
                long r6 = r3.unacknowledgedBytesRead     // Catch:{ all -> 0x00c9 }
                long r6 = r6 + r4
                r3.unacknowledgedBytesRead = r6     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Connection r3 = r3.connection     // Catch:{ all -> 0x00c9 }
                long r6 = r3.unacknowledgedBytesRead     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Stream r3 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Connection r3 = r3.connection     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Settings r3 = r3.okHttpSettings     // Catch:{ all -> 0x00c9 }
                int r3 = r3.getInitialWindowSize()     // Catch:{ all -> 0x00c9 }
                int r3 = r3 / 2
                long r8 = (long) r3     // Catch:{ all -> 0x00c9 }
                int r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r3 >= 0) goto L_0x00c7
            L_0x00a8:
                if (r0 != 0) goto L_0x00c0
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Connection r0 = r0.connection     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Stream r1 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Connection r1 = r1.connection     // Catch:{ all -> 0x00c9 }
                long r6 = r1.unacknowledgedBytesRead     // Catch:{ all -> 0x00c9 }
                r1 = 0
                r0.writeWindowUpdateLater(r1, r6)     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Stream r0 = okhttp3.internal.http2.Http2Stream.this     // Catch:{ all -> 0x00c9 }
                okhttp3.internal.http2.Http2Connection r0 = r0.connection     // Catch:{ all -> 0x00c9 }
                r6 = 0
                r0.unacknowledgedBytesRead = r6     // Catch:{ all -> 0x00c9 }
            L_0x00c0:
                monitor-exit(r2)     // Catch:{ all -> 0x00c9 }
                return r4
            L_0x00c2:
                r2 = r1
                goto L_0x006b
            L_0x00c4:
                r0 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x00c4 }
                throw r0
            L_0x00c7:
                r0 = r1
                goto L_0x00a8
            L_0x00c9:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x00c9 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Stream.FramedDataSource.read(okio.Buffer, long):long");
        }

        /* access modifiers changed from: package-private */
        public void receive(BufferedSource bufferedSource, long j) throws IOException {
            boolean z;
            boolean z2;
            if (!$assertionsDisabled && Thread.holdsLock(Http2Stream.this)) {
                throw new AssertionError();
            }
            while (true) {
                if (!(j <= 0)) {
                    synchronized (Http2Stream.this) {
                        z = this.finished;
                        z2 = !(((this.readBuffer.size() + j) > this.maxByteCount ? 1 : ((this.readBuffer.size() + j) == this.maxByteCount ? 0 : -1)) <= 0);
                    }
                    if (z2) {
                        bufferedSource.skip(j);
                        Http2Stream.this.closeLater(ErrorCode.FLOW_CONTROL_ERROR);
                        return;
                    } else if (!z) {
                        long read = bufferedSource.read(this.receiveBuffer, j);
                        if (read == -1) {
                            throw new EOFException();
                        }
                        j -= read;
                        synchronized (Http2Stream.this) {
                            boolean z3 = this.readBuffer.size() == 0;
                            this.readBuffer.writeAll(this.receiveBuffer);
                            if (z3) {
                                Http2Stream.this.notifyAll();
                            }
                        }
                    } else {
                        bufferedSource.skip(j);
                        return;
                    }
                } else {
                    return;
                }
            }
            while (true) {
            }
        }

        public Timeout timeout() {
            return Http2Stream.this.readTimeout;
        }
    }

    class StreamTimeout extends AsyncTimeout {
        StreamTimeout() {
        }

        public void exitAndThrowIfTimedOut() throws IOException {
            if (exit()) {
                throw newTimeoutException((IOException) null);
            }
        }

        /* access modifiers changed from: protected */
        public IOException newTimeoutException(IOException iOException) {
            SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");
            if (iOException != null) {
                socketTimeoutException.initCause(iOException);
            }
            return socketTimeoutException;
        }

        /* access modifiers changed from: protected */
        public void timedOut() {
            Http2Stream.this.closeLater(ErrorCode.CANCEL);
        }
    }

    static {
        boolean z = false;
        if (!Http2Stream.class.desiredAssertionStatus()) {
            z = true;
        }
        $assertionsDisabled = z;
    }

    Http2Stream(int i, Http2Connection http2Connection, boolean z, boolean z2, List<Header> list) {
        if (http2Connection == null) {
            throw new NullPointerException("connection == null");
        } else if (list != null) {
            this.id = i;
            this.connection = http2Connection;
            this.bytesLeftInWriteWindow = (long) http2Connection.peerSettings.getInitialWindowSize();
            this.source = new FramedDataSource((long) http2Connection.okHttpSettings.getInitialWindowSize());
            this.sink = new FramedDataSink();
            this.source.finished = z2;
            this.sink.finished = z;
            this.requestHeaders = list;
        } else {
            throw new NullPointerException("requestHeaders == null");
        }
    }

    private boolean closeInternal(ErrorCode errorCode2) {
        if (!$assertionsDisabled && Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        synchronized (this) {
            if (this.errorCode != null) {
                return false;
            }
            if (this.source.finished && this.sink.finished) {
                return false;
            }
            this.errorCode = errorCode2;
            notifyAll();
            this.connection.removeStream(this.id);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public void addBytesToWriteWindow(long j) {
        this.bytesLeftInWriteWindow += j;
        if (!(j <= 0)) {
            notifyAll();
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelStreamIfNecessary() throws IOException {
        boolean isOpen;
        boolean z = false;
        if (!$assertionsDisabled && Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        synchronized (this) {
            if (!this.source.finished) {
                if (this.source.closed) {
                    if (this.sink.finished || this.sink.closed) {
                        z = true;
                    }
                }
            }
            isOpen = isOpen();
        }
        if (z) {
            close(ErrorCode.CANCEL);
        } else if (!isOpen) {
            this.connection.removeStream(this.id);
        }
    }

    /* access modifiers changed from: package-private */
    public void checkOutNotClosed() throws IOException {
        if (this.sink.closed) {
            throw new IOException("stream closed");
        } else if (this.sink.finished) {
            throw new IOException("stream finished");
        } else if (this.errorCode != null) {
            throw new StreamResetException(this.errorCode);
        }
    }

    public void close(ErrorCode errorCode2) throws IOException {
        if (closeInternal(errorCode2)) {
            this.connection.writeSynReset(this.id, errorCode2);
        }
    }

    public void closeLater(ErrorCode errorCode2) {
        if (closeInternal(errorCode2)) {
            this.connection.writeSynResetLater(this.id, errorCode2);
        }
    }

    public Http2Connection getConnection() {
        return this.connection;
    }

    public synchronized ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public int getId() {
        return this.id;
    }

    public List<Header> getRequestHeaders() {
        return this.requestHeaders;
    }

    /* JADX INFO: finally extract failed */
    public synchronized List<Header> getResponseHeaders() throws IOException {
        this.readTimeout.enter();
        while (this.responseHeaders == null) {
            try {
                if (this.errorCode == null) {
                    waitForIo();
                }
            } catch (Throwable th) {
                this.readTimeout.exitAndThrowIfTimedOut();
                throw th;
            }
        }
        this.readTimeout.exitAndThrowIfTimedOut();
        if (this.responseHeaders == null) {
            throw new StreamResetException(this.errorCode);
        }
        return this.responseHeaders;
    }

    public Sink getSink() {
        synchronized (this) {
            if (this.responseHeaders == null) {
                if (!isLocallyInitiated()) {
                    throw new IllegalStateException("reply before requesting the sink");
                }
            }
        }
        return this.sink;
    }

    public Source getSource() {
        return this.source;
    }

    public boolean isLocallyInitiated() {
        return this.connection.client == ((this.id & 1) == 1);
    }

    public synchronized boolean isOpen() {
        if (this.errorCode != null) {
            return false;
        }
        if (!this.source.finished) {
            if (!this.source.closed) {
                return true;
            }
        }
        if ((this.sink.finished || this.sink.closed) && this.responseHeaders != null) {
            return false;
        }
        return true;
    }

    public Timeout readTimeout() {
        return this.readTimeout;
    }

    /* access modifiers changed from: package-private */
    public void receiveData(BufferedSource bufferedSource, int i) throws IOException {
        if (!$assertionsDisabled && Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        this.source.receive(bufferedSource, (long) i);
    }

    /* access modifiers changed from: package-private */
    public void receiveFin() {
        boolean isOpen;
        if (!$assertionsDisabled && Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        synchronized (this) {
            this.source.finished = true;
            isOpen = isOpen();
            notifyAll();
        }
        if (!isOpen) {
            this.connection.removeStream(this.id);
        }
    }

    /* access modifiers changed from: package-private */
    public void receiveHeaders(List<Header> list) {
        if (!$assertionsDisabled && Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        boolean z = true;
        synchronized (this) {
            if (this.responseHeaders != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.responseHeaders);
                arrayList.addAll(list);
                this.responseHeaders = arrayList;
            } else {
                this.responseHeaders = list;
                z = isOpen();
                notifyAll();
            }
        }
        if (!z) {
            this.connection.removeStream(this.id);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void receiveRstStream(ErrorCode errorCode2) {
        if (this.errorCode == null) {
            this.errorCode = errorCode2;
            notifyAll();
        }
    }

    public void reply(List<Header> list, boolean z) throws IOException {
        boolean z2 = false;
        if (!$assertionsDisabled && Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        synchronized (this) {
            if (list == null) {
                throw new NullPointerException("responseHeaders == null");
            } else if (this.responseHeaders == null) {
                this.responseHeaders = list;
                if (!z) {
                    this.sink.finished = true;
                    z2 = true;
                }
            } else {
                throw new IllegalStateException("reply already sent");
            }
        }
        this.connection.writeSynReply(this.id, z2, list);
        if (z2) {
            this.connection.flush();
        }
    }

    /* access modifiers changed from: package-private */
    public void waitForIo() throws InterruptedIOException {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
    }

    public Timeout writeTimeout() {
        return this.writeTimeout;
    }
}
