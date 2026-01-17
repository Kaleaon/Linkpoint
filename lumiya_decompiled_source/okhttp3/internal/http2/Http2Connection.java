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
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
/* loaded from: classes.dex */
public final class Http2Connection implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
    static final ExecutorService executor;
    long bytesLeftInWriteWindow;
    final boolean client;
    final String hostname;
    int lastGoodStreamId;
    final Listener listener;
    private int nextPingId;
    int nextStreamId;
    private Map<Integer, Ping> pings;
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    final ReaderRunnable readerRunnable;
    boolean shutdown;
    final Socket socket;
    final Http2Writer writer;
    final Map<Integer, Http2Stream> streams = new LinkedHashMap();
    long unacknowledgedBytesRead = 0;
    Settings okHttpSettings = new Settings();
    final Settings peerSettings = new Settings();
    boolean receivedInitialPeerSettings = false;
    final Set<Integer> currentPushRequests = new LinkedHashSet();

    /* loaded from: classes.dex */
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

        public Builder listener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public Builder pushObserver(PushObserver pushObserver) {
            this.pushObserver = pushObserver;
            return this;
        }

        public Builder socket(Socket socket) throws IOException {
            return socket(socket, ((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName(), Okio.buffer(Okio.source(socket)), Okio.buffer(Okio.sink(socket)));
        }

        public Builder socket(Socket socket, String str, BufferedSource bufferedSource, BufferedSink bufferedSink) {
            this.socket = socket;
            this.hostname = str;
            this.source = bufferedSource;
            this.sink = bufferedSink;
            return this;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Listener {
        public static final Listener REFUSE_INCOMING_STREAMS = new Listener() { // from class: okhttp3.internal.http2.Http2Connection.Listener.1
            @Override // okhttp3.internal.http2.Http2Connection.Listener
            public void onStream(Http2Stream http2Stream) throws IOException {
                http2Stream.close(ErrorCode.REFUSED_STREAM);
            }
        };

        public void onSettings(Http2Connection http2Connection) {
        }

        public abstract void onStream(Http2Stream http2Stream) throws IOException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ReaderRunnable extends NamedRunnable implements Http2Reader.Handler {
        final Http2Reader reader;

        ReaderRunnable(Http2Reader http2Reader) {
            super("OkHttp %s", Http2Connection.this.hostname);
            this.reader = http2Reader;
        }

        private void applyAndAckSettings(final Settings settings) {
            Http2Connection.executor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[]{Http2Connection.this.hostname}) { // from class: okhttp3.internal.http2.Http2Connection.ReaderRunnable.3
                @Override // okhttp3.internal.NamedRunnable
                public void execute() {
                    try {
                        Http2Connection.this.writer.applyAndAckSettings(settings);
                    } catch (IOException e) {
                    }
                }
            });
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void ackSettings() {
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void alternateService(int i, String str, ByteString byteString, String str2, int i2, long j) {
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void data(boolean z, int i, BufferedSource bufferedSource, int i2) throws IOException {
            if (Http2Connection.this.pushedStream(i)) {
                Http2Connection.this.pushDataLater(i, bufferedSource, i2, z);
                return;
            }
            Http2Stream stream = Http2Connection.this.getStream(i);
            if (stream == null) {
                Http2Connection.this.writeSynResetLater(i, ErrorCode.PROTOCOL_ERROR);
                bufferedSource.skip(i2);
                return;
            }
            stream.receiveData(bufferedSource, i2);
            if (z) {
                stream.receiveFin();
            }
        }

        @Override // okhttp3.internal.NamedRunnable
        protected void execute() {
            ErrorCode errorCode;
            Throwable th;
            ErrorCode errorCode2 = ErrorCode.INTERNAL_ERROR;
            ErrorCode errorCode3 = ErrorCode.INTERNAL_ERROR;
            try {
                try {
                    if (!Http2Connection.this.client) {
                        this.reader.readConnectionPreface();
                    }
                    do {
                    } while (this.reader.nextFrame(this));
                    errorCode2 = ErrorCode.NO_ERROR;
                    try {
                        Http2Connection.this.close(errorCode2, ErrorCode.CANCEL);
                    } catch (IOException e) {
                    }
                    Util.closeQuietly(this.reader);
                } catch (IOException e2) {
                    errorCode = ErrorCode.PROTOCOL_ERROR;
                    try {
                        try {
                            Http2Connection.this.close(errorCode, ErrorCode.PROTOCOL_ERROR);
                        } catch (IOException e3) {
                        }
                        Util.closeQuietly(this.reader);
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            Http2Connection.this.close(errorCode, errorCode3);
                        } catch (IOException e4) {
                        }
                        Util.closeQuietly(this.reader);
                        throw th;
                    }
                }
            } catch (Throwable th3) {
                errorCode = errorCode2;
                th = th3;
                Http2Connection.this.close(errorCode, errorCode3);
                Util.closeQuietly(this.reader);
                throw th;
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void goAway(int i, ErrorCode errorCode, ByteString byteString) {
            Http2Stream[] http2StreamArr;
            byteString.size();
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

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void headers(boolean z, int i, int i2, List<Header> list) {
            if (Http2Connection.this.pushedStream(i)) {
                Http2Connection.this.pushHeadersLater(i, list, z);
                return;
            }
            synchronized (Http2Connection.this) {
                if (Http2Connection.this.shutdown) {
                    return;
                }
                Http2Stream stream = Http2Connection.this.getStream(i);
                if (stream != null) {
                    stream.receiveHeaders(list);
                    if (z) {
                        stream.receiveFin();
                    }
                } else if (i > Http2Connection.this.lastGoodStreamId) {
                    if (i % 2 != Http2Connection.this.nextStreamId % 2) {
                        final Http2Stream http2Stream = new Http2Stream(i, Http2Connection.this, false, z, list);
                        Http2Connection.this.lastGoodStreamId = i;
                        Http2Connection.this.streams.put(Integer.valueOf(i), http2Stream);
                        Http2Connection.executor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[]{Http2Connection.this.hostname, Integer.valueOf(i)}) { // from class: okhttp3.internal.http2.Http2Connection.ReaderRunnable.1
                            @Override // okhttp3.internal.NamedRunnable
                            public void execute() {
                                try {
                                    Http2Connection.this.listener.onStream(http2Stream);
                                } catch (IOException e) {
                                    Platform.get().log(4, "FramedConnection.Listener failure for " + Http2Connection.this.hostname, e);
                                    try {
                                        http2Stream.close(ErrorCode.PROTOCOL_ERROR);
                                    } catch (IOException e2) {
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void ping(boolean z, int i, int i2) {
            if (!z) {
                Http2Connection.this.writePingLater(true, i, i2, null);
                return;
            }
            Ping removePing = Http2Connection.this.removePing(i);
            if (removePing != null) {
                removePing.receive();
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void priority(int i, int i2, int i3, boolean z) {
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void pushPromise(int i, int i2, List<Header> list) {
            Http2Connection.this.pushRequestLater(i2, list);
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void rstStream(int i, ErrorCode errorCode) {
            if (Http2Connection.this.pushedStream(i)) {
                Http2Connection.this.pushResetLater(i, errorCode);
                return;
            }
            Http2Stream removeStream = Http2Connection.this.removeStream(i);
            if (removeStream == null) {
                return;
            }
            removeStream.receiveRstStream(errorCode);
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
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
                    long j2 = initialWindowSize2 - initialWindowSize;
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
                Http2Connection.executor.execute(new NamedRunnable("OkHttp %s settings", Http2Connection.this.hostname) { // from class: okhttp3.internal.http2.Http2Connection.ReaderRunnable.2
                    @Override // okhttp3.internal.NamedRunnable
                    public void execute() {
                        Http2Connection.this.listener.onSettings(Http2Connection.this);
                    }
                });
            }
            if (http2StreamArr == null || j == 0) {
                return;
            }
            for (Http2Stream http2Stream : http2StreamArr) {
                synchronized (http2Stream) {
                    http2Stream.addBytesToWriteWindow(j);
                }
            }
        }

        @Override // okhttp3.internal.http2.Http2Reader.Handler
        public void windowUpdate(int i, long j) {
            if (i != 0) {
                Http2Stream stream = Http2Connection.this.getStream(i);
                if (stream == null) {
                    return;
                }
                synchronized (stream) {
                    stream.addBytesToWriteWindow(j);
                }
                return;
            }
            synchronized (Http2Connection.this) {
                Http2Connection.this.bytesLeftInWriteWindow += j;
                Http2Connection.this.notifyAll();
            }
        }
    }

    static {
        $assertionsDisabled = !Http2Connection.class.desiredAssertionStatus();
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp FramedConnection", true));
    }

    Http2Connection(Builder builder) {
        this.pushObserver = builder.pushObserver;
        this.client = builder.client;
        this.listener = builder.listener;
        this.nextStreamId = !builder.client ? 2 : 1;
        if (builder.client) {
            this.nextStreamId += 2;
        }
        this.nextPingId = builder.client ? 1 : 2;
        if (builder.client) {
            this.okHttpSettings.set(7, 16777216);
        }
        this.hostname = builder.hostname;
        this.pushExecutor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory(Util.format("OkHttp %s Push Observer", this.hostname), true));
        this.peerSettings.set(7, SupportMenu.USER_MASK);
        this.peerSettings.set(5, 16384);
        this.bytesLeftInWriteWindow = this.peerSettings.getInitialWindowSize();
        this.socket = builder.socket;
        this.writer = new Http2Writer(builder.sink, this.client);
        this.readerRunnable = new ReaderRunnable(new Http2Reader(builder.source, this.client));
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x0053 A[Catch: all -> 0x0040, TRY_LEAVE, TryCatch #1 {, blocks: (B:7:0x000a, B:17:0x002b, B:19:0x002f, B:20:0x0034, B:40:0x0063, B:41:0x006b, B:39:0x005d, B:8:0x000b, B:10:0x000f, B:13:0x0022, B:15:0x0028, B:37:0x0053, B:32:0x0046, B:34:0x004c, B:24:0x003a, B:25:0x003f), top: B:43:0x000a }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private okhttp3.internal.http2.Http2Stream newStream(int r13, java.util.List<okhttp3.internal.http2.Header> r14, boolean r15) throws java.io.IOException {
        /*
            r12 = this;
            r10 = 0
            r7 = 1
            r6 = 0
            if (r15 == 0) goto L38
            r3 = r6
        L7:
            okhttp3.internal.http2.Http2Writer r8 = r12.writer
            monitor-enter(r8)
            monitor-enter(r12)     // Catch: java.lang.Throwable -> L43
            boolean r0 = r12.shutdown     // Catch: java.lang.Throwable -> L40
            if (r0 != 0) goto L3a
            int r1 = r12.nextStreamId     // Catch: java.lang.Throwable -> L40
            int r0 = r12.nextStreamId     // Catch: java.lang.Throwable -> L40
            int r0 = r0 + 2
            r12.nextStreamId = r0     // Catch: java.lang.Throwable -> L40
            okhttp3.internal.http2.Http2Stream r0 = new okhttp3.internal.http2.Http2Stream     // Catch: java.lang.Throwable -> L40
            r4 = 0
            r2 = r12
            r5 = r14
            r0.<init>(r1, r2, r3, r4, r5)     // Catch: java.lang.Throwable -> L40
            if (r15 != 0) goto L46
        L21:
            r6 = r7
        L22:
            boolean r2 = r0.isOpen()     // Catch: java.lang.Throwable -> L40
            if (r2 != 0) goto L53
        L28:
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L40
            if (r13 == 0) goto L5d
            boolean r2 = r12.client     // Catch: java.lang.Throwable -> L43
            if (r2 != 0) goto L63
            okhttp3.internal.http2.Http2Writer r2 = r12.writer     // Catch: java.lang.Throwable -> L43
            r2.pushPromise(r13, r1, r14)     // Catch: java.lang.Throwable -> L43
        L34:
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L43
            if (r6 != 0) goto L6c
        L37:
            return r0
        L38:
            r3 = r7
            goto L7
        L3a:
            okhttp3.internal.http2.ConnectionShutdownException r0 = new okhttp3.internal.http2.ConnectionShutdownException     // Catch: java.lang.Throwable -> L40
            r0.<init>()     // Catch: java.lang.Throwable -> L40
            throw r0     // Catch: java.lang.Throwable -> L40
        L40:
            r0 = move-exception
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L40
            throw r0     // Catch: java.lang.Throwable -> L43
        L43:
            r0 = move-exception
            monitor-exit(r8)     // Catch: java.lang.Throwable -> L43
            throw r0
        L46:
            long r4 = r12.bytesLeftInWriteWindow     // Catch: java.lang.Throwable -> L40
            int r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r2 == 0) goto L21
            long r4 = r0.bytesLeftInWriteWindow     // Catch: java.lang.Throwable -> L40
            int r2 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r2 != 0) goto L22
            goto L21
        L53:
            java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r2 = r12.streams     // Catch: java.lang.Throwable -> L40
            java.lang.Integer r4 = java.lang.Integer.valueOf(r1)     // Catch: java.lang.Throwable -> L40
            r2.put(r4, r0)     // Catch: java.lang.Throwable -> L40
            goto L28
        L5d:
            okhttp3.internal.http2.Http2Writer r2 = r12.writer     // Catch: java.lang.Throwable -> L43
            r2.synStream(r3, r1, r13, r14)     // Catch: java.lang.Throwable -> L43
            goto L34
        L63:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch: java.lang.Throwable -> L43
            java.lang.String r1 = "client streams shouldn't have associated stream IDs"
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L43
            throw r0     // Catch: java.lang.Throwable -> L43
        L6c:
            okhttp3.internal.http2.Http2Writer r1 = r12.writer
            r1.flush()
            goto L37
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.newStream(int, java.util.List, boolean):okhttp3.internal.http2.Http2Stream");
    }

    void addBytesToWriteWindow(long j) {
        this.bytesLeftInWriteWindow += j;
        if (j <= 0) {
            return;
        }
        notifyAll();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }

    void close(ErrorCode errorCode, ErrorCode errorCode2) throws IOException {
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
            for (Http2Stream http2Stream : http2StreamArr) {
                try {
                    http2Stream.close(errorCode2);
                } catch (IOException e2) {
                    if (iOException2 != null) {
                        iOException2 = e2;
                    }
                }
            }
            iOException = iOException2;
        }
        if (pingArr != null) {
            for (Ping ping : pingArr) {
                ping.cancel();
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

    synchronized Http2Stream getStream(int i) {
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
            if (this.shutdown) {
                throw new ConnectionShutdownException();
            }
            i = this.nextPingId;
            this.nextPingId += 2;
            if (this.pings == null) {
                this.pings = new LinkedHashMap();
            }
            this.pings.put(Integer.valueOf(i), ping);
        }
        writePing(false, i, 1330343787, ping);
        return ping;
    }

    void pushDataLater(final int i, BufferedSource bufferedSource, final int i2, final boolean z) throws IOException {
        final Buffer buffer = new Buffer();
        bufferedSource.require(i2);
        bufferedSource.read(buffer, i2);
        if (buffer.size() != i2) {
            throw new IOException(buffer.size() + " != " + i2);
        }
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) { // from class: okhttp3.internal.http2.Http2Connection.6
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                try {
                    boolean onData = Http2Connection.this.pushObserver.onData(i, buffer, i2, z);
                    if (onData) {
                        Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                    }
                    if (onData || z) {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                        }
                    }
                } catch (IOException e) {
                }
            }
        });
    }

    void pushHeadersLater(final int i, final List<Header> list, final boolean z) {
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) { // from class: okhttp3.internal.http2.Http2Connection.5
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                boolean onHeaders = Http2Connection.this.pushObserver.onHeaders(i, list, z);
                if (onHeaders) {
                    try {
                        Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                    } catch (IOException e) {
                        return;
                    }
                }
                if (onHeaders || z) {
                    synchronized (Http2Connection.this) {
                        Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                    }
                }
            }
        });
    }

    void pushRequestLater(final int i, final List<Header> list) {
        synchronized (this) {
            if (this.currentPushRequests.contains(Integer.valueOf(i))) {
                writeSynResetLater(i, ErrorCode.PROTOCOL_ERROR);
                return;
            }
            this.currentPushRequests.add(Integer.valueOf(i));
            this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Request[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) { // from class: okhttp3.internal.http2.Http2Connection.4
                @Override // okhttp3.internal.NamedRunnable
                public void execute() {
                    if (Http2Connection.this.pushObserver.onRequest(i, list)) {
                        try {
                            Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                            synchronized (Http2Connection.this) {
                                Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            });
        }
    }

    void pushResetLater(final int i, final ErrorCode errorCode) {
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) { // from class: okhttp3.internal.http2.Http2Connection.7
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                Http2Connection.this.pushObserver.onReset(i, errorCode);
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i));
                }
            }
        });
    }

    public Http2Stream pushStream(int i, List<Header> list, boolean z) throws IOException {
        if (this.client) {
            throw new IllegalStateException("Client cannot push requests.");
        }
        return newStream(i, list, z);
    }

    boolean pushedStream(int i) {
        return i != 0 && (i & 1) == 0;
    }

    synchronized Ping removePing(int i) {
        Ping remove;
        synchronized (this) {
            remove = this.pings != null ? this.pings.remove(Integer.valueOf(i)) : null;
        }
        return remove;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized Http2Stream removeStream(int i) {
        Http2Stream remove;
        remove = this.streams.remove(Integer.valueOf(i));
        notifyAll();
        return remove;
    }

    public void setSettings(Settings settings) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                this.okHttpSettings.merge(settings);
                this.writer.settings(settings);
            }
        }
    }

    public void shutdown(ErrorCode errorCode) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    return;
                }
                this.shutdown = true;
                this.writer.goAway(this.lastGoodStreamId, errorCode, Util.EMPTY_BYTE_ARRAY);
            }
        }
    }

    public void start() throws IOException {
        start(true);
    }

    void start(boolean z) throws IOException {
        if (z) {
            this.writer.connectionPreface();
            this.writer.settings(this.okHttpSettings);
            int initialWindowSize = this.okHttpSettings.getInitialWindowSize();
            if (initialWindowSize != 65535) {
                this.writer.windowUpdate(0, initialWindowSize - SupportMenu.USER_MASK);
            }
        }
        new Thread(this.readerRunnable).start();
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x004d, code lost:
        throw new java.io.IOException("stream closed");
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x004e, code lost:
        r3 = java.lang.Math.min((int) java.lang.Math.min(r14, r10.bytesLeftInWriteWindow), r10.writer.maxDataLength());
        r10.bytesLeftInWriteWindow -= r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void writeData(int r11, boolean r12, okio.Buffer r13, long r14) throws java.io.IOException {
        /*
            r10 = this;
            r1 = 1
            r8 = 0
            r2 = 0
            int r0 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r0 != 0) goto L16
            okhttp3.internal.http2.Http2Writer r0 = r10.writer
            r0.data(r12, r11, r13, r2)
            return
        Le:
            int r0 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r0 != 0) goto L6c
            r0 = r1
        L13:
            r4.data(r0, r11, r13, r3)
        L16:
            int r0 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r0 > 0) goto L41
            r0 = r1
        L1b:
            if (r0 != 0) goto L6e
            monitor-enter(r10)
        L1e:
            long r4 = r10.bytesLeftInWriteWindow     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
            int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r0 <= 0) goto L43
            r0 = r1
        L25:
            if (r0 != 0) goto L4e
            java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r0 = r10.streams     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
            boolean r0 = r0.containsKey(r3)     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
            if (r0 == 0) goto L45
            r10.wait()     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
            goto L1e
        L37:
            r0 = move-exception
            java.io.InterruptedIOException r0 = new java.io.InterruptedIOException     // Catch: java.lang.Throwable -> L3e
            r0.<init>()     // Catch: java.lang.Throwable -> L3e
            throw r0     // Catch: java.lang.Throwable -> L3e
        L3e:
            r0 = move-exception
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L3e
            throw r0
        L41:
            r0 = r2
            goto L1b
        L43:
            r0 = r2
            goto L25
        L45:
            java.io.IOException r0 = new java.io.IOException     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
            java.lang.String r1 = "stream closed"
            r0.<init>(r1)     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
            throw r0     // Catch: java.lang.InterruptedException -> L37 java.lang.Throwable -> L3e
        L4e:
            long r4 = r10.bytesLeftInWriteWindow     // Catch: java.lang.Throwable -> L3e
            long r4 = java.lang.Math.min(r14, r4)     // Catch: java.lang.Throwable -> L3e
            int r0 = (int) r4     // Catch: java.lang.Throwable -> L3e
            okhttp3.internal.http2.Http2Writer r3 = r10.writer     // Catch: java.lang.Throwable -> L3e
            int r3 = r3.maxDataLength()     // Catch: java.lang.Throwable -> L3e
            int r3 = java.lang.Math.min(r0, r3)     // Catch: java.lang.Throwable -> L3e
            long r4 = r10.bytesLeftInWriteWindow     // Catch: java.lang.Throwable -> L3e
            long r6 = (long) r3     // Catch: java.lang.Throwable -> L3e
            long r4 = r4 - r6
            r10.bytesLeftInWriteWindow = r4     // Catch: java.lang.Throwable -> L3e
            monitor-exit(r10)     // Catch: java.lang.Throwable -> L3e
            long r4 = (long) r3
            long r14 = r14 - r4
            okhttp3.internal.http2.Http2Writer r4 = r10.writer
            if (r12 != 0) goto Le
        L6c:
            r0 = r2
            goto L13
        L6e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.writeData(int, boolean, okio.Buffer, long):void");
    }

    void writePing(boolean z, int i, int i2, Ping ping) throws IOException {
        synchronized (this.writer) {
            if (ping != null) {
                ping.send();
            }
            this.writer.ping(z, i, i2);
        }
    }

    void writePingLater(final boolean z, final int i, final int i2, final Ping ping) {
        executor.execute(new NamedRunnable("OkHttp %s ping %08x%08x", new Object[]{this.hostname, Integer.valueOf(i), Integer.valueOf(i2)}) { // from class: okhttp3.internal.http2.Http2Connection.3
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                try {
                    Http2Connection.this.writePing(z, i, i2, ping);
                } catch (IOException e) {
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeSynReply(int i, boolean z, List<Header> list) throws IOException {
        this.writer.synReply(z, i, list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeSynReset(int i, ErrorCode errorCode) throws IOException {
        this.writer.rstStream(i, errorCode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeSynResetLater(final int i, final ErrorCode errorCode) {
        executor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[]{this.hostname, Integer.valueOf(i)}) { // from class: okhttp3.internal.http2.Http2Connection.1
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                try {
                    Http2Connection.this.writeSynReset(i, errorCode);
                } catch (IOException e) {
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeWindowUpdateLater(final int i, final long j) {
        executor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[]{this.hostname, Integer.valueOf(i)}) { // from class: okhttp3.internal.http2.Http2Connection.2
            @Override // okhttp3.internal.NamedRunnable
            public void execute() {
                try {
                    Http2Connection.this.writer.windowUpdate(i, j);
                } catch (IOException e) {
                }
            }
        });
    }
}
