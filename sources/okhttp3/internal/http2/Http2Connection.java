package okhttp3.internal.http2;

import android.support.v4.internal.view.SupportMenu;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Protocol;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.Util;
import okhttp3.internal.http2.Http2Reader;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public final class Http2Connection implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled = (!Http2Connection.class.desiredAssertionStatus());
    private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
    static final ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp FramedConnection", true));
    long bytesLeftInWriteWindow;
    final boolean client;
    final Set<Integer> currentPushRequests = new LinkedHashSet();
    final String hostname;
    int lastGoodStreamId;
    final Listener listener;
    private int nextPingId;
    int nextStreamId;
    Settings okHttpSettings = new Settings();
    final Settings peerSettings = new Settings();
    private Map<Integer, Ping> pings;
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    final ReaderRunnable readerRunnable;
    boolean receivedInitialPeerSettings = false;
    boolean shutdown;
    final Socket socket;
    final Map<Integer, Http2Stream> streams = new LinkedHashMap();
    long unacknowledgedBytesRead = 0;
    final Http2Writer writer;

    public static class Builder {
        boolean client;
        String hostname;
        Listener listener = Listener.REFUSE_INCOMING_STREAMS;
        PushObserver pushObserver = PushObserver.CANCEL;
        BufferedSink sink;
        Socket socket;
        BufferedSource source;

        public Builder(boolean z) {
            this.client = z;
        }

        public Http2Connection build() throws IOException {
            return new Http2Connection(this);
        }

        public Builder listener(Listener listener2) {
            this.listener = listener2;
            return this;
        }

        public Builder pushObserver(PushObserver pushObserver2) {
            this.pushObserver = pushObserver2;
            return this;
        }

        public Builder socket(Socket socket2) throws IOException {
            return socket(socket2, ((InetSocketAddress) socket2.getRemoteSocketAddress()).getHostName(), Okio.buffer(Okio.source(socket2)), Okio.buffer(Okio.sink(socket2)));
        }

        public Builder socket(Socket socket2, String str, BufferedSource bufferedSource, BufferedSink bufferedSink) {
            this.socket = socket2;
            this.hostname = str;
            this.source = bufferedSource;
            this.sink = bufferedSink;
            return this;
        }
    }

    public static abstract class Listener {
        public static final Listener REFUSE_INCOMING_STREAMS = new Listener() {
            public void onStream(Http2Stream http2Stream) throws IOException {
                http2Stream.close(ErrorCode.REFUSED_STREAM);
            }
        };

        public void onSettings(Http2Connection http2Connection) {
        }

        public abstract void onStream(Http2Stream http2Stream) throws IOException;
    }

    class ReaderRunnable extends NamedRunnable implements Http2Reader.Handler {
        final Http2Reader reader;

        ReaderRunnable(Http2Reader http2Reader) {
            super("OkHttp %s", Http2Connection.this.hostname);
            this.reader = http2Reader;
        }

        private void applyAndAckSettings(final Settings settings) {
            Http2Connection.executor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[]{Http2Connection.this.hostname}) {
                public void execute() {
                    try {
                        Http2Connection.this.writer.applyAndAckSettings(settings);
                    } catch (IOException e) {
                    }
                }
            });
        }

        public void ackSettings() {
        }

        public void alternateService(int i, String str, ByteString byteString, String str2, int i2, long j) {
        }

        public void data(boolean z, int i, BufferedSource bufferedSource, int i2) throws IOException {
            if (!Http2Connection.this.pushedStream(i)) {
                Http2Stream stream = Http2Connection.this.getStream(i);
                if (stream != null) {
                    stream.receiveData(bufferedSource, i2);
                    if (z) {
                        stream.receiveFin();
                        return;
                    }
                    return;
                }
                Http2Connection.this.writeSynResetLater(i, ErrorCode.PROTOCOL_ERROR);
                bufferedSource.skip((long) i2);
                return;
            }
            Http2Connection.this.pushDataLater(i, bufferedSource, i2, z);
        }

        /* access modifiers changed from: protected */
        public void execute() {
            ErrorCode errorCode;
            Throwable th;
            ErrorCode errorCode2 = ErrorCode.INTERNAL_ERROR;
            ErrorCode errorCode3 = ErrorCode.INTERNAL_ERROR;
            try {
                if (!Http2Connection.this.client) {
                    this.reader.readConnectionPreface();
                }
                do {
                } while (this.reader.nextFrame(this));
                try {
                    Http2Connection.this.close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
                } catch (IOException e) {
                }
                Util.closeQuietly((Closeable) this.reader);
            } catch (IOException e2) {
                errorCode = ErrorCode.PROTOCOL_ERROR;
                try {
                    Http2Connection.this.close(errorCode, ErrorCode.PROTOCOL_ERROR);
                } catch (IOException e3) {
                }
                Util.closeQuietly((Closeable) this.reader);
            } catch (Throwable th2) {
                th = th2;
                Http2Connection.this.close(errorCode, errorCode3);
                Util.closeQuietly((Closeable) this.reader);
                throw th;
            }
        }

        public void goAway(int i, ErrorCode errorCode, ByteString byteString) {
            Http2Stream[] http2StreamArr;
            int size = byteString.size();
            synchronized (Http2Connection.this) {
                http2StreamArr = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                Http2Connection.this.shutdown = true;
            }
            for (Http2Stream http2Stream : http2StreamArr) {
                if (http2Stream.getId() > i && http2Stream.isLocallyInitiated()) {
                    http2Stream.receiveRstStream(ErrorCode.REFUSED_STREAM);
                    Http2Connection.this.removeStream(http2Stream.getId());
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
            r0.receiveHeaders(r11);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
            if (r8 != false) goto L_0x0079;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0079, code lost:
            r0.receiveFin();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void headers(boolean r8, int r9, int r10, java.util.List<okhttp3.internal.http2.Header> r11) {
            /*
                r7 = this;
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this
                boolean r0 = r0.pushedStream(r9)
                if (r0 != 0) goto L_0x0020
                okhttp3.internal.http2.Http2Connection r6 = okhttp3.internal.http2.Http2Connection.this
                monitor-enter(r6)
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                boolean r0 = r0.shutdown     // Catch:{ all -> 0x0076 }
                if (r0 != 0) goto L_0x0026
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                okhttp3.internal.http2.Http2Stream r0 = r0.getStream(r9)     // Catch:{ all -> 0x0076 }
                if (r0 == 0) goto L_0x0028
                monitor-exit(r6)     // Catch:{ all -> 0x0076 }
                r0.receiveHeaders(r11)
                if (r8 != 0) goto L_0x0079
            L_0x001f:
                return
            L_0x0020:
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this
                r0.pushHeadersLater(r9, r11, r8)
                return
            L_0x0026:
                monitor-exit(r6)     // Catch:{ all -> 0x0076 }
                return
            L_0x0028:
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                int r0 = r0.lastGoodStreamId     // Catch:{ all -> 0x0076 }
                if (r9 <= r0) goto L_0x0072
                int r0 = r9 % 2
                okhttp3.internal.http2.Http2Connection r1 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                int r1 = r1.nextStreamId     // Catch:{ all -> 0x0076 }
                int r1 = r1 % 2
                if (r0 == r1) goto L_0x0074
                okhttp3.internal.http2.Http2Stream r0 = new okhttp3.internal.http2.Http2Stream     // Catch:{ all -> 0x0076 }
                okhttp3.internal.http2.Http2Connection r2 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                r3 = 0
                r1 = r9
                r4 = r8
                r5 = r11
                r0.<init>(r1, r2, r3, r4, r5)     // Catch:{ all -> 0x0076 }
                okhttp3.internal.http2.Http2Connection r1 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                r1.lastGoodStreamId = r9     // Catch:{ all -> 0x0076 }
                okhttp3.internal.http2.Http2Connection r1 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r1 = r1.streams     // Catch:{ all -> 0x0076 }
                java.lang.Integer r2 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0076 }
                r1.put(r2, r0)     // Catch:{ all -> 0x0076 }
                java.util.concurrent.ExecutorService r1 = okhttp3.internal.http2.Http2Connection.executor     // Catch:{ all -> 0x0076 }
                okhttp3.internal.http2.Http2Connection$ReaderRunnable$1 r2 = new okhttp3.internal.http2.Http2Connection$ReaderRunnable$1     // Catch:{ all -> 0x0076 }
                r3 = 2
                java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0076 }
                okhttp3.internal.http2.Http2Connection r4 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x0076 }
                java.lang.String r4 = r4.hostname     // Catch:{ all -> 0x0076 }
                r5 = 0
                r3[r5] = r4     // Catch:{ all -> 0x0076 }
                java.lang.Integer r4 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0076 }
                r5 = 1
                r3[r5] = r4     // Catch:{ all -> 0x0076 }
                java.lang.String r4 = "OkHttp %s stream %d"
                r2.<init>(r4, r3, r0)     // Catch:{ all -> 0x0076 }
                r1.execute(r2)     // Catch:{ all -> 0x0076 }
                monitor-exit(r6)     // Catch:{ all -> 0x0076 }
                return
            L_0x0072:
                monitor-exit(r6)     // Catch:{ all -> 0x0076 }
                return
            L_0x0074:
                monitor-exit(r6)     // Catch:{ all -> 0x0076 }
                return
            L_0x0076:
                r0 = move-exception
                monitor-exit(r6)     // Catch:{ all -> 0x0076 }
                throw r0
            L_0x0079:
                r0.receiveFin()
                goto L_0x001f
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.headers(boolean, int, int, java.util.List):void");
        }

        public void ping(boolean z, int i, int i2) {
            if (!z) {
                Http2Connection.this.writePingLater(true, i, i2, (Ping) null);
                return;
            }
            Ping removePing = Http2Connection.this.removePing(i);
            if (removePing != null) {
                removePing.receive();
            }
        }

        public void priority(int i, int i2, int i3, boolean z) {
        }

        public void pushPromise(int i, int i2, List<Header> list) {
            Http2Connection.this.pushRequestLater(i2, list);
        }

        public void rstStream(int i, ErrorCode errorCode) {
            if (!Http2Connection.this.pushedStream(i)) {
                Http2Stream removeStream = Http2Connection.this.removeStream(i);
                if (removeStream != null) {
                    removeStream.receiveRstStream(errorCode);
                    return;
                }
                return;
            }
            Http2Connection.this.pushResetLater(i, errorCode);
        }

        public void settings(boolean z, Settings settings) {
            long j;
            Http2Stream[] http2StreamArr;
            synchronized (Http2Connection.this) {
                int initialWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                if (z) {
                    Http2Connection.this.peerSettings.clear();
                }
                Http2Connection.this.peerSettings.merge(settings);
                applyAndAckSettings(settings);
                int initialWindowSize2 = Http2Connection.this.peerSettings.getInitialWindowSize();
                if (initialWindowSize2 == -1 || initialWindowSize2 == initialWindowSize) {
                    http2StreamArr = null;
                    j = 0;
                } else {
                    long j2 = (long) (initialWindowSize2 - initialWindowSize);
                    if (!Http2Connection.this.receivedInitialPeerSettings) {
                        Http2Connection.this.addBytesToWriteWindow(j2);
                        Http2Connection.this.receivedInitialPeerSettings = true;
                    }
                    if (Http2Connection.this.streams.isEmpty()) {
                        j = j2;
                        http2StreamArr = null;
                    } else {
                        j = j2;
                        http2StreamArr = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                    }
                }
                Http2Connection.executor.execute(new NamedRunnable("OkHttp %s settings", Http2Connection.this.hostname) {
                    public void execute() {
                        Http2Connection.this.listener.onSettings(Http2Connection.this);
                    }
                });
            }
            if (http2StreamArr != null && j != 0) {
                for (Http2Stream http2Stream : http2StreamArr) {
                    synchronized (http2Stream) {
                        http2Stream.addBytesToWriteWindow(j);
                    }
                }
            }
        }

        public void windowUpdate(int i, long j) {
            if (i != 0) {
                Http2Stream stream = Http2Connection.this.getStream(i);
                if (stream != null) {
                    synchronized (stream) {
                        stream.addBytesToWriteWindow(j);
                    }
                    return;
                }
                return;
            }
            synchronized (Http2Connection.this) {
                Http2Connection.this.bytesLeftInWriteWindow += j;
                Http2Connection.this.notifyAll();
            }
        }
    }

    Http2Connection(Builder builder) {
        int i = 2;
        this.pushObserver = builder.pushObserver;
        this.client = builder.client;
        this.listener = builder.listener;
        this.nextStreamId = !builder.client ? 2 : 1;
        if (builder.client) {
            this.nextStreamId += 2;
        }
        this.nextPingId = builder.client ? 1 : i;
        if (builder.client) {
            this.okHttpSettings.set(7, 16777216);
        }
        this.hostname = builder.hostname;
        this.pushExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory(Util.format("OkHttp %s Push Observer", this.hostname), true));
        this.peerSettings.set(7, SupportMenu.USER_MASK);
        this.peerSettings.set(5, 16384);
        this.bytesLeftInWriteWindow = (long) this.peerSettings.getInitialWindowSize();
        this.socket = builder.socket;
        this.writer = new Http2Writer(builder.sink, this.client);
        this.readerRunnable = new ReaderRunnable(new Http2Reader(builder.source, this.client));
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x0053  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.http2.Http2Stream newStream(int r13, java.util.List<okhttp3.internal.http2.Header> r14, boolean r15) throws java.io.IOException {
        /*
            r12 = this;
            r10 = 0
            r7 = 1
            r6 = 0
            if (r15 == 0) goto L_0x0038
            r3 = r6
        L_0x0007:
            okhttp3.internal.http2.Http2Writer r8 = r12.writer
            monitor-enter(r8)
            monitor-enter(r12)     // Catch:{ all -> 0x0043 }
            boolean r0 = r12.shutdown     // Catch:{ all -> 0x0040 }
            if (r0 != 0) goto L_0x003a
            int r1 = r12.nextStreamId     // Catch:{ all -> 0x0040 }
            int r0 = r12.nextStreamId     // Catch:{ all -> 0x0040 }
            int r0 = r0 + 2
            r12.nextStreamId = r0     // Catch:{ all -> 0x0040 }
            okhttp3.internal.http2.Http2Stream r0 = new okhttp3.internal.http2.Http2Stream     // Catch:{ all -> 0x0040 }
            r4 = 0
            r2 = r12
            r5 = r14
            r0.<init>(r1, r2, r3, r4, r5)     // Catch:{ all -> 0x0040 }
            if (r15 != 0) goto L_0x0046
        L_0x0021:
            r6 = r7
        L_0x0022:
            boolean r2 = r0.isOpen()     // Catch:{ all -> 0x0040 }
            if (r2 != 0) goto L_0x0053
        L_0x0028:
            monitor-exit(r12)     // Catch:{ all -> 0x0040 }
            if (r13 == 0) goto L_0x005d
            boolean r2 = r12.client     // Catch:{ all -> 0x0043 }
            if (r2 != 0) goto L_0x0063
            okhttp3.internal.http2.Http2Writer r2 = r12.writer     // Catch:{ all -> 0x0043 }
            r2.pushPromise(r13, r1, r14)     // Catch:{ all -> 0x0043 }
        L_0x0034:
            monitor-exit(r8)     // Catch:{ all -> 0x0043 }
            if (r6 != 0) goto L_0x006c
        L_0x0037:
            return r0
        L_0x0038:
            r3 = r7
            goto L_0x0007
        L_0x003a:
            okhttp3.internal.http2.ConnectionShutdownException r0 = new okhttp3.internal.http2.ConnectionShutdownException     // Catch:{ all -> 0x0040 }
            r0.<init>()     // Catch:{ all -> 0x0040 }
            throw r0     // Catch:{ all -> 0x0040 }
        L_0x0040:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x0040 }
            throw r0     // Catch:{ all -> 0x0043 }
        L_0x0043:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0043 }
            throw r0
        L_0x0046:
            long r4 = r12.bytesLeftInWriteWindow     // Catch:{ all -> 0x0040 }
            int r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r2 == 0) goto L_0x0021
            long r4 = r0.bytesLeftInWriteWindow     // Catch:{ all -> 0x0040 }
            int r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r2 != 0) goto L_0x0022
            goto L_0x0021
        L_0x0053:
            java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r2 = r12.streams     // Catch:{ all -> 0x0040 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x0040 }
            r2.put(r4, r0)     // Catch:{ all -> 0x0040 }
            goto L_0x0028
        L_0x005d:
            okhttp3.internal.http2.Http2Writer r2 = r12.writer     // Catch:{ all -> 0x0043 }
            r2.synStream(r3, r1, r13, r14)     // Catch:{ all -> 0x0043 }
            goto L_0x0034
        L_0x0063:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0043 }
            java.lang.String r1 = "client streams shouldn't have associated stream IDs"
            r0.<init>(r1)     // Catch:{ all -> 0x0043 }
            throw r0     // Catch:{ all -> 0x0043 }
        L_0x006c:
            okhttp3.internal.http2.Http2Writer r1 = r12.writer
            r1.flush()
            goto L_0x0037
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.newStream(int, java.util.List, boolean):okhttp3.internal.http2.Http2Stream");
    }

    /* access modifiers changed from: package-private */
    public void addBytesToWriteWindow(long j) {
        this.bytesLeftInWriteWindow += j;
        if (!(j <= 0)) {
            notifyAll();
        }
    }

    public void close() throws IOException {
        close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }

    /* access modifiers changed from: package-private */
    public void close(ErrorCode errorCode, ErrorCode errorCode2) throws IOException {
        IOException iOException;
        Http2Stream[] http2StreamArr;
        Ping[] pingArr;
        if (!$assertionsDisabled && Thread.holdsLock(this)) {
            throw new AssertionError();
        }
        try {
            shutdown(errorCode);
            iOException = null;
        } catch (IOException e) {
            iOException = e;
        }
        synchronized (this) {
            if (this.streams.isEmpty()) {
                http2StreamArr = null;
            } else {
                this.streams.clear();
                http2StreamArr = (Http2Stream[]) this.streams.values().toArray(new Http2Stream[this.streams.size()]);
            }
            if (this.pings == null) {
                pingArr = null;
            } else {
                this.pings = null;
                pingArr = (Ping[]) this.pings.values().toArray(new Ping[this.pings.size()]);
            }
        }
        if (http2StreamArr != null) {
            IOException iOException2 = iOException;
            for (Http2Stream close : http2StreamArr) {
                try {
                    close.close(errorCode2);
                } catch (IOException e2) {
                    if (iOException2 != null) {
                        iOException2 = e2;
                    }
                }
            }
            iOException = iOException2;
        }
        if (pingArr != null) {
            for (Ping cancel : pingArr) {
                cancel.cancel();
            }
        }
        try {
            this.writer.close();
            e = iOException;
        } catch (IOException e3) {
            e = e3;
            if (iOException != null) {
                e = iOException;
            }
        }
        try {
            this.socket.close();
        } catch (IOException e4) {
            e = e4;
        }
        if (e != null) {
            throw e;
        }
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public Protocol getProtocol() {
        return Protocol.HTTP_2;
    }

    /* access modifiers changed from: package-private */
    public synchronized Http2Stream getStream(int i) {
        return this.streams.get(Integer.valueOf(i));
    }

    public synchronized boolean isShutdown() {
        return this.shutdown;
    }

    public synchronized int maxConcurrentStreams() {
        return this.peerSettings.getMaxConcurrentStreams(Integer.MAX_VALUE);
    }

    public Http2Stream newStream(List<Header> list, boolean z) throws IOException {
        return newStream(0, list, z);
    }

    public synchronized int openStreamCount() {
        return this.streams.size();
    }

    public Ping ping() throws IOException {
        int i;
        Ping ping = new Ping();
        synchronized (this) {
            if (!this.shutdown) {
                i = this.nextPingId;
                this.nextPingId += 2;
                if (this.pings == null) {
                    this.pings = new LinkedHashMap();
                }
                this.pings.put(Integer.valueOf(i), ping);
            } else {
                throw new ConnectionShutdownException();
            }
        }
        writePing(false, i, 1330343787, ping);
        return ping;
    }

    /* access modifiers changed from: package-private */
    public void pushDataLater(int i, BufferedSource bufferedSource, int i2, boolean z) throws IOException {
        final Buffer buffer = new Buffer();
        bufferedSource.require((long) i2);
        bufferedSource.read(buffer, (long) i2);
        if (buffer.size() != ((long) i2)) {
            throw new IOException(buffer.size() + " != " + i2);
        }
        final int i3 = i;
        final int i4 = i2;
        final boolean z2 = z;
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) {
            public void execute() {
                try {
                    boolean onData = Http2Connection.this.pushObserver.onData(i3, buffer, i4, z2);
                    if (onData) {
                        Http2Connection.this.writer.rstStream(i3, ErrorCode.CANCEL);
                    }
                    if (onData || z2) {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i3));
                        }
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void pushHeadersLater(int i, List<Header> list, boolean z) {
        final int i2 = i;
        final List<Header> list2 = list;
        final boolean z2 = z;
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) {
            public void execute() {
                boolean onHeaders = Http2Connection.this.pushObserver.onHeaders(i2, list2, z2);
                if (onHeaders) {
                    Http2Connection.this.writer.rstStream(i2, ErrorCode.CANCEL);
                }
                if (onHeaders || z2) {
                    try {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i2));
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void pushRequestLater(int i, List<Header> list) {
        synchronized (this) {
            if (!this.currentPushRequests.contains(Integer.valueOf(i))) {
                this.currentPushRequests.add(Integer.valueOf(i));
                final int i2 = i;
                final List<Header> list2 = list;
                this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Request[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) {
                    public void execute() {
                        if (Http2Connection.this.pushObserver.onRequest(i2, list2)) {
                            try {
                                Http2Connection.this.writer.rstStream(i2, ErrorCode.CANCEL);
                                synchronized (Http2Connection.this) {
                                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i2));
                                }
                            } catch (IOException e) {
                            }
                        }
                    }
                });
                return;
            }
            writeSynResetLater(i, ErrorCode.PROTOCOL_ERROR);
        }
    }

    /* access modifiers changed from: package-private */
    public void pushResetLater(int i, ErrorCode errorCode) {
        final int i2 = i;
        final ErrorCode errorCode2 = errorCode;
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) {
            public void execute() {
                Http2Connection.this.pushObserver.onReset(i2, errorCode2);
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i2));
                }
            }
        });
    }

    public Http2Stream pushStream(int i, List<Header> list, boolean z) throws IOException {
        if (!this.client) {
            return newStream(i, list, z);
        }
        throw new IllegalStateException("Client cannot push requests.");
    }

    /* access modifiers changed from: package-private */
    public boolean pushedStream(int i) {
        return i != 0 && (i & 1) == 0;
    }

    /* access modifiers changed from: package-private */
    public synchronized Ping removePing(int i) {
        Ping ping = null;
        synchronized (this) {
            if (this.pings != null) {
                ping = this.pings.remove(Integer.valueOf(i));
            }
        }
        return ping;
    }

    /* access modifiers changed from: package-private */
    public synchronized Http2Stream removeStream(int i) {
        Http2Stream remove;
        remove = this.streams.remove(Integer.valueOf(i));
        notifyAll();
        return remove;
    }

    public void setSettings(Settings settings) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (!this.shutdown) {
                    this.okHttpSettings.merge(settings);
                    this.writer.settings(settings);
                } else {
                    throw new ConnectionShutdownException();
                }
            }
        }
    }

    public void shutdown(ErrorCode errorCode) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (!this.shutdown) {
                    this.shutdown = true;
                    int i = this.lastGoodStreamId;
                    this.writer.goAway(i, errorCode, Util.EMPTY_BYTE_ARRAY);
                }
            }
        }
    }

    public void start() throws IOException {
        start(true);
    }

    /* access modifiers changed from: package-private */
    public void start(boolean z) throws IOException {
        if (z) {
            this.writer.connectionPreface();
            this.writer.settings(this.okHttpSettings);
            int initialWindowSize = this.okHttpSettings.getInitialWindowSize();
            if (initialWindowSize != 65535) {
                this.writer.windowUpdate(0, (long) (initialWindowSize - SupportMenu.USER_MASK));
            }
        }
        new Thread(this.readerRunnable).start();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r3 = java.lang.Math.min((int) java.lang.Math.min(r14, r10.bytesLeftInWriteWindow), r10.writer.maxDataLength());
        r10.bytesLeftInWriteWindow -= (long) r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeData(int r11, boolean r12, okio.Buffer r13, long r14) throws java.io.IOException {
        /*
            r10 = this;
            r1 = 1
            r8 = 0
            r2 = 0
            int r0 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r0 != 0) goto L_0x0016
            okhttp3.internal.http2.Http2Writer r0 = r10.writer
            r0.data(r12, r11, r13, r2)
            return
        L_0x000e:
            int r0 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r0 != 0) goto L_0x006c
            r0 = r1
        L_0x0013:
            r4.data(r0, r11, r13, r3)
        L_0x0016:
            int r0 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r0 > 0) goto L_0x0041
            r0 = r1
        L_0x001b:
            if (r0 != 0) goto L_0x006e
            monitor-enter(r10)
        L_0x001e:
            long r4 = r10.bytesLeftInWriteWindow     // Catch:{ InterruptedException -> 0x0037 }
            int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x0043
            r0 = r1
        L_0x0025:
            if (r0 != 0) goto L_0x004e
            java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r0 = r10.streams     // Catch:{ InterruptedException -> 0x0037 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)     // Catch:{ InterruptedException -> 0x0037 }
            boolean r0 = r0.containsKey(r3)     // Catch:{ InterruptedException -> 0x0037 }
            if (r0 == 0) goto L_0x0045
            r10.wait()     // Catch:{ InterruptedException -> 0x0037 }
            goto L_0x001e
        L_0x0037:
            r0 = move-exception
            java.io.InterruptedIOException r0 = new java.io.InterruptedIOException     // Catch:{ all -> 0x003e }
            r0.<init>()     // Catch:{ all -> 0x003e }
            throw r0     // Catch:{ all -> 0x003e }
        L_0x003e:
            r0 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x003e }
            throw r0
        L_0x0041:
            r0 = r2
            goto L_0x001b
        L_0x0043:
            r0 = r2
            goto L_0x0025
        L_0x0045:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ InterruptedException -> 0x0037 }
            java.lang.String r1 = "stream closed"
            r0.<init>(r1)     // Catch:{ InterruptedException -> 0x0037 }
            throw r0     // Catch:{ InterruptedException -> 0x0037 }
        L_0x004e:
            long r4 = r10.bytesLeftInWriteWindow     // Catch:{ all -> 0x003e }
            long r4 = java.lang.Math.min(r14, r4)     // Catch:{ all -> 0x003e }
            int r0 = (int) r4     // Catch:{ all -> 0x003e }
            okhttp3.internal.http2.Http2Writer r3 = r10.writer     // Catch:{ all -> 0x003e }
            int r3 = r3.maxDataLength()     // Catch:{ all -> 0x003e }
            int r3 = java.lang.Math.min(r0, r3)     // Catch:{ all -> 0x003e }
            long r4 = r10.bytesLeftInWriteWindow     // Catch:{ all -> 0x003e }
            long r6 = (long) r3     // Catch:{ all -> 0x003e }
            long r4 = r4 - r6
            r10.bytesLeftInWriteWindow = r4     // Catch:{ all -> 0x003e }
            monitor-exit(r10)     // Catch:{ all -> 0x003e }
            long r4 = (long) r3
            long r14 = r14 - r4
            okhttp3.internal.http2.Http2Writer r4 = r10.writer
            if (r12 != 0) goto L_0x000e
        L_0x006c:
            r0 = r2
            goto L_0x0013
        L_0x006e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.writeData(int, boolean, okio.Buffer, long):void");
    }

    /* access modifiers changed from: package-private */
    public void writePing(boolean z, int i, int i2, Ping ping) throws IOException {
        synchronized (this.writer) {
            if (ping != null) {
                ping.send();
            }
            this.writer.ping(z, i, i2);
        }
    }

    /* access modifiers changed from: package-private */
    public void writePingLater(boolean z, int i, int i2, Ping ping) {
        final boolean z2 = z;
        final int i3 = i;
        final int i4 = i2;
        final Ping ping2 = ping;
        executor.execute(new NamedRunnable("OkHttp %s ping %08x%08x", new Object[]{this.hostname, Integer.valueOf(i), Integer.valueOf(i2)}) {
            public void execute() {
                try {
                    Http2Connection.this.writePing(z2, i3, i4, ping2);
                } catch (IOException e) {
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void writeSynReply(int i, boolean z, List<Header> list) throws IOException {
        this.writer.synReply(z, i, list);
    }

    /* access modifiers changed from: package-private */
    public void writeSynReset(int i, ErrorCode errorCode) throws IOException {
        this.writer.rstStream(i, errorCode);
    }

    /* access modifiers changed from: package-private */
    public void writeSynResetLater(int i, ErrorCode errorCode) {
        final int i2 = i;
        final ErrorCode errorCode2 = errorCode;
        executor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[]{this.hostname, Integer.valueOf(i)}) {
            public void execute() {
                try {
                    Http2Connection.this.writeSynReset(i2, errorCode2);
                } catch (IOException e) {
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void writeWindowUpdateLater(int i, long j) {
        final int i2 = i;
        final long j2 = j;
        executor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[]{this.hostname, Integer.valueOf(i)}) {
            public void execute() {
                try {
                    Http2Connection.this.writer.windowUpdate(i2, j2);
                } catch (IOException e) {
                }
            }
        });
    }
}
