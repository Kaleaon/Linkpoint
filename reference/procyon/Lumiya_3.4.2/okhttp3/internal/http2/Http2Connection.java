// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import okhttp3.internal.platform.Platform;
import okio.ByteString;
import okio.Okio;
import java.net.InetSocketAddress;
import okio.BufferedSink;
import java.io.InterruptedIOException;
import okhttp3.internal.NamedRunnable;
import okio.Buffer;
import okio.BufferedSource;
import okhttp3.Protocol;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.internal.Util;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.io.Closeable;

public final class Http2Connection implements Closeable
{
    private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
    static final ExecutorService executor;
    long bytesLeftInWriteWindow;
    final boolean client;
    final Set<Integer> currentPushRequests;
    final String hostname;
    int lastGoodStreamId;
    final Listener listener;
    private int nextPingId;
    int nextStreamId;
    Settings okHttpSettings;
    final Settings peerSettings;
    private Map<Integer, Ping> pings;
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    final ReaderRunnable readerRunnable;
    boolean receivedInitialPeerSettings;
    boolean shutdown;
    final Socket socket;
    final Map<Integer, Http2Stream> streams;
    long unacknowledgedBytesRead;
    final Http2Writer writer;
    
    static {
        executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp FramedConnection", true));
    }
    
    Http2Connection(final Builder builder) {
        final int n = 2;
        this.streams = new LinkedHashMap<Integer, Http2Stream>();
        this.unacknowledgedBytesRead = 0L;
        this.okHttpSettings = new Settings();
        this.peerSettings = new Settings();
        this.receivedInitialPeerSettings = false;
        this.currentPushRequests = new LinkedHashSet<Integer>();
        this.pushObserver = builder.pushObserver;
        this.client = builder.client;
        this.listener = builder.listener;
        int nextStreamId;
        if (!builder.client) {
            nextStreamId = 2;
        }
        else {
            nextStreamId = 1;
        }
        this.nextStreamId = nextStreamId;
        if (builder.client) {
            this.nextStreamId += 2;
        }
        int nextPingId;
        if (!builder.client) {
            nextPingId = n;
        }
        else {
            nextPingId = 1;
        }
        this.nextPingId = nextPingId;
        if (builder.client) {
            this.okHttpSettings.set(7, 16777216);
        }
        this.hostname = builder.hostname;
        this.pushExecutor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Util.threadFactory(Util.format("OkHttp %s Push Observer", this.hostname), true));
        this.peerSettings.set(7, 65535);
        this.peerSettings.set(5, 16384);
        this.bytesLeftInWriteWindow = this.peerSettings.getInitialWindowSize();
        this.socket = builder.socket;
        this.writer = new Http2Writer(builder.sink, this.client);
        this.readerRunnable = new ReaderRunnable(new Http2Reader(builder.source, this.client));
    }
    
    private Http2Stream newStream(final int n, final List<Header> list, final boolean b) throws IOException {
        boolean b2 = false;
        Label_0111: {
            if (!b) {
                break Label_0111;
            }
            boolean b3 = false;
            int nextStreamId = 0;
            Http2Stream http2Stream = null;
            Label_0068_Outer:Label_0076_Outer:Label_0100_Outer:
            while (true) {
            Label_0208:
                while (true) {
                Label_0180:
                    while (true) {
                    Label_0160:
                        while (true) {
                            while (true) {
                                Label_0138: {
                                    synchronized (this.writer) {
                                        synchronized (this) {
                                            if (this.shutdown) {
                                                throw new ConnectionShutdownException();
                                            }
                                            nextStreamId = this.nextStreamId;
                                            this.nextStreamId += 2;
                                            http2Stream = new Http2Stream(nextStreamId, this, b3, false, list);
                                            if (b) {
                                                break Label_0138;
                                            }
                                            b2 = true;
                                            if (http2Stream.isOpen()) {
                                                break Label_0160;
                                            }
                                            monitorexit(this);
                                            if (n == 0) {
                                                break Label_0180;
                                            }
                                            if (this.client) {
                                                break;
                                            }
                                            this.writer.pushPromise(n, nextStreamId, list);
                                            monitorexit(this.writer);
                                            if (!b2) {
                                                return http2Stream;
                                            }
                                            break Label_0208;
                                            b3 = true;
                                        }
                                    }
                                }
                                if (this.bytesLeftInWriteWindow == 0L) {
                                    continue Label_0068_Outer;
                                }
                                if (http2Stream.bytesLeftInWriteWindow == 0L) {
                                    continue Label_0068_Outer;
                                }
                                break;
                            }
                            continue Label_0076_Outer;
                        }
                        this.streams.put(nextStreamId, http2Stream);
                        continue Label_0100_Outer;
                    }
                    this.writer.synStream(b3, nextStreamId, n, list);
                    continue;
                }
                this.writer.flush();
                return http2Stream;
            }
        }
        throw new IllegalArgumentException("client streams shouldn't have associated stream IDs");
    }
    
    void addBytesToWriteWindow(final long n) {
        this.bytesLeftInWriteWindow += n;
        int n2;
        if (n <= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            this.notifyAll();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }
    
    void close(final ErrorCode p0, final ErrorCode p1) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     6: aload_0        
        //     7: aload_1        
        //     8: invokevirtual   okhttp3/internal/http2/Http2Connection.shutdown:(Lokhttp3/internal/http2/ErrorCode;)V
        //    11: aconst_null    
        //    12: astore_1       
        //    13: aload_0        
        //    14: monitorenter   
        //    15: aload_0        
        //    16: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
        //    19: invokeinterface java/util/Map.isEmpty:()Z
        //    24: ifeq            92
        //    27: aconst_null    
        //    28: astore_3       
        //    29: aload_0        
        //    30: getfield        okhttp3/internal/http2/Http2Connection.pings:Ljava/util/Map;
        //    33: ifnonnull       134
        //    36: aconst_null    
        //    37: astore          4
        //    39: aload_0        
        //    40: monitorexit    
        //    41: aload_3        
        //    42: ifnonnull       178
        //    45: aload           4
        //    47: ifnonnull       235
        //    50: aload_0        
        //    51: getfield        okhttp3/internal/http2/Http2Connection.writer:Lokhttp3/internal/http2/Http2Writer;
        //    54: invokevirtual   okhttp3/internal/http2/Http2Writer.close:()V
        //    57: aload_1        
        //    58: astore_2       
        //    59: aload_0        
        //    60: getfield        okhttp3/internal/http2/Http2Connection.socket:Ljava/net/Socket;
        //    63: invokevirtual   java/net/Socket.close:()V
        //    66: aload_2        
        //    67: astore_1       
        //    68: aload_1        
        //    69: ifnonnull       274
        //    72: return         
        //    73: aload_0        
        //    74: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
        //    77: ifeq            6
        //    80: new             Ljava/lang/AssertionError;
        //    83: dup            
        //    84: invokespecial   java/lang/AssertionError.<init>:()V
        //    87: athrow         
        //    88: astore_1       
        //    89: goto            13
        //    92: aload_0        
        //    93: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
        //    96: invokeinterface java/util/Map.values:()Ljava/util/Collection;
        //   101: aload_0        
        //   102: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
        //   105: invokeinterface java/util/Map.size:()I
        //   110: anewarray       Lokhttp3/internal/http2/Http2Stream;
        //   113: invokeinterface java/util/Collection.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   118: checkcast       [Lokhttp3/internal/http2/Http2Stream;
        //   121: astore_3       
        //   122: aload_0        
        //   123: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
        //   126: invokeinterface java/util/Map.clear:()V
        //   131: goto            29
        //   134: aload_0        
        //   135: getfield        okhttp3/internal/http2/Http2Connection.pings:Ljava/util/Map;
        //   138: invokeinterface java/util/Map.values:()Ljava/util/Collection;
        //   143: aload_0        
        //   144: getfield        okhttp3/internal/http2/Http2Connection.pings:Ljava/util/Map;
        //   147: invokeinterface java/util/Map.size:()I
        //   152: anewarray       Lokhttp3/internal/http2/Ping;
        //   155: invokeinterface java/util/Collection.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   160: checkcast       [Lokhttp3/internal/http2/Ping;
        //   163: astore          4
        //   165: aload_0        
        //   166: aconst_null    
        //   167: putfield        okhttp3/internal/http2/Http2Connection.pings:Ljava/util/Map;
        //   170: goto            39
        //   173: astore_1       
        //   174: aload_0        
        //   175: monitorexit    
        //   176: aload_1        
        //   177: athrow         
        //   178: aload_3        
        //   179: arraylength    
        //   180: istore          5
        //   182: iconst_0       
        //   183: istore          6
        //   185: iload           6
        //   187: iload           5
        //   189: if_icmplt       195
        //   192: goto            45
        //   195: aload_3        
        //   196: iload           6
        //   198: aaload         
        //   199: astore          7
        //   201: aload           7
        //   203: aload_2        
        //   204: invokevirtual   okhttp3/internal/http2/Http2Stream.close:(Lokhttp3/internal/http2/ErrorCode;)V
        //   207: aload_1        
        //   208: astore          7
        //   210: iinc            6, 1
        //   213: aload           7
        //   215: astore_1       
        //   216: goto            185
        //   219: astore          8
        //   221: aload_1        
        //   222: astore          7
        //   224: aload_1        
        //   225: ifnull          210
        //   228: aload           8
        //   230: astore          7
        //   232: goto            210
        //   235: aload           4
        //   237: arraylength    
        //   238: istore          5
        //   240: iconst_0       
        //   241: istore          6
        //   243: iload           6
        //   245: iload           5
        //   247: if_icmpge       50
        //   250: aload           4
        //   252: iload           6
        //   254: aaload         
        //   255: invokevirtual   okhttp3/internal/http2/Ping.cancel:()V
        //   258: iinc            6, 1
        //   261: goto            243
        //   264: astore_2       
        //   265: aload_1        
        //   266: ifnull          59
        //   269: aload_1        
        //   270: astore_2       
        //   271: goto            59
        //   274: aload_1        
        //   275: athrow         
        //   276: astore_1       
        //   277: goto            68
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  6      11     88     92     Ljava/io/IOException;
        //  15     27     173    178    Any
        //  29     36     173    178    Any
        //  39     41     173    178    Any
        //  50     57     264    274    Ljava/io/IOException;
        //  59     66     276    280    Ljava/io/IOException;
        //  92     131    173    178    Any
        //  134    170    173    178    Any
        //  174    176    173    178    Any
        //  201    207    219    235    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 135 out of bounds for length 135
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void flush() throws IOException {
        this.writer.flush();
    }
    
    public Protocol getProtocol() {
        return Protocol.HTTP_2;
    }
    
    Http2Stream getStream(final int i) {
        synchronized (this) {
            return this.streams.get(i);
        }
    }
    
    public boolean isShutdown() {
        synchronized (this) {
            return this.shutdown;
        }
    }
    
    public int maxConcurrentStreams() {
        synchronized (this) {
            return this.peerSettings.getMaxConcurrentStreams(Integer.MAX_VALUE);
        }
    }
    
    public Http2Stream newStream(final List<Header> list, final boolean b) throws IOException {
        return this.newStream(0, list, b);
    }
    
    public int openStreamCount() {
        synchronized (this) {
            return this.streams.size();
        }
    }
    
    public Ping ping() throws IOException {
        while (true) {
            final Ping ping = new Ping();
            while (true) {
                synchronized (this) {
                    if (this.shutdown) {
                        throw new ConnectionShutdownException();
                    }
                    final int nextPingId = this.nextPingId;
                    this.nextPingId += 2;
                    if (this.pings != null) {
                        this.pings.put(nextPingId, ping);
                        monitorexit(this);
                        this.writePing(false, nextPingId, 1330343787, ping);
                        return ping;
                    }
                }
                this.pings = new LinkedHashMap<Integer, Ping>();
                continue;
            }
        }
    }
    
    void pushDataLater(final int i, final BufferedSource bufferedSource, final int j, final boolean b) throws IOException {
        final Buffer buffer = new Buffer();
        bufferedSource.require(j);
        bufferedSource.read(buffer, j);
        if (buffer.size() != j) {
            throw new IOException(buffer.size() + " != " + j);
        }
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Data[%s]", new Object[] { this.hostname, i }) {
            public void execute() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: getfield        okhttp3/internal/http2/Http2Connection$6.this$0:Lokhttp3/internal/http2/Http2Connection;
                //     4: getfield        okhttp3/internal/http2/Http2Connection.pushObserver:Lokhttp3/internal/http2/PushObserver;
                //     7: aload_0        
                //     8: getfield        okhttp3/internal/http2/Http2Connection$6.val$streamId:I
                //    11: aload_0        
                //    12: getfield        okhttp3/internal/http2/Http2Connection$6.val$buffer:Lokio/Buffer;
                //    15: aload_0        
                //    16: getfield        okhttp3/internal/http2/Http2Connection$6.val$byteCount:I
                //    19: aload_0        
                //    20: getfield        okhttp3/internal/http2/Http2Connection$6.val$inFinished:Z
                //    23: invokeinterface okhttp3/internal/http2/PushObserver.onData:(ILokio/BufferedSource;IZ)Z
                //    28: istore_1       
                //    29: iload_1        
                //    30: ifne            67
                //    33: iload_1        
                //    34: ifeq            91
                //    37: aload_0        
                //    38: getfield        okhttp3/internal/http2/Http2Connection$6.this$0:Lokhttp3/internal/http2/Http2Connection;
                //    41: astore_2       
                //    42: aload_2        
                //    43: monitorenter   
                //    44: aload_0        
                //    45: getfield        okhttp3/internal/http2/Http2Connection$6.this$0:Lokhttp3/internal/http2/Http2Connection;
                //    48: getfield        okhttp3/internal/http2/Http2Connection.currentPushRequests:Ljava/util/Set;
                //    51: aload_0        
                //    52: getfield        okhttp3/internal/http2/Http2Connection$6.val$streamId:I
                //    55: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
                //    58: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
                //    63: pop            
                //    64: aload_2        
                //    65: monitorexit    
                //    66: return         
                //    67: aload_0        
                //    68: getfield        okhttp3/internal/http2/Http2Connection$6.this$0:Lokhttp3/internal/http2/Http2Connection;
                //    71: getfield        okhttp3/internal/http2/Http2Connection.writer:Lokhttp3/internal/http2/Http2Writer;
                //    74: aload_0        
                //    75: getfield        okhttp3/internal/http2/Http2Connection$6.val$streamId:I
                //    78: getstatic       okhttp3/internal/http2/ErrorCode.CANCEL:Lokhttp3/internal/http2/ErrorCode;
                //    81: invokevirtual   okhttp3/internal/http2/Http2Writer.rstStream:(ILokhttp3/internal/http2/ErrorCode;)V
                //    84: goto            33
                //    87: astore_3       
                //    88: goto            66
                //    91: aload_0        
                //    92: getfield        okhttp3/internal/http2/Http2Connection$6.val$inFinished:Z
                //    95: istore_1       
                //    96: iload_1        
                //    97: ifne            37
                //   100: goto            66
                //   103: astore_3       
                //   104: aload_2        
                //   105: monitorexit    
                //   106: aload_3        
                //   107: athrow         
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                 
                //  -----  -----  -----  -----  ---------------------
                //  0      29     87     91     Ljava/io/IOException;
                //  37     44     87     91     Ljava/io/IOException;
                //  44     66     103    108    Any
                //  67     84     87     91     Ljava/io/IOException;
                //  91     96     87     91     Ljava/io/IOException;
                //  104    106    103    108    Any
                //  106    108    87     91     Ljava/io/IOException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0066:
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
        });
    }
    
    void pushHeadersLater(final int i, final List<Header> list, final boolean b) {
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[] { this.hostname, i }) {
            public void execute() {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: getfield        okhttp3/internal/http2/Http2Connection$5.this$0:Lokhttp3/internal/http2/Http2Connection;
                //     4: getfield        okhttp3/internal/http2/Http2Connection.pushObserver:Lokhttp3/internal/http2/PushObserver;
                //     7: aload_0        
                //     8: getfield        okhttp3/internal/http2/Http2Connection$5.val$streamId:I
                //    11: aload_0        
                //    12: getfield        okhttp3/internal/http2/Http2Connection$5.val$requestHeaders:Ljava/util/List;
                //    15: aload_0        
                //    16: getfield        okhttp3/internal/http2/Http2Connection$5.val$inFinished:Z
                //    19: invokeinterface okhttp3/internal/http2/PushObserver.onHeaders:(ILjava/util/List;Z)Z
                //    24: istore_1       
                //    25: iload_1        
                //    26: ifne            63
                //    29: iload_1        
                //    30: ifeq            87
                //    33: aload_0        
                //    34: getfield        okhttp3/internal/http2/Http2Connection$5.this$0:Lokhttp3/internal/http2/Http2Connection;
                //    37: astore_2       
                //    38: aload_2        
                //    39: monitorenter   
                //    40: aload_0        
                //    41: getfield        okhttp3/internal/http2/Http2Connection$5.this$0:Lokhttp3/internal/http2/Http2Connection;
                //    44: getfield        okhttp3/internal/http2/Http2Connection.currentPushRequests:Ljava/util/Set;
                //    47: aload_0        
                //    48: getfield        okhttp3/internal/http2/Http2Connection$5.val$streamId:I
                //    51: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
                //    54: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
                //    59: pop            
                //    60: aload_2        
                //    61: monitorexit    
                //    62: return         
                //    63: aload_0        
                //    64: getfield        okhttp3/internal/http2/Http2Connection$5.this$0:Lokhttp3/internal/http2/Http2Connection;
                //    67: getfield        okhttp3/internal/http2/Http2Connection.writer:Lokhttp3/internal/http2/Http2Writer;
                //    70: aload_0        
                //    71: getfield        okhttp3/internal/http2/Http2Connection$5.val$streamId:I
                //    74: getstatic       okhttp3/internal/http2/ErrorCode.CANCEL:Lokhttp3/internal/http2/ErrorCode;
                //    77: invokevirtual   okhttp3/internal/http2/Http2Writer.rstStream:(ILokhttp3/internal/http2/ErrorCode;)V
                //    80: goto            29
                //    83: astore_3       
                //    84: goto            62
                //    87: aload_0        
                //    88: getfield        okhttp3/internal/http2/Http2Connection$5.val$inFinished:Z
                //    91: istore_1       
                //    92: iload_1        
                //    93: ifne            33
                //    96: goto            62
                //    99: astore_3       
                //   100: aload_2        
                //   101: monitorexit    
                //   102: aload_3        
                //   103: athrow         
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                 
                //  -----  -----  -----  -----  ---------------------
                //  33     40     83     87     Ljava/io/IOException;
                //  40     62     99     104    Any
                //  63     80     83     87     Ljava/io/IOException;
                //  87     92     83     87     Ljava/io/IOException;
                //  100    102    99     104    Any
                //  102    104    83     87     Ljava/io/IOException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0062:
                //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
                //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
        });
    }
    
    void pushRequestLater(final int i, final List<Header> list) {
        synchronized (this) {
            if (!this.currentPushRequests.contains(i)) {
                this.currentPushRequests.add(i);
                monitorexit(this);
                this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Request[%s]", new Object[] { this.hostname, i }) {
                    public void execute() {
                        if (Http2Connection.this.pushObserver.onRequest(i, list)) {
                            try {
                                Http2Connection.this.writer.rstStream(i, ErrorCode.CANCEL);
                                synchronized (Http2Connection.this) {
                                    Http2Connection.this.currentPushRequests.remove(i);
                                }
                            }
                            catch (final IOException ex) {}
                        }
                    }
                });
                return;
            }
            this.writeSynResetLater(i, ErrorCode.PROTOCOL_ERROR);
        }
    }
    
    void pushResetLater(final int i, final ErrorCode errorCode) {
        this.pushExecutor.execute(new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[] { this.hostname, i }) {
            public void execute() {
                Http2Connection.this.pushObserver.onReset(i, errorCode);
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(i);
                }
            }
        });
    }
    
    public Http2Stream pushStream(final int n, final List<Header> list, final boolean b) throws IOException {
        if (!this.client) {
            return this.newStream(n, list, b);
        }
        throw new IllegalStateException("Client cannot push requests.");
    }
    
    boolean pushedStream(final int n) {
        boolean b = false;
        if (n != 0 && (n & 0x1) == 0x0) {
            b = true;
        }
        return b;
    }
    
    Ping removePing(final int i) {
        Ping ping = null;
        synchronized (this) {
            if (this.pings != null) {
                ping = this.pings.remove(i);
            }
            return ping;
        }
    }
    
    Http2Stream removeStream(final int i) {
        synchronized (this) {
            final Http2Stream http2Stream = this.streams.remove(i);
            this.notifyAll();
            return http2Stream;
        }
    }
    
    public void setSettings(final Settings settings) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (!this.shutdown) {
                    this.okHttpSettings.merge(settings);
                    this.writer.settings(settings);
                    return;
                }
                throw new ConnectionShutdownException();
            }
        }
    }
    
    public void shutdown(final ErrorCode errorCode) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (!this.shutdown) {
                    this.shutdown = true;
                    final int lastGoodStreamId = this.lastGoodStreamId;
                    monitorexit(this);
                    this.writer.goAway(lastGoodStreamId, errorCode, Util.EMPTY_BYTE_ARRAY);
                }
            }
        }
    }
    
    public void start() throws IOException {
        this.start(true);
    }
    
    void start(final boolean b) throws IOException {
        if (b) {
            this.writer.connectionPreface();
            this.writer.settings(this.okHttpSettings);
            final int initialWindowSize = this.okHttpSettings.getInitialWindowSize();
            if (initialWindowSize != 65535) {
                this.writer.windowUpdate(0, initialWindowSize - 65535);
            }
        }
        new Thread(this.readerRunnable).start();
    }
    
    public void writeData(final int i, final boolean b, final Buffer buffer, final long n) throws IOException {
        long a = n;
        if (n == 0L) {
            this.writer.data(b, i, buffer, 0);
            return;
        }
    Label_0054_Outer:
        while (true) {
            while (true) {
                Label_0117: {
                    if (a > 0L) {
                        break Label_0117;
                    }
                    final int n2 = 1;
                    if (n2 == 0) {
                    Label_0142:
                        while (true) {
                            while (true) {
                                synchronized (this) {
                                    try {
                                        while (this.bytesLeftInWriteWindow > 0L) {
                                            final int n3 = 1;
                                            if (n3 != 0) {
                                                break Label_0142;
                                            }
                                            if (!this.streams.containsKey(i)) {
                                                throw new IOException("stream closed");
                                            }
                                            this.wait();
                                        }
                                    }
                                    catch (final InterruptedException ex) {
                                        throw new InterruptedIOException();
                                    }
                                }
                                break Label_0117;
                                final int n3 = 0;
                                continue;
                            }
                        }
                        final int min = Math.min((int)Math.min(a, this.bytesLeftInWriteWindow), this.writer.maxDataLength());
                        this.bytesLeftInWriteWindow -= min;
                        monitorexit(this);
                        a -= min;
                        final Buffer buffer2;
                        this.writer.data(b && a == 0L, i, buffer2, min);
                        continue Label_0054_Outer;
                    }
                    return;
                }
                final int n2 = 0;
                continue;
            }
        }
    }
    
    void writePing(final boolean b, final int n, final int n2, final Ping ping) throws IOException {
        final Http2Writer writer = this.writer;
        monitorenter(writer);
        Label_0028: {
            if (ping != null) {
                break Label_0028;
            }
            try {
                while (true) {
                    this.writer.ping(b, n, n2);
                    return;
                    ping.send();
                    continue;
                }
            }
            finally {
                monitorexit(writer);
            }
        }
    }
    
    void writePingLater(final boolean b, final int i, final int j, final Ping ping) {
        Http2Connection.executor.execute(new NamedRunnable("OkHttp %s ping %08x%08x", new Object[] { this.hostname, i, j }) {
            public void execute() {
                try {
                    Http2Connection.this.writePing(b, i, j, ping);
                }
                catch (final IOException ex) {}
            }
        });
    }
    
    void writeSynReply(final int n, final boolean b, final List<Header> list) throws IOException {
        this.writer.synReply(b, n, list);
    }
    
    void writeSynReset(final int n, final ErrorCode errorCode) throws IOException {
        this.writer.rstStream(n, errorCode);
    }
    
    void writeSynResetLater(final int i, final ErrorCode errorCode) {
        Http2Connection.executor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[] { this.hostname, i }) {
            public void execute() {
                try {
                    Http2Connection.this.writeSynReset(i, errorCode);
                }
                catch (final IOException ex) {}
            }
        });
    }
    
    void writeWindowUpdateLater(final int i, final long n) {
        Http2Connection.executor.execute(new NamedRunnable("OkHttp Window Update %s stream %d", new Object[] { this.hostname, i }) {
            public void execute() {
                try {
                    Http2Connection.this.writer.windowUpdate(i, n);
                }
                catch (final IOException ex) {}
            }
        });
    }
    
    public static class Builder
    {
        boolean client;
        String hostname;
        Listener listener;
        PushObserver pushObserver;
        BufferedSink sink;
        Socket socket;
        BufferedSource source;
        
        public Builder(final boolean client) {
            this.listener = Listener.REFUSE_INCOMING_STREAMS;
            this.pushObserver = PushObserver.CANCEL;
            this.client = client;
        }
        
        public Http2Connection build() throws IOException {
            return new Http2Connection(this);
        }
        
        public Builder listener(final Listener listener) {
            this.listener = listener;
            return this;
        }
        
        public Builder pushObserver(final PushObserver pushObserver) {
            this.pushObserver = pushObserver;
            return this;
        }
        
        public Builder socket(final Socket socket) throws IOException {
            return this.socket(socket, ((InetSocketAddress)socket.getRemoteSocketAddress()).getHostName(), Okio.buffer(Okio.source(socket)), Okio.buffer(Okio.sink(socket)));
        }
        
        public Builder socket(final Socket socket, final String hostname, final BufferedSource source, final BufferedSink sink) {
            this.socket = socket;
            this.hostname = hostname;
            this.source = source;
            this.sink = sink;
            return this;
        }
    }
    
    public abstract static class Listener
    {
        public static final Listener REFUSE_INCOMING_STREAMS;
        
        static {
            REFUSE_INCOMING_STREAMS = new Listener() {
                @Override
                public void onStream(final Http2Stream http2Stream) throws IOException {
                    http2Stream.close(ErrorCode.REFUSED_STREAM);
                }
            };
        }
        
        public void onSettings(final Http2Connection http2Connection) {
        }
        
        public abstract void onStream(final Http2Stream p0) throws IOException;
    }
    
    class ReaderRunnable extends NamedRunnable implements Handler
    {
        final Http2Reader reader;
        
        ReaderRunnable(final Http2Reader reader) {
            super("OkHttp %s", new Object[] { Http2Connection.this.hostname });
            this.reader = reader;
        }
        
        private void applyAndAckSettings(final Settings settings) {
            Http2Connection.executor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[] { Http2Connection.this.hostname }) {
                public void execute() {
                    try {
                        Http2Connection.this.writer.applyAndAckSettings(settings);
                    }
                    catch (final IOException ex) {}
                }
            });
        }
        
        @Override
        public void ackSettings() {
        }
        
        @Override
        public void alternateService(final int n, final String s, final ByteString byteString, final String s2, final int n2, final long n3) {
        }
        
        @Override
        public void data(final boolean b, final int n, final BufferedSource bufferedSource, final int n2) throws IOException {
            if (Http2Connection.this.pushedStream(n)) {
                Http2Connection.this.pushDataLater(n, bufferedSource, n2, b);
                return;
            }
            final Http2Stream stream = Http2Connection.this.getStream(n);
            if (stream != null) {
                stream.receiveData(bufferedSource, n2);
                if (b) {
                    stream.receiveFin();
                }
                return;
            }
            Http2Connection.this.writeSynResetLater(n, ErrorCode.PROTOCOL_ERROR);
            bufferedSource.skip(n2);
        }
        
        @Override
        protected void execute() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: astore_1       
            //     4: getstatic       okhttp3/internal/http2/ErrorCode.INTERNAL_ERROR:Lokhttp3/internal/http2/ErrorCode;
            //     7: astore_2       
            //     8: aload_1        
            //     9: astore_3       
            //    10: aload_1        
            //    11: astore          4
            //    13: aload_0        
            //    14: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    17: getfield        okhttp3/internal/http2/Http2Connection.client:Z
            //    20: ifeq            76
            //    23: aload_1        
            //    24: astore_3       
            //    25: aload_1        
            //    26: astore          4
            //    28: aload_0        
            //    29: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.reader:Lokhttp3/internal/http2/Http2Reader;
            //    32: aload_0        
            //    33: invokevirtual   okhttp3/internal/http2/Http2Reader.nextFrame:(Lokhttp3/internal/http2/Http2Reader$Handler;)Z
            //    36: ifne            23
            //    39: aload_1        
            //    40: astore_3       
            //    41: aload_1        
            //    42: astore          4
            //    44: getstatic       okhttp3/internal/http2/ErrorCode.NO_ERROR:Lokhttp3/internal/http2/ErrorCode;
            //    47: astore_1       
            //    48: aload_1        
            //    49: astore_3       
            //    50: aload_1        
            //    51: astore          4
            //    53: getstatic       okhttp3/internal/http2/ErrorCode.CANCEL:Lokhttp3/internal/http2/ErrorCode;
            //    56: astore          5
            //    58: aload_0        
            //    59: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    62: aload_1        
            //    63: aload           5
            //    65: invokevirtual   okhttp3/internal/http2/Http2Connection.close:(Lokhttp3/internal/http2/ErrorCode;Lokhttp3/internal/http2/ErrorCode;)V
            //    68: aload_0        
            //    69: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.reader:Lokhttp3/internal/http2/Http2Reader;
            //    72: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
            //    75: return         
            //    76: aload_1        
            //    77: astore_3       
            //    78: aload_1        
            //    79: astore          4
            //    81: aload_0        
            //    82: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.reader:Lokhttp3/internal/http2/Http2Reader;
            //    85: invokevirtual   okhttp3/internal/http2/Http2Reader.readConnectionPreface:()V
            //    88: goto            23
            //    91: astore          4
            //    93: aload_3        
            //    94: astore          4
            //    96: getstatic       okhttp3/internal/http2/ErrorCode.PROTOCOL_ERROR:Lokhttp3/internal/http2/ErrorCode;
            //    99: astore_3       
            //   100: getstatic       okhttp3/internal/http2/ErrorCode.PROTOCOL_ERROR:Lokhttp3/internal/http2/ErrorCode;
            //   103: astore          4
            //   105: aload_0        
            //   106: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   109: aload_3        
            //   110: aload           4
            //   112: invokevirtual   okhttp3/internal/http2/Http2Connection.close:(Lokhttp3/internal/http2/ErrorCode;Lokhttp3/internal/http2/ErrorCode;)V
            //   115: aload_0        
            //   116: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.reader:Lokhttp3/internal/http2/Http2Reader;
            //   119: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
            //   122: goto            75
            //   125: astore_1       
            //   126: aload           4
            //   128: astore_3       
            //   129: aload_1        
            //   130: astore          4
            //   132: aload_0        
            //   133: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   136: aload_3        
            //   137: aload_2        
            //   138: invokevirtual   okhttp3/internal/http2/Http2Connection.close:(Lokhttp3/internal/http2/ErrorCode;Lokhttp3/internal/http2/ErrorCode;)V
            //   141: aload_0        
            //   142: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.reader:Lokhttp3/internal/http2/Http2Reader;
            //   145: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
            //   148: aload           4
            //   150: athrow         
            //   151: astore_3       
            //   152: goto            141
            //   155: astore          4
            //   157: goto            132
            //   160: astore          4
            //   162: goto            115
            //   165: astore          4
            //   167: goto            68
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  13     23     91     165    Ljava/io/IOException;
            //  13     23     125    132    Any
            //  28     39     91     165    Ljava/io/IOException;
            //  28     39     125    132    Any
            //  44     48     91     165    Ljava/io/IOException;
            //  44     48     125    132    Any
            //  53     58     91     165    Ljava/io/IOException;
            //  53     58     125    132    Any
            //  58     68     165    170    Ljava/io/IOException;
            //  81     88     91     165    Ljava/io/IOException;
            //  81     88     125    132    Any
            //  96     100    125    132    Any
            //  100    105    155    160    Any
            //  105    115    160    165    Ljava/io/IOException;
            //  132    141    151    155    Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0068:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        @Override
        public void goAway(final int p0, final ErrorCode p1, final ByteString p2) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: istore          4
            //     3: aload_3        
            //     4: invokevirtual   okio/ByteString.size:()I
            //     7: pop            
            //     8: aload_0        
            //     9: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    12: astore_3       
            //    13: aload_3        
            //    14: monitorenter   
            //    15: aload_0        
            //    16: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    19: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
            //    22: invokeinterface java/util/Map.values:()Ljava/util/Collection;
            //    27: aload_0        
            //    28: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    31: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
            //    34: invokeinterface java/util/Map.size:()I
            //    39: anewarray       Lokhttp3/internal/http2/Http2Stream;
            //    42: invokeinterface java/util/Collection.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
            //    47: checkcast       [Lokhttp3/internal/http2/Http2Stream;
            //    50: astore_2       
            //    51: aload_0        
            //    52: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    55: iconst_1       
            //    56: putfield        okhttp3/internal/http2/Http2Connection.shutdown:Z
            //    59: aload_3        
            //    60: monitorexit    
            //    61: aload_2        
            //    62: arraylength    
            //    63: istore          5
            //    65: iload           4
            //    67: iload           5
            //    69: if_icmplt       78
            //    72: return         
            //    73: astore_2       
            //    74: aload_3        
            //    75: monitorexit    
            //    76: aload_2        
            //    77: athrow         
            //    78: aload_2        
            //    79: iload           4
            //    81: aaload         
            //    82: astore_3       
            //    83: aload_3        
            //    84: invokevirtual   okhttp3/internal/http2/Http2Stream.getId:()I
            //    87: iload_1        
            //    88: if_icmpgt       97
            //    91: iinc            4, 1
            //    94: goto            65
            //    97: aload_3        
            //    98: invokevirtual   okhttp3/internal/http2/Http2Stream.isLocallyInitiated:()Z
            //   101: ifeq            91
            //   104: aload_3        
            //   105: getstatic       okhttp3/internal/http2/ErrorCode.REFUSED_STREAM:Lokhttp3/internal/http2/ErrorCode;
            //   108: invokevirtual   okhttp3/internal/http2/Http2Stream.receiveRstStream:(Lokhttp3/internal/http2/ErrorCode;)V
            //   111: aload_0        
            //   112: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   115: aload_3        
            //   116: invokevirtual   okhttp3/internal/http2/Http2Stream.getId:()I
            //   119: invokevirtual   okhttp3/internal/http2/Http2Connection.removeStream:(I)Lokhttp3/internal/http2/Http2Stream;
            //   122: pop            
            //   123: goto            91
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  15     61     73     78     Any
            //  74     76     73     78     Any
            // 
            // The error that occurred was:
            // 
            // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
            //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
            //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
            //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
            //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        @Override
        public void headers(final boolean b, final int i, final int n, final List<Header> list) {
            Label_0060: {
                if (Http2Connection.this.pushedStream(i)) {
                    break Label_0060;
                }
                while (true) {
                    Http2Stream stream = null;
                    Label_0218: {
                        synchronized (Http2Connection.this) {
                            if (Http2Connection.this.shutdown) {
                                return;
                            }
                            stream = Http2Connection.this.getStream(i);
                            if (stream != null) {
                                monitorexit(Http2Connection.this);
                                stream.receiveHeaders(list);
                                if (!b) {
                                    return;
                                }
                                break Label_0218;
                            }
                            else {
                                if (i <= Http2Connection.this.lastGoodStreamId) {
                                    return;
                                }
                                if (i % 2 != Http2Connection.this.nextStreamId % 2) {
                                    stream = new Http2Stream(i, Http2Connection.this, false, b, list);
                                    Http2Connection.this.lastGoodStreamId = i;
                                    Http2Connection.this.streams.put(i, stream);
                                    Http2Connection.executor.execute(new NamedRunnable("OkHttp %s stream %d", new Object[] { Http2Connection.this.hostname, i }) {
                                        public void execute() {
                                            try {
                                                Http2Connection.this.listener.onStream(stream);
                                            }
                                            catch (final IOException ex) {
                                                Platform.get().log(4, "FramedConnection.Listener failure for " + Http2Connection.this.hostname, ex);
                                                try {
                                                    stream.close(ErrorCode.PROTOCOL_ERROR);
                                                }
                                                catch (final IOException ex2) {}
                                            }
                                        }
                                    });
                                }
                                return;
                            }
                            Http2Connection.this.pushHeadersLater(i, list, b);
                            return;
                        }
                    }
                    stream.receiveFin();
                }
            }
        }
        
        @Override
        public void ping(final boolean b, final int n, final int n2) {
            if (!b) {
                Http2Connection.this.writePingLater(true, n, n2, null);
            }
            else {
                final Ping removePing = Http2Connection.this.removePing(n);
                if (removePing != null) {
                    removePing.receive();
                }
            }
        }
        
        @Override
        public void priority(final int n, final int n2, final int n3, final boolean b) {
        }
        
        @Override
        public void pushPromise(final int n, final int n2, final List<Header> list) {
            Http2Connection.this.pushRequestLater(n2, list);
        }
        
        @Override
        public void rstStream(final int n, final ErrorCode errorCode) {
            if (!Http2Connection.this.pushedStream(n)) {
                final Http2Stream removeStream = Http2Connection.this.removeStream(n);
                if (removeStream != null) {
                    removeStream.receiveRstStream(errorCode);
                }
                return;
            }
            Http2Connection.this.pushResetLater(n, errorCode);
        }
        
        @Override
        public void settings(final boolean p0, final Settings p1) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //     4: astore_3       
            //     5: aload_3        
            //     6: monitorenter   
            //     7: aload_0        
            //     8: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    11: getfield        okhttp3/internal/http2/Http2Connection.peerSettings:Lokhttp3/internal/http2/Settings;
            //    14: invokevirtual   okhttp3/internal/http2/Settings.getInitialWindowSize:()I
            //    17: istore          4
            //    19: iload_1        
            //    20: ifne            111
            //    23: aload_0        
            //    24: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    27: getfield        okhttp3/internal/http2/Http2Connection.peerSettings:Lokhttp3/internal/http2/Settings;
            //    30: aload_2        
            //    31: invokevirtual   okhttp3/internal/http2/Settings.merge:(Lokhttp3/internal/http2/Settings;)V
            //    34: aload_0        
            //    35: aload_2        
            //    36: invokespecial   okhttp3/internal/http2/Http2Connection$ReaderRunnable.applyAndAckSettings:(Lokhttp3/internal/http2/Settings;)V
            //    39: aload_0        
            //    40: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    43: getfield        okhttp3/internal/http2/Http2Connection.peerSettings:Lokhttp3/internal/http2/Settings;
            //    46: invokevirtual   okhttp3/internal/http2/Settings.getInitialWindowSize:()I
            //    49: istore          5
            //    51: iload           5
            //    53: iconst_m1      
            //    54: if_icmpne       129
            //    57: aconst_null    
            //    58: astore_2       
            //    59: lconst_0       
            //    60: lstore          6
            //    62: getstatic       okhttp3/internal/http2/Http2Connection.executor:Ljava/util/concurrent/ExecutorService;
            //    65: astore          8
            //    67: new             Lokhttp3/internal/http2/Http2Connection$ReaderRunnable$2;
            //    70: astore          9
            //    72: aload           9
            //    74: aload_0        
            //    75: ldc_w           "OkHttp %s settings"
            //    78: iconst_1       
            //    79: anewarray       Ljava/lang/Object;
            //    82: dup            
            //    83: iconst_0       
            //    84: aload_0        
            //    85: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //    88: getfield        okhttp3/internal/http2/Http2Connection.hostname:Ljava/lang/String;
            //    91: aastore        
            //    92: invokespecial   okhttp3/internal/http2/Http2Connection$ReaderRunnable$2.<init>:(Lokhttp3/internal/http2/Http2Connection$ReaderRunnable;Ljava/lang/String;[Ljava/lang/Object;)V
            //    95: aload           8
            //    97: aload           9
            //    99: invokeinterface java/util/concurrent/ExecutorService.execute:(Ljava/lang/Runnable;)V
            //   104: aload_3        
            //   105: monitorexit    
            //   106: aload_2        
            //   107: ifnonnull       233
            //   110: return         
            //   111: aload_0        
            //   112: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   115: getfield        okhttp3/internal/http2/Http2Connection.peerSettings:Lokhttp3/internal/http2/Settings;
            //   118: invokevirtual   okhttp3/internal/http2/Settings.clear:()V
            //   121: goto            23
            //   124: astore_2       
            //   125: aload_3        
            //   126: monitorexit    
            //   127: aload_2        
            //   128: athrow         
            //   129: iload           5
            //   131: iload           4
            //   133: if_icmpeq       57
            //   136: iload           5
            //   138: iload           4
            //   140: isub           
            //   141: i2l            
            //   142: lstore          6
            //   144: aload_0        
            //   145: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   148: getfield        okhttp3/internal/http2/Http2Connection.receivedInitialPeerSettings:Z
            //   151: ifeq            174
            //   154: aload_0        
            //   155: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   158: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
            //   161: invokeinterface java/util/Map.isEmpty:()Z
            //   166: ifeq            194
            //   169: aconst_null    
            //   170: astore_2       
            //   171: goto            62
            //   174: aload_0        
            //   175: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   178: lload           6
            //   180: invokevirtual   okhttp3/internal/http2/Http2Connection.addBytesToWriteWindow:(J)V
            //   183: aload_0        
            //   184: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   187: iconst_1       
            //   188: putfield        okhttp3/internal/http2/Http2Connection.receivedInitialPeerSettings:Z
            //   191: goto            154
            //   194: aload_0        
            //   195: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   198: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
            //   201: invokeinterface java/util/Map.values:()Ljava/util/Collection;
            //   206: aload_0        
            //   207: getfield        okhttp3/internal/http2/Http2Connection$ReaderRunnable.this$0:Lokhttp3/internal/http2/Http2Connection;
            //   210: getfield        okhttp3/internal/http2/Http2Connection.streams:Ljava/util/Map;
            //   213: invokeinterface java/util/Map.size:()I
            //   218: anewarray       Lokhttp3/internal/http2/Http2Stream;
            //   221: invokeinterface java/util/Collection.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
            //   226: checkcast       [Lokhttp3/internal/http2/Http2Stream;
            //   229: astore_2       
            //   230: goto            62
            //   233: lload           6
            //   235: lconst_0       
            //   236: lcmp           
            //   237: ifeq            110
            //   240: aload_2        
            //   241: arraylength    
            //   242: istore          5
            //   244: iconst_0       
            //   245: istore          4
            //   247: iload           4
            //   249: iload           5
            //   251: if_icmpge       110
            //   254: aload_2        
            //   255: iload           4
            //   257: aaload         
            //   258: astore_3       
            //   259: aload_3        
            //   260: monitorenter   
            //   261: aload_3        
            //   262: lload           6
            //   264: invokevirtual   okhttp3/internal/http2/Http2Stream.addBytesToWriteWindow:(J)V
            //   267: aload_3        
            //   268: monitorexit    
            //   269: iinc            4, 1
            //   272: goto            247
            //   275: astore_2       
            //   276: aload_3        
            //   277: monitorexit    
            //   278: aload_2        
            //   279: athrow         
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  7      19     124    129    Any
            //  23     51     124    129    Any
            //  62     106    124    129    Any
            //  111    121    124    129    Any
            //  125    127    124    129    Any
            //  144    154    124    129    Any
            //  154    169    124    129    Any
            //  174    191    124    129    Any
            //  194    230    124    129    Any
            //  261    269    275    280    Any
            //  276    278    275    280    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.NullPointerException: Cannot invoke "com.strobel.assembler.metadata.TypeReference.getSimpleType()" because "type" is null
            //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
            //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
            //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
            //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        @Override
        public void windowUpdate(final int n, final long n2) {
            final Http2Stream stream;
            Label_0068: {
                if (n == 0) {
                    synchronized (Http2Connection.this) {
                        final Http2Connection this$0 = Http2Connection.this;
                        this$0.bytesLeftInWriteWindow += n2;
                        Http2Connection.this.notifyAll();
                        return;
                    }
                    break Label_0068;
                }
                stream = Http2Connection.this.getStream(n);
                if (stream != null) {
                    break Label_0068;
                }
                return;
            }
            synchronized (stream) {
                stream.addBytesToWriteWindow(n2);
            }
        }
    }
}
