package okhttp3.internal.ws;

import com.google.common.net.HttpHeaders;
import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.connection.StreamAllocation;
import okhttp3.internal.ws.WebSocketReader;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;

public final class RealWebSocket implements WebSocket, WebSocketReader.FrameCallback {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000;
    private static final long MAX_QUEUE_SIZE = 16777216;
    private static final List<Protocol> ONLY_HTTP1 = Collections.singletonList(Protocol.HTTP_1_1);
    private Call call;
    private ScheduledFuture<?> cancelFuture;
    private boolean enqueuedClose;
    private ScheduledExecutorService executor;
    private boolean failed;
    private final String key;
    final WebSocketListener listener;
    private final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque<>();
    private final Request originalRequest;
    int pingCount;
    int pongCount;
    private final ArrayDeque<ByteString> pongQueue = new ArrayDeque<>();
    private long queueSize;
    private final Random random;
    private WebSocketReader reader;
    private int receivedCloseCode = -1;
    private String receivedCloseReason;
    private Streams streams;
    private WebSocketWriter writer;
    private final Runnable writerRunnable;

    final class CancelRunnable implements Runnable {
        CancelRunnable() {
        }

        public void run() {
            RealWebSocket.this.cancel();
        }
    }

    static final class ClientStreams extends Streams {
        private final StreamAllocation streamAllocation;

        ClientStreams(StreamAllocation streamAllocation2) {
            super(true, streamAllocation2.connection().source, streamAllocation2.connection().sink);
            this.streamAllocation = streamAllocation2;
        }

        public void close() {
            this.streamAllocation.streamFinished(true, this.streamAllocation.codec());
        }
    }

    static final class Close {
        final long cancelAfterCloseMillis;
        final int code;
        final ByteString reason;

        Close(int i, ByteString byteString, long j) {
            this.code = i;
            this.reason = byteString;
            this.cancelAfterCloseMillis = j;
        }
    }

    static final class Message {
        final ByteString data;
        final int formatOpcode;

        Message(int i, ByteString byteString) {
            this.formatOpcode = i;
            this.data = byteString;
        }
    }

    private final class PingRunnable implements Runnable {
        private PingRunnable() {
        }

        public void run() {
            RealWebSocket.this.writePingFrame();
        }
    }

    public static abstract class Streams implements Closeable {
        public final boolean client;
        public final BufferedSink sink;
        public final BufferedSource source;

        public Streams(boolean z, BufferedSource bufferedSource, BufferedSink bufferedSink) {
            this.client = z;
            this.source = bufferedSource;
            this.sink = bufferedSink;
        }
    }

    static {
        boolean z = false;
        if (!RealWebSocket.class.desiredAssertionStatus()) {
            z = true;
        }
        $assertionsDisabled = z;
    }

    public RealWebSocket(Request request, WebSocketListener webSocketListener, Random random2) {
        if ("GET".equals(request.method())) {
            this.originalRequest = request;
            this.listener = webSocketListener;
            this.random = random2;
            byte[] bArr = new byte[16];
            random2.nextBytes(bArr);
            this.key = ByteString.of(bArr).base64();
            this.writerRunnable = new Runnable() {
                public void run() {
                    do {
                        try {
                        } catch (IOException e) {
                            RealWebSocket.this.failWebSocket(e, (Response) null);
                            return;
                        }
                    } while (RealWebSocket.this.writeOneFrame());
                }
            };
            return;
        }
        throw new IllegalArgumentException("Request must be GET: " + request.method());
    }

