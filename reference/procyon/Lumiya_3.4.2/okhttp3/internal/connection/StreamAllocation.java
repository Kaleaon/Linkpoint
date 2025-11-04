// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.connection;

import java.lang.ref.WeakReference;
import okhttp3.OkHttpClient;
import java.lang.ref.Reference;
import java.io.IOException;
import okhttp3.internal.Util;
import okhttp3.internal.Internal;
import okhttp3.Route;
import okhttp3.ConnectionPool;
import okhttp3.internal.http.HttpCodec;
import okhttp3.Address;

public final class StreamAllocation
{
    public final Address address;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    private int refusedStreamCount;
    private boolean released;
    private Route route;
    private final RouteSelector routeSelector;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!StreamAllocation.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
    }
    
    public StreamAllocation(final ConnectionPool connectionPool, final Address address, final Object callStackTrace) {
        this.connectionPool = connectionPool;
        this.address = address;
        this.routeSelector = new RouteSelector(address, this.routeDatabase());
        this.callStackTrace = callStackTrace;
    }
    
    private void deallocate(final boolean b, final boolean b2, final boolean b3) {
        final RealConnection realConnection = null;
        final RealConnection realConnection2 = null;
        final ConnectionPool connectionPool = this.connectionPool;
        monitorenter(connectionPool);
        while (true) {
        Label_0059:
            while (true) {
                Label_0019: {
                    if (!b3) {
                        break Label_0019;
                    }
                    Label_0043: {
                        break Label_0043;
                        while (true) {
                        Label_0192:
                            while (true) {
                                Label_0067: {
                                    try {
                                        if (this.connection != null) {
                                            break Label_0067;
                                        }
                                        final RealConnection connection = realConnection2;
                                        monitorexit(connectionPool);
                                        if (connection == null) {
                                            return;
                                        }
                                        break Label_0192;
                                        this.codec = null;
                                        break;
                                    }
                                    finally {
                                        monitorexit(connectionPool);
                                    }
                                    break Label_0059;
                                }
                                if (b) {
                                    this.connection.noNewStreams = true;
                                }
                                RealConnection connection = realConnection2;
                                if (this.codec != null) {
                                    continue;
                                }
                                if (!this.released && !this.connection.noNewStreams) {
                                    connection = realConnection2;
                                    continue;
                                }
                                this.release(this.connection);
                                if (!this.connection.allocations.isEmpty()) {
                                    connection = realConnection;
                                }
                                else {
                                    this.connection.idleAtNanos = System.nanoTime();
                                    connection = realConnection;
                                    if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                                        connection = this.connection;
                                    }
                                }
                                this.connection = null;
                                continue;
                            }
                            final RealConnection realConnection3;
                            Util.closeQuietly(realConnection3.socket());
                            return;
                        }
                    }
                }
                if (!b2) {
                    continue;
                }
                break;
            }
            this.released = true;
            continue;
        }
    }
    
    private RealConnection findConnection(final int p0, final int p1, final int p2, final boolean p3) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        okhttp3/internal/connection/StreamAllocation.connectionPool:Lokhttp3/ConnectionPool;
        //     4: astore          5
        //     6: aload           5
        //     8: monitorenter   
        //     9: aload_0        
        //    10: getfield        okhttp3/internal/connection/StreamAllocation.released:Z
        //    13: ifne            163
        //    16: aload_0        
        //    17: getfield        okhttp3/internal/connection/StreamAllocation.codec:Lokhttp3/internal/http/HttpCodec;
        //    20: ifnonnull       186
        //    23: aload_0        
        //    24: getfield        okhttp3/internal/connection/StreamAllocation.canceled:Z
        //    27: ifne            201
        //    30: aload_0        
        //    31: getfield        okhttp3/internal/connection/StreamAllocation.connection:Lokhttp3/internal/connection/RealConnection;
        //    34: astore          6
        //    36: aload           6
        //    38: ifnonnull       216
        //    41: getstatic       okhttp3/internal/Internal.instance:Lokhttp3/internal/Internal;
        //    44: aload_0        
        //    45: getfield        okhttp3/internal/connection/StreamAllocation.connectionPool:Lokhttp3/ConnectionPool;
        //    48: aload_0        
        //    49: getfield        okhttp3/internal/connection/StreamAllocation.address:Lokhttp3/Address;
        //    52: aload_0        
        //    53: invokevirtual   okhttp3/internal/Internal.get:(Lokhttp3/ConnectionPool;Lokhttp3/Address;Lokhttp3/internal/connection/StreamAllocation;)Lokhttp3/internal/connection/RealConnection;
        //    56: astore          6
        //    58: aload           6
        //    60: ifnonnull       230
        //    63: aload_0        
        //    64: getfield        okhttp3/internal/connection/StreamAllocation.route:Lokhttp3/Route;
        //    67: astore          6
        //    69: aload           5
        //    71: monitorexit    
        //    72: aload           6
        //    74: ifnull          242
        //    77: new             Lokhttp3/internal/connection/RealConnection;
        //    80: dup            
        //    81: aload           6
        //    83: invokespecial   okhttp3/internal/connection/RealConnection.<init>:(Lokhttp3/Route;)V
        //    86: astore          5
        //    88: aload_0        
        //    89: getfield        okhttp3/internal/connection/StreamAllocation.connectionPool:Lokhttp3/ConnectionPool;
        //    92: astore          6
        //    94: aload           6
        //    96: monitorenter   
        //    97: aload_0        
        //    98: aload           5
        //   100: invokevirtual   okhttp3/internal/connection/StreamAllocation.acquire:(Lokhttp3/internal/connection/RealConnection;)V
        //   103: getstatic       okhttp3/internal/Internal.instance:Lokhttp3/internal/Internal;
        //   106: aload_0        
        //   107: getfield        okhttp3/internal/connection/StreamAllocation.connectionPool:Lokhttp3/ConnectionPool;
        //   110: aload           5
        //   112: invokevirtual   okhttp3/internal/Internal.put:(Lokhttp3/ConnectionPool;Lokhttp3/internal/connection/RealConnection;)V
        //   115: aload_0        
        //   116: aload           5
        //   118: putfield        okhttp3/internal/connection/StreamAllocation.connection:Lokhttp3/internal/connection/RealConnection;
        //   121: aload_0        
        //   122: getfield        okhttp3/internal/connection/StreamAllocation.canceled:Z
        //   125: ifne            285
        //   128: aload           6
        //   130: monitorexit    
        //   131: aload           5
        //   133: iload_1        
        //   134: iload_2        
        //   135: iload_3        
        //   136: aload_0        
        //   137: getfield        okhttp3/internal/connection/StreamAllocation.address:Lokhttp3/Address;
        //   140: invokevirtual   okhttp3/Address.connectionSpecs:()Ljava/util/List;
        //   143: iload           4
        //   145: invokevirtual   okhttp3/internal/connection/RealConnection.connect:(IIILjava/util/List;Z)V
        //   148: aload_0        
        //   149: invokespecial   okhttp3/internal/connection/StreamAllocation.routeDatabase:()Lokhttp3/internal/connection/RouteDatabase;
        //   152: aload           5
        //   154: invokevirtual   okhttp3/internal/connection/RealConnection.route:()Lokhttp3/Route;
        //   157: invokevirtual   okhttp3/internal/connection/RouteDatabase.connected:(Lokhttp3/Route;)V
        //   160: aload           5
        //   162: areturn        
        //   163: new             Ljava/lang/IllegalStateException;
        //   166: astore          6
        //   168: aload           6
        //   170: ldc             "released"
        //   172: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   175: aload           6
        //   177: athrow         
        //   178: astore          6
        //   180: aload           5
        //   182: monitorexit    
        //   183: aload           6
        //   185: athrow         
        //   186: new             Ljava/lang/IllegalStateException;
        //   189: astore          6
        //   191: aload           6
        //   193: ldc             "codec != null"
        //   195: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   198: aload           6
        //   200: athrow         
        //   201: new             Ljava/io/IOException;
        //   204: astore          6
        //   206: aload           6
        //   208: ldc             "Canceled"
        //   210: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //   213: aload           6
        //   215: athrow         
        //   216: aload           6
        //   218: getfield        okhttp3/internal/connection/RealConnection.noNewStreams:Z
        //   221: ifne            41
        //   224: aload           5
        //   226: monitorexit    
        //   227: aload           6
        //   229: areturn        
        //   230: aload_0        
        //   231: aload           6
        //   233: putfield        okhttp3/internal/connection/StreamAllocation.connection:Lokhttp3/internal/connection/RealConnection;
        //   236: aload           5
        //   238: monitorexit    
        //   239: aload           6
        //   241: areturn        
        //   242: aload_0        
        //   243: getfield        okhttp3/internal/connection/StreamAllocation.routeSelector:Lokhttp3/internal/connection/RouteSelector;
        //   246: invokevirtual   okhttp3/internal/connection/RouteSelector.next:()Lokhttp3/Route;
        //   249: astore          6
        //   251: aload_0        
        //   252: getfield        okhttp3/internal/connection/StreamAllocation.connectionPool:Lokhttp3/ConnectionPool;
        //   255: astore          5
        //   257: aload           5
        //   259: monitorenter   
        //   260: aload_0        
        //   261: aload           6
        //   263: putfield        okhttp3/internal/connection/StreamAllocation.route:Lokhttp3/Route;
        //   266: aload_0        
        //   267: iconst_0       
        //   268: putfield        okhttp3/internal/connection/StreamAllocation.refusedStreamCount:I
        //   271: aload           5
        //   273: monitorexit    
        //   274: goto            77
        //   277: astore          6
        //   279: aload           5
        //   281: monitorexit    
        //   282: aload           6
        //   284: athrow         
        //   285: new             Ljava/io/IOException;
        //   288: astore          5
        //   290: aload           5
        //   292: ldc             "Canceled"
        //   294: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //   297: aload           5
        //   299: athrow         
        //   300: astore          5
        //   302: aload           6
        //   304: monitorexit    
        //   305: aload           5
        //   307: athrow         
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  9      36     178    186    Any
        //  41     58     178    186    Any
        //  63     72     178    186    Any
        //  97     131    300    308    Any
        //  163    178    178    186    Any
        //  180    183    178    186    Any
        //  186    201    178    186    Any
        //  201    216    178    186    Any
        //  216    227    178    186    Any
        //  230    239    178    186    Any
        //  260    274    277    285    Any
        //  279    282    277    285    Any
        //  285    300    300    308    Any
        //  302    305    300    308    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0163:
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
    
    private RealConnection findHealthyConnection(final int n, final int n2, final int n3, final boolean b, final boolean b2) throws IOException {
        while (true) {
            final RealConnection connection = this.findConnection(n, n2, n3, b);
            synchronized (this.connectionPool) {
                if (connection.successCount == 0) {
                    return connection;
                }
                monitorexit(this.connectionPool);
                if (connection.isHealthy(b2)) {
                    return connection;
                }
            }
            this.noNewStreams();
        }
    }
    
    private void release(final RealConnection realConnection) {
        for (int size = realConnection.allocations.size(), i = 0; i < size; ++i) {
            if (realConnection.allocations.get(i).get() == this) {
                realConnection.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }
    
    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }
    
    public void acquire(final RealConnection realConnection) {
        assert Thread.holdsLock(this.connectionPool);
        realConnection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
    }
    
    public void cancel() {
        while (true) {
            Label_0047: {
                final HttpCodec codec;
                synchronized (this.connectionPool) {
                    this.canceled = true;
                    codec = this.codec;
                    final RealConnection connection = this.connection;
                    monitorexit(this.connectionPool);
                    if (codec == null) {
                        if (connection == null) {
                            return;
                        }
                        break Label_0047;
                    }
                }
                codec.cancel();
                return;
            }
            final RealConnection realConnection;
            realConnection.cancel();
        }
    }
    
    public HttpCodec codec() {
        synchronized (this.connectionPool) {
            return this.codec;
        }
    }
    
    public RealConnection connection() {
        synchronized (this) {
            return this.connection;
        }
    }
    
    public boolean hasMoreRoutes() {
        boolean b = false;
        if (this.route != null || this.routeSelector.hasNext()) {
            b = true;
        }
        return b;
    }
    
    public HttpCodec newStream(final OkHttpClient p0, final boolean p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   okhttp3/OkHttpClient.connectTimeoutMillis:()I
        //     4: istore_3       
        //     5: aload_1        
        //     6: invokevirtual   okhttp3/OkHttpClient.readTimeoutMillis:()I
        //     9: istore          4
        //    11: aload_1        
        //    12: invokevirtual   okhttp3/OkHttpClient.writeTimeoutMillis:()I
        //    15: istore          5
        //    17: aload_1        
        //    18: invokevirtual   okhttp3/OkHttpClient.retryOnConnectionFailure:()Z
        //    21: istore          6
        //    23: aload_0        
        //    24: iload_3        
        //    25: iload           4
        //    27: iload           5
        //    29: iload           6
        //    31: iload_2        
        //    32: invokespecial   okhttp3/internal/connection/StreamAllocation.findHealthyConnection:(IIIZZ)Lokhttp3/internal/connection/RealConnection;
        //    35: astore          7
        //    37: aload           7
        //    39: getfield        okhttp3/internal/connection/RealConnection.http2Connection:Lokhttp3/internal/http2/Http2Connection;
        //    42: ifnonnull       139
        //    45: aload           7
        //    47: invokevirtual   okhttp3/internal/connection/RealConnection.socket:()Ljava/net/Socket;
        //    50: iload           4
        //    52: invokevirtual   java/net/Socket.setSoTimeout:(I)V
        //    55: aload           7
        //    57: getfield        okhttp3/internal/connection/RealConnection.source:Lokio/BufferedSource;
        //    60: invokeinterface okio/BufferedSource.timeout:()Lokio/Timeout;
        //    65: iload           4
        //    67: i2l            
        //    68: getstatic       java/util/concurrent/TimeUnit.MILLISECONDS:Ljava/util/concurrent/TimeUnit;
        //    71: invokevirtual   okio/Timeout.timeout:(JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
        //    74: pop            
        //    75: aload           7
        //    77: getfield        okhttp3/internal/connection/RealConnection.sink:Lokio/BufferedSink;
        //    80: invokeinterface okio/BufferedSink.timeout:()Lokio/Timeout;
        //    85: iload           5
        //    87: i2l            
        //    88: getstatic       java/util/concurrent/TimeUnit.MILLISECONDS:Ljava/util/concurrent/TimeUnit;
        //    91: invokevirtual   okio/Timeout.timeout:(JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
        //    94: pop            
        //    95: new             Lokhttp3/internal/http1/Http1Codec;
        //    98: astore          8
        //   100: aload           8
        //   102: aload_1        
        //   103: aload_0        
        //   104: aload           7
        //   106: getfield        okhttp3/internal/connection/RealConnection.source:Lokio/BufferedSource;
        //   109: aload           7
        //   111: getfield        okhttp3/internal/connection/RealConnection.sink:Lokio/BufferedSink;
        //   114: invokespecial   okhttp3/internal/http1/Http1Codec.<init>:(Lokhttp3/OkHttpClient;Lokhttp3/internal/connection/StreamAllocation;Lokio/BufferedSource;Lokio/BufferedSink;)V
        //   117: aload           8
        //   119: astore_1       
        //   120: aload_0        
        //   121: getfield        okhttp3/internal/connection/StreamAllocation.connectionPool:Lokhttp3/ConnectionPool;
        //   124: astore          8
        //   126: aload           8
        //   128: monitorenter   
        //   129: aload_0        
        //   130: aload_1        
        //   131: putfield        okhttp3/internal/connection/StreamAllocation.codec:Lokhttp3/internal/http/HttpCodec;
        //   134: aload           8
        //   136: monitorexit    
        //   137: aload_1        
        //   138: areturn        
        //   139: new             Lokhttp3/internal/http2/Http2Codec;
        //   142: dup            
        //   143: aload_1        
        //   144: aload_0        
        //   145: aload           7
        //   147: getfield        okhttp3/internal/connection/RealConnection.http2Connection:Lokhttp3/internal/http2/Http2Connection;
        //   150: invokespecial   okhttp3/internal/http2/Http2Codec.<init>:(Lokhttp3/OkHttpClient;Lokhttp3/internal/connection/StreamAllocation;Lokhttp3/internal/http2/Http2Connection;)V
        //   153: astore_1       
        //   154: goto            120
        //   157: astore_1       
        //   158: new             Lokhttp3/internal/connection/RouteException;
        //   161: dup            
        //   162: aload_1        
        //   163: invokespecial   okhttp3/internal/connection/RouteException.<init>:(Ljava/io/IOException;)V
        //   166: athrow         
        //   167: astore_1       
        //   168: aload           8
        //   170: monitorexit    
        //   171: aload_1        
        //   172: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  23     117    157    167    Ljava/io/IOException;
        //  120    129    157    167    Ljava/io/IOException;
        //  129    137    167    173    Any
        //  139    154    157    167    Ljava/io/IOException;
        //  168    171    167    173    Any
        //  171    173    157    167    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0139:
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
    
    public void noNewStreams() {
        this.deallocate(true, false, false);
    }
    
    public void release() {
        this.deallocate(false, true, false);
    }
    
    public void streamFailed(final IOException p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        okhttp3/internal/connection/StreamAllocation.connectionPool:Lokhttp3/ConnectionPool;
        //     4: astore_2       
        //     5: aload_2        
        //     6: monitorenter   
        //     7: aload_1        
        //     8: instanceof      Lokhttp3/internal/http2/StreamResetException;
        //    11: ifne            40
        //    14: aload_0        
        //    15: getfield        okhttp3/internal/connection/StreamAllocation.connection:Lokhttp3/internal/connection/RealConnection;
        //    18: ifnonnull       106
        //    21: aload_1        
        //    22: instanceof      Lokhttp3/internal/http2/ConnectionShutdownException;
        //    25: ifne            116
        //    28: iconst_0       
        //    29: istore_3       
        //    30: aload_2        
        //    31: monitorexit    
        //    32: aload_0        
        //    33: iload_3        
        //    34: iconst_0       
        //    35: iconst_1       
        //    36: invokespecial   okhttp3/internal/connection/StreamAllocation.deallocate:(ZZZ)V
        //    39: return         
        //    40: aload_1        
        //    41: checkcast       Lokhttp3/internal/http2/StreamResetException;
        //    44: astore_1       
        //    45: aload_1        
        //    46: getfield        okhttp3/internal/http2/StreamResetException.errorCode:Lokhttp3/internal/http2/ErrorCode;
        //    49: getstatic       okhttp3/internal/http2/ErrorCode.REFUSED_STREAM:Lokhttp3/internal/http2/ErrorCode;
        //    52: if_acmpeq       75
        //    55: aload_1        
        //    56: getfield        okhttp3/internal/http2/StreamResetException.errorCode:Lokhttp3/internal/http2/ErrorCode;
        //    59: getstatic       okhttp3/internal/http2/ErrorCode.REFUSED_STREAM:Lokhttp3/internal/http2/ErrorCode;
        //    62: if_acmpeq       93
        //    65: aload_0        
        //    66: aconst_null    
        //    67: putfield        okhttp3/internal/connection/StreamAllocation.route:Lokhttp3/Route;
        //    70: iconst_1       
        //    71: istore_3       
        //    72: goto            30
        //    75: aload_0        
        //    76: aload_0        
        //    77: getfield        okhttp3/internal/connection/StreamAllocation.refusedStreamCount:I
        //    80: iconst_1       
        //    81: iadd           
        //    82: putfield        okhttp3/internal/connection/StreamAllocation.refusedStreamCount:I
        //    85: goto            55
        //    88: astore_1       
        //    89: aload_2        
        //    90: monitorexit    
        //    91: aload_1        
        //    92: athrow         
        //    93: aload_0        
        //    94: getfield        okhttp3/internal/connection/StreamAllocation.refusedStreamCount:I
        //    97: iconst_1       
        //    98: if_icmpgt       65
        //   101: iconst_0       
        //   102: istore_3       
        //   103: goto            30
        //   106: aload_0        
        //   107: getfield        okhttp3/internal/connection/StreamAllocation.connection:Lokhttp3/internal/connection/RealConnection;
        //   110: invokevirtual   okhttp3/internal/connection/RealConnection.isMultiplexed:()Z
        //   113: ifne            21
        //   116: aload_0        
        //   117: getfield        okhttp3/internal/connection/StreamAllocation.connection:Lokhttp3/internal/connection/RealConnection;
        //   120: getfield        okhttp3/internal/connection/RealConnection.successCount:I
        //   123: ifeq            131
        //   126: iconst_1       
        //   127: istore_3       
        //   128: goto            30
        //   131: aload_0        
        //   132: getfield        okhttp3/internal/connection/StreamAllocation.route:Lokhttp3/Route;
        //   135: ifnonnull       148
        //   138: aload_0        
        //   139: aconst_null    
        //   140: putfield        okhttp3/internal/connection/StreamAllocation.route:Lokhttp3/Route;
        //   143: iconst_1       
        //   144: istore_3       
        //   145: goto            30
        //   148: aload_1        
        //   149: ifnull          138
        //   152: aload_0        
        //   153: getfield        okhttp3/internal/connection/StreamAllocation.routeSelector:Lokhttp3/internal/connection/RouteSelector;
        //   156: aload_0        
        //   157: getfield        okhttp3/internal/connection/StreamAllocation.route:Lokhttp3/Route;
        //   160: aload_1        
        //   161: invokevirtual   okhttp3/internal/connection/RouteSelector.connectFailed:(Lokhttp3/Route;Ljava/io/IOException;)V
        //   164: goto            138
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  7      21     88     93     Any
        //  21     28     88     93     Any
        //  30     32     88     93     Any
        //  40     55     88     93     Any
        //  55     65     88     93     Any
        //  65     70     88     93     Any
        //  75     85     88     93     Any
        //  89     91     88     93     Any
        //  93     101    88     93     Any
        //  106    116    88     93     Any
        //  116    126    88     93     Any
        //  131    138    88     93     Any
        //  138    143    88     93     Any
        //  152    164    88     93     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: invokevirtual:boolean(RealConnection::isMultiplexed, getfield:RealConnection(StreamAllocation::connection, this:StreamAllocation))
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
    
    public void streamFinished(final boolean b, final HttpCodec obj) {
        final ConnectionPool connectionPool = this.connectionPool;
        monitorenter(connectionPool);
        while (true) {
            Label_0067: {
                if (obj != null) {
                    break Label_0067;
                }
                try {
                    throw new IllegalStateException("expected " + this.codec + " but was " + obj);
                }
                finally {
                    monitorexit(connectionPool);
                }
            }
            if (obj == this.codec) {
                if (!b) {
                    final RealConnection connection = this.connection;
                    ++connection.successCount;
                }
                monitorexit(connectionPool);
                this.deallocate(b, false, true);
                return;
            }
            continue;
        }
    }
    
    @Override
    public String toString() {
        return this.address.toString();
    }
    
    public static final class StreamAllocationReference extends WeakReference<StreamAllocation>
    {
        public final Object callStackTrace;
        
        StreamAllocationReference(final StreamAllocation referent, final Object callStackTrace) {
            super(referent);
            this.callStackTrace = callStackTrace;
        }
    }
}
