// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.ws;

import okio.BufferedSource;
import okhttp3.internal.connection.StreamAllocation;
import okio.BufferedSink;
import java.io.Serializable;
import java.io.Closeable;
import okio.Okio;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import okhttp3.internal.Util;
import okhttp3.Callback;
import okhttp3.internal.Internal;
import okhttp3.OkHttpClient;
import java.net.ProtocolException;
import java.io.IOException;
import okhttp3.Response;
import java.util.Collections;
import java.util.Random;
import okio.ByteString;
import okhttp3.Request;
import java.util.ArrayDeque;
import okhttp3.WebSocketListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import okhttp3.Call;
import okhttp3.Protocol;
import java.util.List;
import okhttp3.WebSocket;

public final class RealWebSocket implements WebSocket, FrameCallback
{
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000L;
    private static final long MAX_QUEUE_SIZE = 16777216L;
    private static final List<Protocol> ONLY_HTTP1;
    private Call call;
    private ScheduledFuture<?> cancelFuture;
    private boolean enqueuedClose;
    private ScheduledExecutorService executor;
    private boolean failed;
    private final String key;
    final WebSocketListener listener;
    private final ArrayDeque<Object> messageAndCloseQueue;
    private final Request originalRequest;
    int pingCount;
    int pongCount;
    private final ArrayDeque<ByteString> pongQueue;
    private long queueSize;
    private final Random random;
    private WebSocketReader reader;
    private int receivedCloseCode;
    private String receivedCloseReason;
    private Streams streams;
    private WebSocketWriter writer;
    private final Runnable writerRunnable;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!RealWebSocket.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
        ONLY_HTTP1 = Collections.singletonList(Protocol.HTTP_1_1);
    }
    
    public RealWebSocket(final Request originalRequest, final WebSocketListener listener, final Random random) {
        this.pongQueue = new ArrayDeque<ByteString>();
        this.messageAndCloseQueue = new ArrayDeque<Object>();
        this.receivedCloseCode = -1;
        if ("GET".equals(originalRequest.method())) {
            this.originalRequest = originalRequest;
            this.listener = listener;
            this.random = random;
            final byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            this.key = ByteString.of(bytes).base64();
            this.writerRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (RealWebSocket.this.writeOneFrame()) {}
                    }
                    catch (final IOException ex) {
                        RealWebSocket.this.failWebSocket(ex, null);
                    }
                }
            };
            return;
        }
        throw new IllegalArgumentException("Request must be GET: " + originalRequest.method());
    }
    
    private void runWriter() {
        assert Thread.holdsLock(this);
        if (this.executor != null) {
            this.executor.execute(this.writerRunnable);
        }
    }
    
    private boolean send(final ByteString byteString, final int n) {
        synchronized (this) {
            if (this.failed || this.enqueuedClose) {
                return false;
            }
            int n2;
            if (this.queueSize + byteString.size() <= 16777216L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                this.close(1001, null);
                return false;
            }
            this.queueSize += byteString.size();
            this.messageAndCloseQueue.add(new Message(n, byteString));
            this.runWriter();
            return true;
        }
    }
    
    private void writePingFrame() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_0        
        //     3: getfield        okhttp3/internal/ws/RealWebSocket.failed:Z
        //     6: ifne            24
        //     9: aload_0        
        //    10: getfield        okhttp3/internal/ws/RealWebSocket.writer:Lokhttp3/internal/ws/WebSocketWriter;
        //    13: astore_1       
        //    14: aload_0        
        //    15: monitorexit    
        //    16: aload_1        
        //    17: getstatic       okio/ByteString.EMPTY:Lokio/ByteString;
        //    20: invokevirtual   okhttp3/internal/ws/WebSocketWriter.writePing:(Lokio/ByteString;)V
        //    23: return         
        //    24: aload_0        
        //    25: monitorexit    
        //    26: return         
        //    27: astore_1       
        //    28: aload_0        
        //    29: monitorexit    
        //    30: aload_1        
        //    31: athrow         
        //    32: astore_1       
        //    33: aload_0        
        //    34: aload_1        
        //    35: aconst_null    
        //    36: invokevirtual   okhttp3/internal/ws/RealWebSocket.failWebSocket:(Ljava/lang/Exception;Lokhttp3/Response;)V
        //    39: goto            23
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  2      16     27     32     Any
        //  16     23     32     42     Ljava/io/IOException;
        //  24     26     27     32     Any
        //  28     30     27     32     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0023:
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
    public void cancel() {
        this.call.cancel();
    }
    
    void checkResponse(final Response response) throws ProtocolException {
        if (response.code() != 101) {
            throw new ProtocolException("Expected HTTP 101 response but was '" + response.code() + " " + response.message() + "'");
        }
        final String header = response.header("Connection");
        if (!"Upgrade".equalsIgnoreCase(header)) {
            throw new ProtocolException("Expected 'Connection' header value 'Upgrade' but was '" + header + "'");
        }
        final String header2 = response.header("Upgrade");
        if (!"websocket".equalsIgnoreCase(header2)) {
            throw new ProtocolException("Expected 'Upgrade' header value 'websocket' but was '" + header2 + "'");
        }
        final String header3 = response.header("Sec-WebSocket-Accept");
        final String base64 = ByteString.encodeUtf8(this.key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
        if (base64.equals(header3)) {
            return;
        }
        throw new ProtocolException("Expected 'Sec-WebSocket-Accept' header value '" + base64 + "' but was '" + header3 + "'");
    }
    
    @Override
    public boolean close(final int n, final String s) {
        return this.close(n, s, 60000L);
    }
    
    boolean close(final int p0, final String p1, final long p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: aload_0        
        //     4: monitorenter   
        //     5: iload_1        
        //     6: invokestatic    okhttp3/internal/ws/WebSocketProtocol.validateCloseCode:(I)V
        //     9: aload_2        
        //    10: ifnonnull       28
        //    13: aload_0        
        //    14: getfield        okhttp3/internal/ws/RealWebSocket.failed:Z
        //    17: istore          6
        //    19: iload           6
        //    21: ifeq            104
        //    24: aload_0        
        //    25: monitorexit    
        //    26: iconst_0       
        //    27: ireturn        
        //    28: aload_2        
        //    29: invokestatic    okio/ByteString.encodeUtf8:(Ljava/lang/String;)Lokio/ByteString;
        //    32: astore          5
        //    34: aload           5
        //    36: invokevirtual   okio/ByteString.size:()I
        //    39: i2l            
        //    40: ldc2_w          123
        //    43: lcmp           
        //    44: ifgt            98
        //    47: iconst_1       
        //    48: istore          7
        //    50: iload           7
        //    52: ifne            13
        //    55: new             Ljava/lang/IllegalArgumentException;
        //    58: astore          5
        //    60: new             Ljava/lang/StringBuilder;
        //    63: astore          8
        //    65: aload           8
        //    67: invokespecial   java/lang/StringBuilder.<init>:()V
        //    70: aload           5
        //    72: aload           8
        //    74: ldc_w           "reason.size() > 123: "
        //    77: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    80: aload_2        
        //    81: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    84: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //    87: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;)V
        //    90: aload           5
        //    92: athrow         
        //    93: astore_2       
        //    94: aload_0        
        //    95: monitorexit    
        //    96: aload_2        
        //    97: athrow         
        //    98: iconst_0       
        //    99: istore          7
        //   101: goto            50
        //   104: aload_0        
        //   105: getfield        okhttp3/internal/ws/RealWebSocket.enqueuedClose:Z
        //   108: ifne            24
        //   111: aload_0        
        //   112: iconst_1       
        //   113: putfield        okhttp3/internal/ws/RealWebSocket.enqueuedClose:Z
        //   116: aload_0        
        //   117: getfield        okhttp3/internal/ws/RealWebSocket.messageAndCloseQueue:Ljava/util/ArrayDeque;
        //   120: astore          8
        //   122: new             Lokhttp3/internal/ws/RealWebSocket$Close;
        //   125: astore_2       
        //   126: aload_2        
        //   127: iload_1        
        //   128: aload           5
        //   130: lload_3        
        //   131: invokespecial   okhttp3/internal/ws/RealWebSocket$Close.<init>:(ILokio/ByteString;J)V
        //   134: aload           8
        //   136: aload_2        
        //   137: invokevirtual   java/util/ArrayDeque.add:(Ljava/lang/Object;)Z
        //   140: pop            
        //   141: aload_0        
        //   142: invokespecial   okhttp3/internal/ws/RealWebSocket.runWriter:()V
        //   145: aload_0        
        //   146: monitorexit    
        //   147: iconst_1       
        //   148: ireturn        
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  5      9      93     98     Any
        //  13     19     93     98     Any
        //  28     47     93     98     Any
        //  55     93     93     98     Any
        //  104    145    93     98     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: getfield:boolean(RealWebSocket::enqueuedClose, this:RealWebSocket)
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
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
    
    public void connect(final OkHttpClient okHttpClient) {
        final OkHttpClient build = okHttpClient.newBuilder().protocols(RealWebSocket.ONLY_HTTP1).build();
        final int pingIntervalMillis = build.pingIntervalMillis();
        final Request build2 = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").build();
        (this.call = Internal.instance.newWebSocketCall(build, build2)).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException ex) {
                RealWebSocket.this.failWebSocket(ex, null);
            }
            
            @Override
            public void onResponse(final Call p0, final Response p1) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     1: getfield        okhttp3/internal/ws/RealWebSocket$2.this$0:Lokhttp3/internal/ws/RealWebSocket;
                //     4: aload_2        
                //     5: invokevirtual   okhttp3/internal/ws/RealWebSocket.checkResponse:(Lokhttp3/Response;)V
                //     8: getstatic       okhttp3/internal/Internal.instance:Lokhttp3/internal/Internal;
                //    11: aload_1        
                //    12: invokevirtual   okhttp3/internal/Internal.streamAllocation:(Lokhttp3/Call;)Lokhttp3/internal/connection/StreamAllocation;
                //    15: astore_1       
                //    16: aload_1        
                //    17: invokevirtual   okhttp3/internal/connection/StreamAllocation.noNewStreams:()V
                //    20: new             Lokhttp3/internal/ws/RealWebSocket$ClientStreams;
                //    23: dup            
                //    24: aload_1        
                //    25: invokespecial   okhttp3/internal/ws/RealWebSocket$ClientStreams.<init>:(Lokhttp3/internal/connection/StreamAllocation;)V
                //    28: astore_3       
                //    29: aload_0        
                //    30: getfield        okhttp3/internal/ws/RealWebSocket$2.this$0:Lokhttp3/internal/ws/RealWebSocket;
                //    33: getfield        okhttp3/internal/ws/RealWebSocket.listener:Lokhttp3/WebSocketListener;
                //    36: aload_0        
                //    37: getfield        okhttp3/internal/ws/RealWebSocket$2.this$0:Lokhttp3/internal/ws/RealWebSocket;
                //    40: aload_2        
                //    41: invokevirtual   okhttp3/WebSocketListener.onOpen:(Lokhttp3/WebSocket;Lokhttp3/Response;)V
                //    44: new             Ljava/lang/StringBuilder;
                //    47: astore_2       
                //    48: aload_2        
                //    49: invokespecial   java/lang/StringBuilder.<init>:()V
                //    52: aload_2        
                //    53: ldc             "OkHttp WebSocket "
                //    55: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //    58: aload_0        
                //    59: getfield        okhttp3/internal/ws/RealWebSocket$2.val$request:Lokhttp3/Request;
                //    62: invokevirtual   okhttp3/Request.url:()Lokhttp3/HttpUrl;
                //    65: invokevirtual   okhttp3/HttpUrl.redact:()Ljava/lang/String;
                //    68: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                //    71: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                //    74: astore_2       
                //    75: aload_0        
                //    76: getfield        okhttp3/internal/ws/RealWebSocket$2.this$0:Lokhttp3/internal/ws/RealWebSocket;
                //    79: aload_2        
                //    80: aload_0        
                //    81: getfield        okhttp3/internal/ws/RealWebSocket$2.val$pingIntervalMillis:I
                //    84: i2l            
                //    85: aload_3        
                //    86: invokevirtual   okhttp3/internal/ws/RealWebSocket.initReaderAndWriter:(Ljava/lang/String;JLokhttp3/internal/ws/RealWebSocket$Streams;)V
                //    89: aload_1        
                //    90: invokevirtual   okhttp3/internal/connection/StreamAllocation.connection:()Lokhttp3/internal/connection/RealConnection;
                //    93: invokevirtual   okhttp3/internal/connection/RealConnection.socket:()Ljava/net/Socket;
                //    96: iconst_0       
                //    97: invokevirtual   java/net/Socket.setSoTimeout:(I)V
                //   100: aload_0        
                //   101: getfield        okhttp3/internal/ws/RealWebSocket$2.this$0:Lokhttp3/internal/ws/RealWebSocket;
                //   104: invokevirtual   okhttp3/internal/ws/RealWebSocket.loopReader:()V
                //   107: return         
                //   108: astore_1       
                //   109: aload_0        
                //   110: getfield        okhttp3/internal/ws/RealWebSocket$2.this$0:Lokhttp3/internal/ws/RealWebSocket;
                //   113: aload_1        
                //   114: aload_2        
                //   115: invokevirtual   okhttp3/internal/ws/RealWebSocket.failWebSocket:(Ljava/lang/Exception;Lokhttp3/Response;)V
                //   118: aload_2        
                //   119: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
                //   122: return         
                //   123: astore_1       
                //   124: aload_0        
                //   125: getfield        okhttp3/internal/ws/RealWebSocket$2.this$0:Lokhttp3/internal/ws/RealWebSocket;
                //   128: aload_1        
                //   129: aconst_null    
                //   130: invokevirtual   okhttp3/internal/ws/RealWebSocket.failWebSocket:(Ljava/lang/Exception;Lokhttp3/Response;)V
                //   133: goto            107
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                        
                //  -----  -----  -----  -----  ----------------------------
                //  0      8      108    123    Ljava/net/ProtocolException;
                //  29     107    123    136    Ljava/lang/Exception;
                // 
                // The error that occurred was:
                // 
                // java.lang.IllegalStateException: Expression is linked from several locations: Label_0107:
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
    
    void failWebSocket(final Exception p0, final Response p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: aload_0        
        //     3: getfield        okhttp3/internal/ws/RealWebSocket.failed:Z
        //     6: ifne            55
        //     9: aload_0        
        //    10: iconst_1       
        //    11: putfield        okhttp3/internal/ws/RealWebSocket.failed:Z
        //    14: aload_0        
        //    15: getfield        okhttp3/internal/ws/RealWebSocket.streams:Lokhttp3/internal/ws/RealWebSocket$Streams;
        //    18: astore_3       
        //    19: aload_0        
        //    20: aconst_null    
        //    21: putfield        okhttp3/internal/ws/RealWebSocket.streams:Lokhttp3/internal/ws/RealWebSocket$Streams;
        //    24: aload_0        
        //    25: getfield        okhttp3/internal/ws/RealWebSocket.cancelFuture:Ljava/util/concurrent/ScheduledFuture;
        //    28: ifnonnull       58
        //    31: aload_0        
        //    32: getfield        okhttp3/internal/ws/RealWebSocket.executor:Ljava/util/concurrent/ScheduledExecutorService;
        //    35: ifnonnull       77
        //    38: aload_0        
        //    39: monitorexit    
        //    40: aload_0        
        //    41: getfield        okhttp3/internal/ws/RealWebSocket.listener:Lokhttp3/WebSocketListener;
        //    44: aload_0        
        //    45: aload_1        
        //    46: aload_2        
        //    47: invokevirtual   okhttp3/WebSocketListener.onFailure:(Lokhttp3/WebSocket;Ljava/lang/Throwable;Lokhttp3/Response;)V
        //    50: aload_3        
        //    51: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //    54: return         
        //    55: aload_0        
        //    56: monitorexit    
        //    57: return         
        //    58: aload_0        
        //    59: getfield        okhttp3/internal/ws/RealWebSocket.cancelFuture:Ljava/util/concurrent/ScheduledFuture;
        //    62: iconst_0       
        //    63: invokeinterface java/util/concurrent/ScheduledFuture.cancel:(Z)Z
        //    68: pop            
        //    69: goto            31
        //    72: astore_1       
        //    73: aload_0        
        //    74: monitorexit    
        //    75: aload_1        
        //    76: athrow         
        //    77: aload_0        
        //    78: getfield        okhttp3/internal/ws/RealWebSocket.executor:Ljava/util/concurrent/ScheduledExecutorService;
        //    81: invokeinterface java/util/concurrent/ScheduledExecutorService.shutdown:()V
        //    86: goto            38
        //    89: astore_1       
        //    90: aload_3        
        //    91: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //    94: aload_1        
        //    95: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  2      31     72     77     Any
        //  31     38     72     77     Any
        //  38     40     72     77     Any
        //  40     50     89     96     Any
        //  55     57     72     77     Any
        //  58     69     72     77     Any
        //  73     75     72     77     Any
        //  77     86     72     77     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0055:
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
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void initReaderAndWriter(final String s, final long n, final Streams streams) throws IOException {
        synchronized (this) {
            this.streams = streams;
            this.writer = new WebSocketWriter(streams.client, streams.sink, this.random);
            this.executor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(s, false));
            if (n != 0L) {
                this.executor.scheduleAtFixedRate(new PingRunnable(), n, n, TimeUnit.MILLISECONDS);
            }
            if (!this.messageAndCloseQueue.isEmpty()) {
                this.runWriter();
            }
            monitorexit(this);
            this.reader = new WebSocketReader(streams.client, streams.source, (WebSocketReader.FrameCallback)this);
        }
    }
    
    public void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            this.reader.processNextFrame();
        }
    }
    
    @Override
    public void onReadClose(final int p0, final String p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: iconst_m1      
        //     2: if_icmpeq       55
        //     5: aload_0        
        //     6: monitorenter   
        //     7: aload_0        
        //     8: getfield        okhttp3/internal/ws/RealWebSocket.receivedCloseCode:I
        //    11: iconst_m1      
        //    12: if_icmpne       63
        //    15: aload_0        
        //    16: iload_1        
        //    17: putfield        okhttp3/internal/ws/RealWebSocket.receivedCloseCode:I
        //    20: aload_0        
        //    21: aload_2        
        //    22: putfield        okhttp3/internal/ws/RealWebSocket.receivedCloseReason:Ljava/lang/String;
        //    25: aload_0        
        //    26: getfield        okhttp3/internal/ws/RealWebSocket.enqueuedClose:Z
        //    29: ifne            81
        //    32: aconst_null    
        //    33: astore_3       
        //    34: aload_0        
        //    35: monitorexit    
        //    36: aload_0        
        //    37: getfield        okhttp3/internal/ws/RealWebSocket.listener:Lokhttp3/WebSocketListener;
        //    40: aload_0        
        //    41: iload_1        
        //    42: aload_2        
        //    43: invokevirtual   okhttp3/WebSocketListener.onClosing:(Lokhttp3/WebSocket;ILjava/lang/String;)V
        //    46: aload_3        
        //    47: ifnonnull       134
        //    50: aload_3        
        //    51: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //    54: return         
        //    55: new             Ljava/lang/IllegalArgumentException;
        //    58: dup            
        //    59: invokespecial   java/lang/IllegalArgumentException.<init>:()V
        //    62: athrow         
        //    63: new             Ljava/lang/IllegalStateException;
        //    66: astore_2       
        //    67: aload_2        
        //    68: ldc_w           "already closed"
        //    71: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //    74: aload_2        
        //    75: athrow         
        //    76: astore_2       
        //    77: aload_0        
        //    78: monitorexit    
        //    79: aload_2        
        //    80: athrow         
        //    81: aload_0        
        //    82: getfield        okhttp3/internal/ws/RealWebSocket.messageAndCloseQueue:Ljava/util/ArrayDeque;
        //    85: invokevirtual   java/util/ArrayDeque.isEmpty:()Z
        //    88: ifeq            32
        //    91: aload_0        
        //    92: getfield        okhttp3/internal/ws/RealWebSocket.streams:Lokhttp3/internal/ws/RealWebSocket$Streams;
        //    95: astore_3       
        //    96: aload_0        
        //    97: aconst_null    
        //    98: putfield        okhttp3/internal/ws/RealWebSocket.streams:Lokhttp3/internal/ws/RealWebSocket$Streams;
        //   101: aload_0        
        //   102: getfield        okhttp3/internal/ws/RealWebSocket.cancelFuture:Ljava/util/concurrent/ScheduledFuture;
        //   105: ifnonnull       120
        //   108: aload_0        
        //   109: getfield        okhttp3/internal/ws/RealWebSocket.executor:Ljava/util/concurrent/ScheduledExecutorService;
        //   112: invokeinterface java/util/concurrent/ScheduledExecutorService.shutdown:()V
        //   117: goto            34
        //   120: aload_0        
        //   121: getfield        okhttp3/internal/ws/RealWebSocket.cancelFuture:Ljava/util/concurrent/ScheduledFuture;
        //   124: iconst_0       
        //   125: invokeinterface java/util/concurrent/ScheduledFuture.cancel:(Z)Z
        //   130: pop            
        //   131: goto            108
        //   134: aload_0        
        //   135: getfield        okhttp3/internal/ws/RealWebSocket.listener:Lokhttp3/WebSocketListener;
        //   138: aload_0        
        //   139: iload_1        
        //   140: aload_2        
        //   141: invokevirtual   okhttp3/WebSocketListener.onClosed:(Lokhttp3/WebSocket;ILjava/lang/String;)V
        //   144: goto            50
        //   147: astore_2       
        //   148: aload_3        
        //   149: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //   152: aload_2        
        //   153: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  7      32     76     81     Any
        //  34     36     76     81     Any
        //  36     46     147    154    Any
        //  63     76     76     81     Any
        //  77     79     76     81     Any
        //  81     108    76     81     Any
        //  108    117    76     81     Any
        //  120    131    76     81     Any
        //  134    144    147    154    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0050:
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
    public void onReadMessage(final String s) throws IOException {
        this.listener.onMessage(this, s);
    }
    
    @Override
    public void onReadMessage(final ByteString byteString) throws IOException {
        this.listener.onMessage(this, byteString);
    }
    
    @Override
    public void onReadPing(final ByteString e) {
        synchronized (this) {
            if (!this.failed && (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty())) {
                this.pongQueue.add(e);
                this.runWriter();
                ++this.pingCount;
            }
        }
    }
    
    @Override
    public void onReadPong(final ByteString byteString) {
        synchronized (this) {
            ++this.pongCount;
        }
    }
    
    int pingCount() {
        synchronized (this) {
            return this.pingCount;
        }
    }
    
    boolean pong(final ByteString e) {
        synchronized (this) {
            if (!this.failed && (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty())) {
                this.pongQueue.add(e);
                this.runWriter();
                return true;
            }
            return false;
        }
    }
    
    int pongCount() {
        synchronized (this) {
            return this.pongCount;
        }
    }
    
    boolean processNextFrame() throws IOException {
        boolean b = false;
        try {
            this.reader.processNextFrame();
            if (this.receivedCloseCode == -1) {
                b = true;
            }
            return b;
        }
        catch (final Exception ex) {
            this.failWebSocket(ex, null);
            return false;
        }
    }
    
    @Override
    public long queueSize() {
        synchronized (this) {
            return this.queueSize;
        }
    }
    
    @Override
    public Request request() {
        return this.originalRequest;
    }
    
    @Override
    public boolean send(final String s) {
        if (s != null) {
            return this.send(ByteString.encodeUtf8(s), 1);
        }
        throw new NullPointerException("text == null");
    }
    
    @Override
    public boolean send(final ByteString byteString) {
        if (byteString != null) {
            return this.send(byteString, 2);
        }
        throw new NullPointerException("bytes == null");
    }
    
    boolean writeOneFrame() throws IOException {
        Object executor = null;
        while (true) {
            WebSocketWriter writer = null;
            int receivedCloseCode = 0;
            Serializable receivedCloseReason = null;
            final Closeable closeable4;
            Label_0334: {
                Label_0259: {
                    ByteString byteString = null;
                    Label_0247: {
                        synchronized (this) {
                            if (!this.failed) {
                                writer = this.writer;
                                byteString = this.pongQueue.poll();
                                Closeable closeable;
                                if (byteString != null) {
                                    closeable = null;
                                    receivedCloseCode = -1;
                                    receivedCloseReason = null;
                                }
                                else {
                                    final Serializable poll = this.messageAndCloseQueue.poll();
                                    if (!(poll instanceof Close)) {
                                        if (poll == null) {
                                            return false;
                                        }
                                        receivedCloseCode = -1;
                                        final Closeable closeable2 = null;
                                        receivedCloseReason = poll;
                                        closeable = closeable2;
                                    }
                                    else {
                                        receivedCloseCode = this.receivedCloseCode;
                                        receivedCloseReason = this.receivedCloseReason;
                                        if (receivedCloseCode == -1) {
                                            executor = this.executor;
                                            this.cancelFuture = ((ScheduledExecutorService)executor).schedule(new CancelRunnable(), ((Close)poll).cancelAfterCloseMillis, TimeUnit.MILLISECONDS);
                                            final Closeable closeable3 = null;
                                            executor = receivedCloseReason;
                                            receivedCloseReason = poll;
                                            closeable = closeable3;
                                        }
                                        else {
                                            final Streams streams = this.streams;
                                            this.streams = null;
                                            this.executor.shutdown();
                                            executor = receivedCloseReason;
                                            receivedCloseReason = poll;
                                            closeable = streams;
                                        }
                                    }
                                }
                                monitorexit(this);
                                if (byteString != null) {
                                    break Label_0247;
                                }
                                try {
                                    if (receivedCloseReason instanceof Message) {
                                        break Label_0259;
                                    }
                                    if (!(receivedCloseReason instanceof Close)) {
                                        throw new AssertionError();
                                    }
                                    break Label_0334;
                                }
                                finally {
                                    Util.closeQuietly(closeable);
                                }
                            }
                            return false;
                        }
                    }
                    writer.writePong(byteString);
                    Util.closeQuietly(closeable4);
                    return true;
                }
                final ByteString data = ((Message)receivedCloseReason).data;
                final BufferedSink buffer = Okio.buffer(writer.newMessageSink(((Message)receivedCloseReason).formatOpcode, data.size()));
                buffer.write(data);
                buffer.close();
                synchronized (this) {
                    this.queueSize -= data.size();
                    continue;
                }
            }
            final Close close = (Close)receivedCloseReason;
            writer.writeClose(close.code, close.reason);
            if (closeable4 != null) {
                this.listener.onClosed(this, receivedCloseCode, (String)executor);
            }
            continue;
        }
    }
    
    final class CancelRunnable implements Runnable
    {
        @Override
        public void run() {
            RealWebSocket.this.cancel();
        }
    }
    
    static final class ClientStreams extends Streams
    {
        private final StreamAllocation streamAllocation;
        
        ClientStreams(final StreamAllocation streamAllocation) {
            super(true, streamAllocation.connection().source, streamAllocation.connection().sink);
            this.streamAllocation = streamAllocation;
        }
        
        @Override
        public void close() {
            this.streamAllocation.streamFinished(true, this.streamAllocation.codec());
        }
    }
    
    static final class Close
    {
        final long cancelAfterCloseMillis;
        final int code;
        final ByteString reason;
        
        Close(final int code, final ByteString reason, final long cancelAfterCloseMillis) {
            this.code = code;
            this.reason = reason;
            this.cancelAfterCloseMillis = cancelAfterCloseMillis;
        }
    }
    
    static final class Message
    {
        final ByteString data;
        final int formatOpcode;
        
        Message(final int formatOpcode, final ByteString data) {
            this.formatOpcode = formatOpcode;
            this.data = data;
        }
    }
    
    private final class PingRunnable implements Runnable
    {
        @Override
        public void run() {
            RealWebSocket.this.writePingFrame();
        }
    }
    
    public abstract static class Streams implements Closeable
    {
        public final boolean client;
        public final BufferedSink sink;
        public final BufferedSource source;
        
        public Streams(final boolean client, final BufferedSource source, final BufferedSink sink) {
            this.client = client;
            this.source = source;
            this.sink = sink;
        }
    }
}