    private void runWriter() {
        if (!$assertionsDisabled && !Thread.holdsLock(this)) {
            throw new AssertionError();
        } else if (this.executor != null) {
            this.executor.execute(this.writerRunnable);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0008, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean send(okio.ByteString r9, int r10) {
        /*
            r8 = this;
            r1 = 1
            r2 = 0
            monitor-enter(r8)
            boolean r0 = r8.failed     // Catch:{ all -> 0x0042 }
            if (r0 == 0) goto L_0x0009
        L_0x0007:
            monitor-exit(r8)
            return r2
        L_0x0009:
            boolean r0 = r8.enqueuedClose     // Catch:{ all -> 0x0042 }
            if (r0 != 0) goto L_0x0007
            long r4 = r8.queueSize     // Catch:{ all -> 0x0042 }
            int r0 = r9.size()     // Catch:{ all -> 0x0042 }
            long r6 = (long) r0     // Catch:{ all -> 0x0042 }
            long r4 = r4 + r6
            r6 = 16777216(0x1000000, double:8.289046E-317)
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 > 0) goto L_0x0027
            r0 = r1
        L_0x001d:
            if (r0 != 0) goto L_0x0029
            r0 = 0
            r1 = 1001(0x3e9, float:1.403E-42)
            r8.close(r1, r0)     // Catch:{ all -> 0x0042 }
            monitor-exit(r8)
            return r2
        L_0x0027:
            r0 = r2
            goto L_0x001d
        L_0x0029:
            long r2 = r8.queueSize     // Catch:{ all -> 0x0042 }
            int r0 = r9.size()     // Catch:{ all -> 0x0042 }
            long r4 = (long) r0     // Catch:{ all -> 0x0042 }
            long r2 = r2 + r4
            r8.queueSize = r2     // Catch:{ all -> 0x0042 }
            java.util.ArrayDeque<java.lang.Object> r0 = r8.messageAndCloseQueue     // Catch:{ all -> 0x0042 }
            okhttp3.internal.ws.RealWebSocket$Message r2 = new okhttp3.internal.ws.RealWebSocket$Message     // Catch:{ all -> 0x0042 }
            r2.<init>(r10, r9)     // Catch:{ all -> 0x0042 }
            r0.add(r2)     // Catch:{ all -> 0x0042 }
            r8.runWriter()     // Catch:{ all -> 0x0042 }
            monitor-exit(r8)
            return r1
        L_0x0042:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.send(okio.ByteString, int):boolean");
    }

    /* access modifiers changed from: private */
    public void writePingFrame() {
        synchronized (this) {
            if (!this.failed) {
                WebSocketWriter webSocketWriter = this.writer;
                try {
                    webSocketWriter.writePing(ByteString.EMPTY);
                } catch (IOException e) {
                    failWebSocket(e, (Response) null);
                }
            }
        }
    }

    public void cancel() {
        this.call.cancel();
    }

    /* access modifiers changed from: package-private */
    public void checkResponse(Response response) throws ProtocolException {
        if (response.code() == 101) {
            String header = response.header(HttpHeaders.CONNECTION);
            if (HttpHeaders.UPGRADE.equalsIgnoreCase(header)) {
                String header2 = response.header(HttpHeaders.UPGRADE);
                if ("websocket".equalsIgnoreCase(header2)) {
                    String header3 = response.header("Sec-WebSocket-Accept");
                    String base64 = ByteString.encodeUtf8(this.key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
                    if (!base64.equals(header3)) {
                        throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + base64 + "' but was '" + header3 + "'");
                    }
                    return;
                }
                throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + header2 + "'");
            }
            throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + header + "'");
        }
        throw new ProtocolException("Expected HTTP 101 response but was '" + response.code() + " " + response.message() + "'");
    }

    public boolean close(int i, String str) {
        return close(i, str, 60000);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000e, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean close(int r10, java.lang.String r11, long r12) {
        /*
            r9 = this;
            r0 = 0
            r2 = 1
            r3 = 0
            monitor-enter(r9)
            okhttp3.internal.ws.WebSocketProtocol.validateCloseCode(r10)     // Catch:{ all -> 0x003b }
            if (r11 != 0) goto L_0x000f
        L_0x0009:
            boolean r1 = r9.failed     // Catch:{ all -> 0x003b }
            if (r1 == 0) goto L_0x0040
        L_0x000d:
            monitor-exit(r9)
            return r3
        L_0x000f:
            okio.ByteString r0 = okio.ByteString.encodeUtf8(r11)     // Catch:{ all -> 0x003b }
            int r1 = r0.size()     // Catch:{ all -> 0x003b }
            long r4 = (long) r1     // Catch:{ all -> 0x003b }
            r6 = 123(0x7b, double:6.1E-322)
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 > 0) goto L_0x003e
            r1 = r2
        L_0x001f:
            if (r1 != 0) goto L_0x0009
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x003b }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x003b }
            r1.<init>()     // Catch:{ all -> 0x003b }
            java.lang.String r2 = "reason.size() > 123: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x003b }
            java.lang.StringBuilder r1 = r1.append(r11)     // Catch:{ all -> 0x003b }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x003b }
            r0.<init>(r1)     // Catch:{ all -> 0x003b }
            throw r0     // Catch:{ all -> 0x003b }
        L_0x003b:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        L_0x003e:
            r1 = r3
            goto L_0x001f
        L_0x0040:
            boolean r1 = r9.enqueuedClose     // Catch:{ all -> 0x003b }
            if (r1 != 0) goto L_0x000d
            r1 = 1
            r9.enqueuedClose = r1     // Catch:{ all -> 0x003b }
            java.util.ArrayDeque<java.lang.Object> r1 = r9.messageAndCloseQueue     // Catch:{ all -> 0x003b }
            okhttp3.internal.ws.RealWebSocket$Close r3 = new okhttp3.internal.ws.RealWebSocket$Close     // Catch:{ all -> 0x003b }
            r3.<init>(r10, r0, r12)     // Catch:{ all -> 0x003b }
            r1.add(r3)     // Catch:{ all -> 0x003b }
            r9.runWriter()     // Catch:{ all -> 0x003b }
            monitor-exit(r9)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.close(int, java.lang.String, long):boolean");
    }

    public void connect(OkHttpClient okHttpClient) {
        OkHttpClient build = okHttpClient.newBuilder().protocols(ONLY_HTTP1).build();
        final int pingIntervalMillis = build.pingIntervalMillis();
        final Request build2 = this.originalRequest.newBuilder().header(HttpHeaders.UPGRADE, "websocket").header(HttpHeaders.CONNECTION, HttpHeaders.UPGRADE).header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").build();
        this.call = Internal.instance.newWebSocketCall(build, build2);
        this.call.enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                RealWebSocket.this.failWebSocket(iOException, (Response) null);
            }

            public void onResponse(Call call, Response response) {
                try {
                    RealWebSocket.this.checkResponse(response);
                    StreamAllocation streamAllocation = Internal.instance.streamAllocation(call);
                    streamAllocation.noNewStreams();
                    ClientStreams clientStreams = new ClientStreams(streamAllocation);
                    try {
                        RealWebSocket.this.listener.onOpen(RealWebSocket.this, response);
                        RealWebSocket.this.initReaderAndWriter("OkHttp WebSocket " + build2.url().redact(), (long) pingIntervalMillis, clientStreams);
                        streamAllocation.connection().socket().setSoTimeout(0);
                        RealWebSocket.this.loopReader();
                    } catch (Exception e) {
                        RealWebSocket.this.failWebSocket(e, (Response) null);
                    }
                } catch (ProtocolException e2) {
                    RealWebSocket.this.failWebSocket(e2, response);
                    Util.closeQuietly((Closeable) response);
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r3.listener.onFailure(r3, r4, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0031, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0032, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0035, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void failWebSocket(java.lang.Exception r4, okhttp3.Response r5) {
        /*
            r3 = this;
            monitor-enter(r3)
            boolean r0 = r3.failed     // Catch:{ all -> 0x0028 }
            if (r0 != 0) goto L_0x001f
            r0 = 1
            r3.failed = r0     // Catch:{ all -> 0x0028 }
            okhttp3.internal.ws.RealWebSocket$Streams r1 = r3.streams     // Catch:{ all -> 0x0028 }
            r0 = 0
            r3.streams = r0     // Catch:{ all -> 0x0028 }
            java.util.concurrent.ScheduledFuture<?> r0 = r3.cancelFuture     // Catch:{ all -> 0x0028 }
            if (r0 != 0) goto L_0x0021
        L_0x0011:
            java.util.concurrent.ScheduledExecutorService r0 = r3.executor     // Catch:{ all -> 0x0028 }
            if (r0 != 0) goto L_0x002b
        L_0x0015:
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            okhttp3.WebSocketListener r0 = r3.listener     // Catch:{ all -> 0x0031 }
            r0.onFailure(r3, r4, r5)     // Catch:{ all -> 0x0031 }
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r1)
            return
        L_0x001f:
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            return
        L_0x0021:
            java.util.concurrent.ScheduledFuture<?> r0 = r3.cancelFuture     // Catch:{ all -> 0x0028 }
            r2 = 0
            r0.cancel(r2)     // Catch:{ all -> 0x0028 }
            goto L_0x0011
        L_0x0028:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0028 }
            throw r0
        L_0x002b:
            java.util.concurrent.ScheduledExecutorService r0 = r3.executor     // Catch:{ all -> 0x0028 }
            r0.shutdown()     // Catch:{ all -> 0x0028 }
            goto L_0x0015
        L_0x0031:
            r0 = move-exception
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.failWebSocket(java.lang.Exception, okhttp3.Response):void");
    }

    public void initReaderAndWriter(String str, long j, Streams streams2) throws IOException {
        synchronized (this) {
            this.streams = streams2;
            this.writer = new WebSocketWriter(streams2.client, streams2.sink, this.random);
            this.executor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(str, false));
            if (j != 0) {
                this.executor.scheduleAtFixedRate(new PingRunnable(), j, j, TimeUnit.MILLISECONDS);
            }
            if (!this.messageAndCloseQueue.isEmpty()) {
                runWriter();
            }
        }
        this.reader = new WebSocketReader(streams2.client, streams2.source, this);
    }

    public void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            this.reader.processNextFrame();
        }
    }

    public void onReadClose(int i, String str) {
        Streams streams2;
        if (i != -1) {
            synchronized (this) {
                if (this.receivedCloseCode == -1) {
                    this.receivedCloseCode = i;
                    this.receivedCloseReason = str;
                    if (this.enqueuedClose) {
                        if (this.messageAndCloseQueue.isEmpty()) {
                            Streams streams3 = this.streams;
                            this.streams = null;
                            if (this.cancelFuture != null) {
                                this.cancelFuture.cancel(false);
                            }
                            this.executor.shutdown();
                            streams2 = streams3;
                        }
                    }
                    streams2 = null;
                } else {
                    throw new IllegalStateException("already closed");
                }
            }
            try {
                this.listener.onClosing(this, i, str);
                if (streams2 != null) {
                    this.listener.onClosed(this, i, str);
                }
            } finally {
                Util.closeQuietly((Closeable) streams2);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void onReadMessage(String str) throws IOException {
        this.listener.onMessage((WebSocket) this, str);
    }

    public void onReadMessage(ByteString byteString) throws IOException {
        this.listener.onMessage((WebSocket) this, byteString);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0021, code lost:
        if (r1.messageAndCloseQueue.isEmpty() == false) goto L_0x000b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0006, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onReadPing(okio.ByteString r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r1.failed     // Catch:{ all -> 0x0024 }
            if (r0 == 0) goto L_0x0007
        L_0x0005:
            monitor-exit(r1)
            return
        L_0x0007:
            boolean r0 = r1.enqueuedClose     // Catch:{ all -> 0x0024 }
            if (r0 != 0) goto L_0x001b
        L_0x000b:
            java.util.ArrayDeque<okio.ByteString> r0 = r1.pongQueue     // Catch:{ all -> 0x0024 }
            r0.add(r2)     // Catch:{ all -> 0x0024 }
            r1.runWriter()     // Catch:{ all -> 0x0024 }
            int r0 = r1.pingCount     // Catch:{ all -> 0x0024 }
            int r0 = r0 + 1
            r1.pingCount = r0     // Catch:{ all -> 0x0024 }
            monitor-exit(r1)
            return
        L_0x001b:
            java.util.ArrayDeque<java.lang.Object> r0 = r1.messageAndCloseQueue     // Catch:{ all -> 0x0024 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0024 }
            if (r0 != 0) goto L_0x0005
            goto L_0x000b
        L_0x0024:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.onReadPing(okio.ByteString):void");
    }

    public synchronized void onReadPong(ByteString byteString) {
        this.pongCount++;
    }

    /* access modifiers changed from: package-private */
    public synchronized int pingCount() {
        return this.pingCount;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001d, code lost:
        if (r2.messageAndCloseQueue.isEmpty() == false) goto L_0x000c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0007, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean pong(okio.ByteString r3) {
        /*
            r2 = this;
            r1 = 0
            monitor-enter(r2)
            boolean r0 = r2.failed     // Catch:{ all -> 0x0020 }
            if (r0 == 0) goto L_0x0008
        L_0x0006:
            monitor-exit(r2)
            return r1
        L_0x0008:
            boolean r0 = r2.enqueuedClose     // Catch:{ all -> 0x0020 }
            if (r0 != 0) goto L_0x0017
        L_0x000c:
            java.util.ArrayDeque<okio.ByteString> r0 = r2.pongQueue     // Catch:{ all -> 0x0020 }
            r0.add(r3)     // Catch:{ all -> 0x0020 }
            r2.runWriter()     // Catch:{ all -> 0x0020 }
            r0 = 1
            monitor-exit(r2)
            return r0
        L_0x0017:
            java.util.ArrayDeque<java.lang.Object> r0 = r2.messageAndCloseQueue     // Catch:{ all -> 0x0020 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0020 }
            if (r0 != 0) goto L_0x0006
            goto L_0x000c
        L_0x0020:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.pong(okio.ByteString):boolean");
    }

    /* access modifiers changed from: package-private */
    public synchronized int pongCount() {
        return this.pongCount;
    }

    /* access modifiers changed from: package-private */
    public boolean processNextFrame() throws IOException {
        try {
            this.reader.processNextFrame();
            return this.receivedCloseCode == -1;
        } catch (Exception e) {
            failWebSocket(e, (Response) null);
            return false;
        }
    }

    public synchronized long queueSize() {
        return this.queueSize;
    }

    public Request request() {
        return this.originalRequest;
    }

    public boolean send(String str) {
        if (str != null) {
            return send(ByteString.encodeUtf8(str), 1);
        }
        throw new NullPointerException("text == null");
    }

    public boolean send(ByteString byteString) {
        if (byteString != null) {
            return send(byteString, 2);
        }
        throw new NullPointerException("bytes == null");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001d, code lost:
        if ((r3 instanceof okhttp3.internal.ws.RealWebSocket.Message) != false) goto L_0x007c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0021, code lost:
        if ((r3 instanceof okhttp3.internal.ws.RealWebSocket.Close) != false) goto L_0x00aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        throw new java.lang.AssertionError();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002a, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002d, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r8.writePong(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0078, code lost:
        okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x007b, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        r2 = ((okhttp3.internal.ws.RealWebSocket.Message) r3).data;
        r3 = okio.Okio.buffer(r8.newMessageSink(((okhttp3.internal.ws.RealWebSocket.Message) r3).formatOpcode, (long) r2.size()));
        r3.write(r2);
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0099, code lost:
        monitor-enter(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        r15.queueSize -= (long) r2.size();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00a5, code lost:
        monitor-exit(r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00aa, code lost:
        r3 = (okhttp3.internal.ws.RealWebSocket.Close) r3;
        r8.writeClose(r3.code, r3.reason);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b3, code lost:
        if (r4 == null) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00b5, code lost:
        r15.listener.onClosed(r15, r6, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        if (r2 != null) goto L_0x0075;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean writeOneFrame() throws java.io.IOException {
        /*
            r15 = this;
            r11 = 1
            r3 = -1
            r7 = 0
            r5 = 0
            monitor-enter(r15)
            boolean r2 = r15.failed     // Catch:{ all -> 0x0072 }
            if (r2 != 0) goto L_0x002e
            okhttp3.internal.ws.WebSocketWriter r8 = r15.writer     // Catch:{ all -> 0x0072 }
            java.util.ArrayDeque<okio.ByteString> r2 = r15.pongQueue     // Catch:{ all -> 0x0072 }
            java.lang.Object r2 = r2.poll()     // Catch:{ all -> 0x0072 }
            okio.ByteString r2 = (okio.ByteString) r2     // Catch:{ all -> 0x0072 }
            if (r2 == 0) goto L_0x0030
            r4 = r5
            r6 = r3
            r3 = r5
        L_0x0018:
            monitor-exit(r15)     // Catch:{ all -> 0x0072 }
            if (r2 != 0) goto L_0x0075
            boolean r2 = r3 instanceof okhttp3.internal.ws.RealWebSocket.Message     // Catch:{ all -> 0x0029 }
            if (r2 != 0) goto L_0x007c
            boolean r2 = r3 instanceof okhttp3.internal.ws.RealWebSocket.Close     // Catch:{ all -> 0x0029 }
            if (r2 != 0) goto L_0x00aa
            java.lang.AssertionError r2 = new java.lang.AssertionError     // Catch:{ all -> 0x0029 }
            r2.<init>()     // Catch:{ all -> 0x0029 }
            throw r2     // Catch:{ all -> 0x0029 }
        L_0x0029:
            r2 = move-exception
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
            throw r2
        L_0x002e:
            monitor-exit(r15)     // Catch:{ all -> 0x0072 }
            return r7
        L_0x0030:
            java.util.ArrayDeque<java.lang.Object> r4 = r15.messageAndCloseQueue     // Catch:{ all -> 0x0072 }
            java.lang.Object r4 = r4.poll()     // Catch:{ all -> 0x0072 }
            boolean r6 = r4 instanceof okhttp3.internal.ws.RealWebSocket.Close     // Catch:{ all -> 0x0072 }
            if (r6 != 0) goto L_0x0040
            if (r4 == 0) goto L_0x0070
            r6 = r3
            r3 = r4
            r4 = r5
            goto L_0x0018
        L_0x0040:
            int r7 = r15.receivedCloseCode     // Catch:{ all -> 0x0072 }
            java.lang.String r6 = r15.receivedCloseReason     // Catch:{ all -> 0x0072 }
            if (r7 != r3) goto L_0x0060
            java.util.concurrent.ScheduledExecutorService r9 = r15.executor     // Catch:{ all -> 0x0072 }
            okhttp3.internal.ws.RealWebSocket$CancelRunnable r10 = new okhttp3.internal.ws.RealWebSocket$CancelRunnable     // Catch:{ all -> 0x0072 }
            r10.<init>()     // Catch:{ all -> 0x0072 }
            r0 = r4
            okhttp3.internal.ws.RealWebSocket$Close r0 = (okhttp3.internal.ws.RealWebSocket.Close) r0     // Catch:{ all -> 0x0072 }
            r3 = r0
            long r12 = r3.cancelAfterCloseMillis     // Catch:{ all -> 0x0072 }
            java.util.concurrent.TimeUnit r3 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ all -> 0x0072 }
            java.util.concurrent.ScheduledFuture r3 = r9.schedule(r10, r12, r3)     // Catch:{ all -> 0x0072 }
            r15.cancelFuture = r3     // Catch:{ all -> 0x0072 }
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            goto L_0x0018
        L_0x0060:
            okhttp3.internal.ws.RealWebSocket$Streams r3 = r15.streams     // Catch:{ all -> 0x0072 }
            r5 = 0
            r15.streams = r5     // Catch:{ all -> 0x0072 }
            java.util.concurrent.ScheduledExecutorService r5 = r15.executor     // Catch:{ all -> 0x0072 }
            r5.shutdown()     // Catch:{ all -> 0x0072 }
            r5 = r6
            r6 = r7
            r14 = r3
            r3 = r4
            r4 = r14
            goto L_0x0018
        L_0x0070:
            monitor-exit(r15)     // Catch:{ all -> 0x0072 }
            return r7
        L_0x0072:
            r2 = move-exception
            monitor-exit(r15)     // Catch:{ all -> 0x0072 }
            throw r2
        L_0x0075:
            r8.writePong(r2)     // Catch:{ all -> 0x0029 }
        L_0x0078:
            okhttp3.internal.Util.closeQuietly((java.io.Closeable) r4)
            return r11
        L_0x007c:
            r0 = r3
            okhttp3.internal.ws.RealWebSocket$Message r0 = (okhttp3.internal.ws.RealWebSocket.Message) r0     // Catch:{ all -> 0x0029 }
            r2 = r0
            okio.ByteString r2 = r2.data     // Catch:{ all -> 0x0029 }
            okhttp3.internal.ws.RealWebSocket$Message r3 = (okhttp3.internal.ws.RealWebSocket.Message) r3     // Catch:{ all -> 0x0029 }
            int r3 = r3.formatOpcode     // Catch:{ all -> 0x0029 }
            int r5 = r2.size()     // Catch:{ all -> 0x0029 }
            long r6 = (long) r5     // Catch:{ all -> 0x0029 }
            okio.Sink r3 = r8.newMessageSink(r3, r6)     // Catch:{ all -> 0x0029 }
            okio.BufferedSink r3 = okio.Okio.buffer((okio.Sink) r3)     // Catch:{ all -> 0x0029 }
            r3.write((okio.ByteString) r2)     // Catch:{ all -> 0x0029 }
            r3.close()     // Catch:{ all -> 0x0029 }
            monitor-enter(r15)     // Catch:{ all -> 0x0029 }
            long r6 = r15.queueSize     // Catch:{ all -> 0x00a7 }
            int r2 = r2.size()     // Catch:{ all -> 0x00a7 }
            long r2 = (long) r2     // Catch:{ all -> 0x00a7 }
            long r2 = r6 - r2
            r15.queueSize = r2     // Catch:{ all -> 0x00a7 }
            monitor-exit(r15)     // Catch:{ all -> 0x00a7 }
            goto L_0x0078
        L_0x00a7:
            r2 = move-exception
            monitor-exit(r15)     // Catch:{ all -> 0x00a7 }
            throw r2     // Catch:{ all -> 0x0029 }
        L_0x00aa:
            okhttp3.internal.ws.RealWebSocket$Close r3 = (okhttp3.internal.ws.RealWebSocket.Close) r3     // Catch:{ all -> 0x0029 }
            int r2 = r3.code     // Catch:{ all -> 0x0029 }
            okio.ByteString r3 = r3.reason     // Catch:{ all -> 0x0029 }
            r8.writeClose(r2, r3)     // Catch:{ all -> 0x0029 }
            if (r4 == 0) goto L_0x0078
            okhttp3.WebSocketListener r2 = r15.listener     // Catch:{ all -> 0x0029 }
            r2.onClosed(r15, r6, r5)     // Catch:{ all -> 0x0029 }
            goto L_0x0078
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.RealWebSocket.writeOneFrame():boolean");
    }
}
