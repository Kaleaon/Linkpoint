// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import java.net.SocketTimeoutException;
import okio.AsyncTimeout;
import okio.Buffer;
import java.io.InterruptedIOException;
import okio.BufferedSource;
import okio.Timeout;
import okio.Source;
import okio.Sink;
import java.io.IOException;
import java.util.List;

public final class Http2Stream
{
    long bytesLeftInWriteWindow;
    final Http2Connection connection;
    ErrorCode errorCode;
    final int id;
    final StreamTimeout readTimeout;
    private final List<Header> requestHeaders;
    private List<Header> responseHeaders;
    final FramedDataSink sink;
    private final FramedDataSource source;
    long unacknowledgedBytesRead;
    final StreamTimeout writeTimeout;
    
    static {
        boolean $assertionsDisabled2 = false;
        if (!Http2Stream.class.desiredAssertionStatus()) {
            $assertionsDisabled2 = true;
        }
        $assertionsDisabled = $assertionsDisabled2;
    }
    
    Http2Stream(final int id, final Http2Connection connection, final boolean finished, final boolean finished2, final List<Header> requestHeaders) {
        this.unacknowledgedBytesRead = 0L;
        this.readTimeout = new StreamTimeout();
        this.writeTimeout = new StreamTimeout();
        this.errorCode = null;
        if (connection == null) {
            throw new NullPointerException("connection == null");
        }
        if (requestHeaders != null) {
            this.id = id;
            this.connection = connection;
            this.bytesLeftInWriteWindow = connection.peerSettings.getInitialWindowSize();
            this.source = new FramedDataSource(connection.okHttpSettings.getInitialWindowSize());
            this.sink = new FramedDataSink();
            this.source.finished = finished2;
            this.sink.finished = finished;
            this.requestHeaders = requestHeaders;
            return;
        }
        throw new NullPointerException("requestHeaders == null");
    }
    
    private boolean closeInternal(final ErrorCode p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: ifeq            50
        //     6: aload_0        
        //     7: monitorenter   
        //     8: aload_0        
        //     9: getfield        okhttp3/internal/http2/Http2Stream.errorCode:Lokhttp3/internal/http2/ErrorCode;
        //    12: ifnonnull       65
        //    15: aload_0        
        //    16: getfield        okhttp3/internal/http2/Http2Stream.source:Lokhttp3/internal/http2/Http2Stream$FramedDataSource;
        //    19: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.finished:Z
        //    22: ifne            69
        //    25: aload_0        
        //    26: aload_1        
        //    27: putfield        okhttp3/internal/http2/Http2Stream.errorCode:Lokhttp3/internal/http2/ErrorCode;
        //    30: aload_0        
        //    31: invokevirtual   java/lang/Object.notifyAll:()V
        //    34: aload_0        
        //    35: monitorexit    
        //    36: aload_0        
        //    37: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
        //    40: aload_0        
        //    41: getfield        okhttp3/internal/http2/Http2Stream.id:I
        //    44: invokevirtual   okhttp3/internal/http2/Http2Connection.removeStream:(I)Lokhttp3/internal/http2/Http2Stream;
        //    47: pop            
        //    48: iconst_1       
        //    49: ireturn        
        //    50: aload_0        
        //    51: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
        //    54: ifeq            6
        //    57: new             Ljava/lang/AssertionError;
        //    60: dup            
        //    61: invokespecial   java/lang/AssertionError.<init>:()V
        //    64: athrow         
        //    65: aload_0        
        //    66: monitorexit    
        //    67: iconst_0       
        //    68: ireturn        
        //    69: aload_0        
        //    70: getfield        okhttp3/internal/http2/Http2Stream.sink:Lokhttp3/internal/http2/Http2Stream$FramedDataSink;
        //    73: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.finished:Z
        //    76: ifeq            25
        //    79: aload_0        
        //    80: monitorexit    
        //    81: iconst_0       
        //    82: ireturn        
        //    83: astore_1       
        //    84: aload_0        
        //    85: monitorexit    
        //    86: aload_1        
        //    87: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  8      25     83     88     Any
        //  25     36     83     88     Any
        //  65     67     83     88     Any
        //  69     81     83     88     Any
        //  84     86     83     88     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokestatic:boolean(Thread::holdsLock, this:Http2Stream[expected:Object]))
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
    
    void cancelStreamIfNecessary() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: getstatic       okhttp3/internal/http2/Http2Stream.$assertionsDisabled:Z
        //     5: ifeq            38
        //     8: aload_0        
        //     9: monitorenter   
        //    10: aload_0        
        //    11: getfield        okhttp3/internal/http2/Http2Stream.source:Lokhttp3/internal/http2/Http2Stream$FramedDataSource;
        //    14: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.finished:Z
        //    17: ifeq            53
        //    20: iload_1        
        //    21: istore_2       
        //    22: aload_0        
        //    23: invokevirtual   okhttp3/internal/http2/Http2Stream.isOpen:()Z
        //    26: istore_3       
        //    27: aload_0        
        //    28: monitorexit    
        //    29: iload_2        
        //    30: ifne            102
        //    33: iload_3        
        //    34: ifeq            112
        //    37: return         
        //    38: aload_0        
        //    39: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
        //    42: ifeq            8
        //    45: new             Ljava/lang/AssertionError;
        //    48: dup            
        //    49: invokespecial   java/lang/AssertionError.<init>:()V
        //    52: athrow         
        //    53: iload_1        
        //    54: istore_2       
        //    55: aload_0        
        //    56: getfield        okhttp3/internal/http2/Http2Stream.source:Lokhttp3/internal/http2/Http2Stream$FramedDataSource;
        //    59: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.closed:Z
        //    62: ifeq            22
        //    65: aload_0        
        //    66: getfield        okhttp3/internal/http2/Http2Stream.sink:Lokhttp3/internal/http2/Http2Stream$FramedDataSink;
        //    69: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.finished:Z
        //    72: ifeq            80
        //    75: iconst_1       
        //    76: istore_2       
        //    77: goto            22
        //    80: aload_0        
        //    81: getfield        okhttp3/internal/http2/Http2Stream.sink:Lokhttp3/internal/http2/Http2Stream$FramedDataSink;
        //    84: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.closed:Z
        //    87: ifne            75
        //    90: iload_1        
        //    91: istore_2       
        //    92: goto            22
        //    95: astore          4
        //    97: aload_0        
        //    98: monitorexit    
        //    99: aload           4
        //   101: athrow         
        //   102: aload_0        
        //   103: getstatic       okhttp3/internal/http2/ErrorCode.CANCEL:Lokhttp3/internal/http2/ErrorCode;
        //   106: invokevirtual   okhttp3/internal/http2/Http2Stream.close:(Lokhttp3/internal/http2/ErrorCode;)V
        //   109: goto            37
        //   112: aload_0        
        //   113: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
        //   116: aload_0        
        //   117: getfield        okhttp3/internal/http2/Http2Stream.id:I
        //   120: invokevirtual   okhttp3/internal/http2/Http2Connection.removeStream:(I)Lokhttp3/internal/http2/Http2Stream;
        //   123: pop            
        //   124: goto            37
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  10     20     95     102    Any
        //  22     29     95     102    Any
        //  55     75     95     102    Any
        //  80     90     95     102    Any
        //  97     99     95     102    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokestatic:boolean(Thread::holdsLock, this:Http2Stream[expected:Object]))
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
    
    void checkOutNotClosed() throws IOException {
        if (this.sink.closed) {
            throw new IOException("stream closed");
        }
        if (this.sink.finished) {
            throw new IOException("stream finished");
        }
        if (this.errorCode == null) {
            return;
        }
        throw new StreamResetException(this.errorCode);
    }
    
    public void close(final ErrorCode errorCode) throws IOException {
        if (this.closeInternal(errorCode)) {
            this.connection.writeSynReset(this.id, errorCode);
        }
    }
    
    public void closeLater(final ErrorCode errorCode) {
        if (this.closeInternal(errorCode)) {
            this.connection.writeSynResetLater(this.id, errorCode);
        }
    }
    
    public Http2Connection getConnection() {
        return this.connection;
    }
    
    public ErrorCode getErrorCode() {
        synchronized (this) {
            return this.errorCode;
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    public List<Header> getRequestHeaders() {
        return this.requestHeaders;
    }
    
    public List<Header> getResponseHeaders() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_0        
        //     3: getfield        okhttp3/internal/http2/Http2Stream.readTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
        //     6: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.enter:()V
        //     9: aload_0        
        //    10: getfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    13: astore_1       
        //    14: aload_1        
        //    15: ifnull          51
        //    18: aload_0        
        //    19: getfield        okhttp3/internal/http2/Http2Stream.readTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
        //    22: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
        //    25: aload_0        
        //    26: getfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    29: ifnonnull       75
        //    32: new             Lokhttp3/internal/http2/StreamResetException;
        //    35: astore_1       
        //    36: aload_1        
        //    37: aload_0        
        //    38: getfield        okhttp3/internal/http2/Http2Stream.errorCode:Lokhttp3/internal/http2/ErrorCode;
        //    41: invokespecial   okhttp3/internal/http2/StreamResetException.<init>:(Lokhttp3/internal/http2/ErrorCode;)V
        //    44: aload_1        
        //    45: athrow         
        //    46: astore_1       
        //    47: aload_0        
        //    48: monitorexit    
        //    49: aload_1        
        //    50: athrow         
        //    51: aload_0        
        //    52: getfield        okhttp3/internal/http2/Http2Stream.errorCode:Lokhttp3/internal/http2/ErrorCode;
        //    55: ifnonnull       18
        //    58: aload_0        
        //    59: invokevirtual   okhttp3/internal/http2/Http2Stream.waitForIo:()V
        //    62: goto            9
        //    65: astore_1       
        //    66: aload_0        
        //    67: getfield        okhttp3/internal/http2/Http2Stream.readTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
        //    70: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
        //    73: aload_1        
        //    74: athrow         
        //    75: aload_0        
        //    76: getfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    79: astore_1       
        //    80: aload_0        
        //    81: monitorexit    
        //    82: aload_1        
        //    83: areturn        
        //    Exceptions:
        //  throws java.io.IOException
        //    Signature:
        //  ()Ljava/util/List<Lokhttp3/internal/http2/Header;>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  2      9      46     51     Any
        //  9      14     65     75     Any
        //  18     46     46     51     Any
        //  51     62     65     75     Any
        //  66     75     46     51     Any
        //  75     80     46     51     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0009:
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
    
    public Sink getSink() {
        synchronized (this) {
            if (this.responseHeaders == null && !this.isLocallyInitiated()) {
                throw new IllegalStateException("reply before requesting the sink");
            }
            monitorexit(this);
            return this.sink;
        }
    }
    
    public Source getSource() {
        return this.source;
    }
    
    public boolean isLocallyInitiated() {
        final boolean b = false;
        return this.connection.client == ((this.id & 0x1) == 0x1) || b;
    }
    
    public boolean isOpen() {
        synchronized (this) {
            if (this.errorCode == null) {
                if (this.source.finished || this.source.closed) {
                    if (this.sink.finished || this.sink.closed) {
                        if (this.responseHeaders != null) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    public Timeout readTimeout() {
        return this.readTimeout;
    }
    
    void receiveData(final BufferedSource bufferedSource, final int n) throws IOException {
        assert !Thread.holdsLock(this);
        this.source.receive(bufferedSource, n);
    }
    
    void receiveFin() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     6: aload_0        
        //     7: monitorenter   
        //     8: aload_0        
        //     9: getfield        okhttp3/internal/http2/Http2Stream.source:Lokhttp3/internal/http2/Http2Stream$FramedDataSource;
        //    12: iconst_1       
        //    13: putfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.finished:Z
        //    16: aload_0        
        //    17: invokevirtual   okhttp3/internal/http2/Http2Stream.isOpen:()Z
        //    20: istore_1       
        //    21: aload_0        
        //    22: invokevirtual   java/lang/Object.notifyAll:()V
        //    25: aload_0        
        //    26: monitorexit    
        //    27: iload_1        
        //    28: ifeq            52
        //    31: return         
        //    32: aload_0        
        //    33: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
        //    36: ifeq            6
        //    39: new             Ljava/lang/AssertionError;
        //    42: dup            
        //    43: invokespecial   java/lang/AssertionError.<init>:()V
        //    46: athrow         
        //    47: astore_2       
        //    48: aload_0        
        //    49: monitorexit    
        //    50: aload_2        
        //    51: athrow         
        //    52: aload_0        
        //    53: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
        //    56: aload_0        
        //    57: getfield        okhttp3/internal/http2/Http2Stream.id:I
        //    60: invokevirtual   okhttp3/internal/http2/Http2Connection.removeStream:(I)Lokhttp3/internal/http2/Http2Stream;
        //    63: pop            
        //    64: goto            31
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  8      27     47     52     Any
        //  48     50     47     52     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokestatic:boolean(Thread::holdsLock, this:Http2Stream[expected:Object]))
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
    
    void receiveHeaders(final List<Header> p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     6: iconst_1       
        //     7: istore_2       
        //     8: aload_0        
        //     9: monitorenter   
        //    10: aload_0        
        //    11: getfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    14: ifnull          71
        //    17: new             Ljava/util/ArrayList;
        //    20: astore_3       
        //    21: aload_3        
        //    22: invokespecial   java/util/ArrayList.<init>:()V
        //    25: aload_3        
        //    26: aload_0        
        //    27: getfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    30: invokeinterface java/util/List.addAll:(Ljava/util/Collection;)Z
        //    35: pop            
        //    36: aload_3        
        //    37: aload_1        
        //    38: invokeinterface java/util/List.addAll:(Ljava/util/Collection;)Z
        //    43: pop            
        //    44: aload_0        
        //    45: aload_3        
        //    46: putfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    49: aload_0        
        //    50: monitorexit    
        //    51: iload_2        
        //    52: ifeq            93
        //    55: return         
        //    56: aload_0        
        //    57: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
        //    60: ifeq            6
        //    63: new             Ljava/lang/AssertionError;
        //    66: dup            
        //    67: invokespecial   java/lang/AssertionError.<init>:()V
        //    70: athrow         
        //    71: aload_0        
        //    72: aload_1        
        //    73: putfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    76: aload_0        
        //    77: invokevirtual   okhttp3/internal/http2/Http2Stream.isOpen:()Z
        //    80: istore_2       
        //    81: aload_0        
        //    82: invokevirtual   java/lang/Object.notifyAll:()V
        //    85: goto            49
        //    88: astore_1       
        //    89: aload_0        
        //    90: monitorexit    
        //    91: aload_1        
        //    92: athrow         
        //    93: aload_0        
        //    94: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
        //    97: aload_0        
        //    98: getfield        okhttp3/internal/http2/Http2Stream.id:I
        //   101: invokevirtual   okhttp3/internal/http2/Http2Connection.removeStream:(I)Lokhttp3/internal/http2/Http2Stream;
        //   104: pop            
        //   105: goto            55
        //    Signature:
        //  (Ljava/util/List<Lokhttp3/internal/http2/Header;>;)V
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  10     49     88     93     Any
        //  49     51     88     93     Any
        //  71     85     88     93     Any
        //  89     91     88     93     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokestatic:boolean(Thread::holdsLock, this:Http2Stream[expected:Object]))
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
    
    void receiveRstStream(final ErrorCode errorCode) {
        synchronized (this) {
            if (this.errorCode == null) {
                this.errorCode = errorCode;
                this.notifyAll();
            }
        }
    }
    
    public void reply(final List<Header> p0, final boolean p1) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_3       
        //     2: getstatic       okhttp3/internal/http2/Http2Stream.$assertionsDisabled:Z
        //     5: ifeq            52
        //     8: aload_0        
        //     9: monitorenter   
        //    10: aload_1        
        //    11: ifnull          67
        //    14: aload_0        
        //    15: getfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    18: ifnonnull       84
        //    21: aload_0        
        //    22: aload_1        
        //    23: putfield        okhttp3/internal/http2/Http2Stream.responseHeaders:Ljava/util/List;
        //    26: iload_2        
        //    27: ifeq            96
        //    30: iload_3        
        //    31: istore_2       
        //    32: aload_0        
        //    33: monitorexit    
        //    34: aload_0        
        //    35: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
        //    38: aload_0        
        //    39: getfield        okhttp3/internal/http2/Http2Stream.id:I
        //    42: iload_2        
        //    43: aload_1        
        //    44: invokevirtual   okhttp3/internal/http2/Http2Connection.writeSynReply:(IZLjava/util/List;)V
        //    47: iload_2        
        //    48: ifne            109
        //    51: return         
        //    52: aload_0        
        //    53: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
        //    56: ifeq            8
        //    59: new             Ljava/lang/AssertionError;
        //    62: dup            
        //    63: invokespecial   java/lang/AssertionError.<init>:()V
        //    66: athrow         
        //    67: new             Ljava/lang/NullPointerException;
        //    70: astore_1       
        //    71: aload_1        
        //    72: ldc             "responseHeaders == null"
        //    74: invokespecial   java/lang/NullPointerException.<init>:(Ljava/lang/String;)V
        //    77: aload_1        
        //    78: athrow         
        //    79: astore_1       
        //    80: aload_0        
        //    81: monitorexit    
        //    82: aload_1        
        //    83: athrow         
        //    84: new             Ljava/lang/IllegalStateException;
        //    87: astore_1       
        //    88: aload_1        
        //    89: ldc             "reply already sent"
        //    91: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //    94: aload_1        
        //    95: athrow         
        //    96: aload_0        
        //    97: getfield        okhttp3/internal/http2/Http2Stream.sink:Lokhttp3/internal/http2/Http2Stream$FramedDataSink;
        //   100: iconst_1       
        //   101: putfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.finished:Z
        //   104: iconst_1       
        //   105: istore_2       
        //   106: goto            32
        //   109: aload_0        
        //   110: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
        //   113: invokevirtual   okhttp3/internal/http2/Http2Connection.flush:()V
        //   116: goto            51
        //    Exceptions:
        //  throws java.io.IOException
        //    Signature:
        //  (Ljava/util/List<Lokhttp3/internal/http2/Header;>;Z)V
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  14     26     79     84     Any
        //  32     34     79     84     Any
        //  67     79     79     84     Any
        //  80     82     79     84     Any
        //  84     96     79     84     Any
        //  96     104    79     84     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokestatic:boolean(Thread::holdsLock, this:Http2Stream[expected:Object]))
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
    
    void waitForIo() throws InterruptedIOException {
        try {
            this.wait();
        }
        catch (final InterruptedException ex) {
            throw new InterruptedIOException();
        }
    }
    
    public Timeout writeTimeout() {
        return this.writeTimeout;
    }
    
    final class FramedDataSink implements Sink
    {
        private static final long EMIT_BUFFER_SIZE = 16384L;
        boolean closed;
        boolean finished;
        private final Buffer sendBuffer;
        
        static {
            boolean $assertionsDisabled2 = false;
            if (!Http2Stream.class.desiredAssertionStatus()) {
                $assertionsDisabled2 = true;
            }
            $assertionsDisabled = $assertionsDisabled2;
        }
        
        FramedDataSink() {
            this.sendBuffer = new Buffer();
        }
        
        private void emitDataFrame(final boolean p0) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: istore_2       
            //     2: aload_0        
            //     3: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //     6: astore_3       
            //     7: aload_3        
            //     8: monitorenter   
            //     9: aload_0        
            //    10: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    13: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //    16: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.enter:()V
            //    19: aload_0        
            //    20: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    23: getfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //    26: lconst_0       
            //    27: lcmp           
            //    28: ifle            164
            //    31: iconst_1       
            //    32: istore          4
            //    34: iload           4
            //    36: ifne            50
            //    39: aload_0        
            //    40: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.finished:Z
            //    43: istore          5
            //    45: iload           5
            //    47: ifeq            170
            //    50: aload_0        
            //    51: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    54: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //    57: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //    60: aload_0        
            //    61: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    64: invokevirtual   okhttp3/internal/http2/Http2Stream.checkOutNotClosed:()V
            //    67: aload_0        
            //    68: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    71: getfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //    74: aload_0        
            //    75: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.sendBuffer:Lokio/Buffer;
            //    78: invokevirtual   okio/Buffer.size:()J
            //    81: invokestatic    java/lang/Math.min:(JJ)J
            //    84: lstore          6
            //    86: aload_0        
            //    87: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    90: astore          8
            //    92: aload           8
            //    94: aload           8
            //    96: getfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //    99: lload           6
            //   101: lsub           
            //   102: putfield        okhttp3/internal/http2/Http2Stream.bytesLeftInWriteWindow:J
            //   105: aload_3        
            //   106: monitorexit    
            //   107: aload_0        
            //   108: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   111: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //   114: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.enter:()V
            //   117: aload_0        
            //   118: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   121: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   124: astore_3       
            //   125: aload_0        
            //   126: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   129: getfield        okhttp3/internal/http2/Http2Stream.id:I
            //   132: istore          4
            //   134: iload_1        
            //   135: ifne            219
            //   138: iconst_0       
            //   139: istore_1       
            //   140: aload_3        
            //   141: iload           4
            //   143: iload_1        
            //   144: aload_0        
            //   145: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.sendBuffer:Lokio/Buffer;
            //   148: lload           6
            //   150: invokevirtual   okhttp3/internal/http2/Http2Connection.writeData:(IZLokio/Buffer;J)V
            //   153: aload_0        
            //   154: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   157: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //   160: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //   163: return         
            //   164: iconst_0       
            //   165: istore          4
            //   167: goto            34
            //   170: aload_0        
            //   171: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.closed:Z
            //   174: ifne            50
            //   177: aload_0        
            //   178: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   181: getfield        okhttp3/internal/http2/Http2Stream.errorCode:Lokhttp3/internal/http2/ErrorCode;
            //   184: ifnonnull       50
            //   187: aload_0        
            //   188: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   191: invokevirtual   okhttp3/internal/http2/Http2Stream.waitForIo:()V
            //   194: goto            19
            //   197: astore          8
            //   199: aload_0        
            //   200: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   203: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //   206: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //   209: aload           8
            //   211: athrow         
            //   212: astore          8
            //   214: aload_3        
            //   215: monitorexit    
            //   216: aload           8
            //   218: athrow         
            //   219: aload_0        
            //   220: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.sendBuffer:Lokio/Buffer;
            //   223: invokevirtual   okio/Buffer.size:()J
            //   226: lstore          9
            //   228: lload           6
            //   230: lload           9
            //   232: lcmp           
            //   233: ifne            138
            //   236: iload_2        
            //   237: istore_1       
            //   238: goto            140
            //   241: astore_3       
            //   242: aload_0        
            //   243: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   246: getfield        okhttp3/internal/http2/Http2Stream.writeTimeout:Lokhttp3/internal/http2/Http2Stream$StreamTimeout;
            //   249: invokevirtual   okhttp3/internal/http2/Http2Stream$StreamTimeout.exitAndThrowIfTimedOut:()V
            //   252: aload_3        
            //   253: athrow         
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  9      19     212    219    Any
            //  19     31     197    212    Any
            //  39     45     197    212    Any
            //  50     107    212    219    Any
            //  117    134    241    254    Any
            //  140    153    241    254    Any
            //  170    194    197    212    Any
            //  199    212    212    219    Any
            //  214    216    212    219    Any
            //  219    228    241    254    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0138:
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
        public void close() throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: ifeq            67
            //     6: aload_0        
            //     7: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    10: astore_1       
            //    11: aload_1        
            //    12: monitorenter   
            //    13: aload_0        
            //    14: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.closed:Z
            //    17: ifne            85
            //    20: aload_1        
            //    21: monitorexit    
            //    22: aload_0        
            //    23: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    26: getfield        okhttp3/internal/http2/Http2Stream.sink:Lokhttp3/internal/http2/Http2Stream$FramedDataSink;
            //    29: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.finished:Z
            //    32: ifeq            93
            //    35: aload_0        
            //    36: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    39: astore_2       
            //    40: aload_2        
            //    41: monitorenter   
            //    42: aload_0        
            //    43: iconst_1       
            //    44: putfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.closed:Z
            //    47: aload_2        
            //    48: monitorexit    
            //    49: aload_0        
            //    50: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    53: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //    56: invokevirtual   okhttp3/internal/http2/Http2Connection.flush:()V
            //    59: aload_0        
            //    60: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    63: invokevirtual   okhttp3/internal/http2/Http2Stream.cancelStreamIfNecessary:()V
            //    66: return         
            //    67: aload_0        
            //    68: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    71: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
            //    74: ifeq            6
            //    77: new             Ljava/lang/AssertionError;
            //    80: dup            
            //    81: invokespecial   java/lang/AssertionError.<init>:()V
            //    84: athrow         
            //    85: aload_1        
            //    86: monitorexit    
            //    87: return         
            //    88: astore_2       
            //    89: aload_1        
            //    90: monitorexit    
            //    91: aload_2        
            //    92: athrow         
            //    93: aload_0        
            //    94: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.sendBuffer:Lokio/Buffer;
            //    97: invokevirtual   okio/Buffer.size:()J
            //   100: lconst_0       
            //   101: lcmp           
            //   102: ifgt            137
            //   105: iconst_1       
            //   106: istore_3       
            //   107: iload_3        
            //   108: ifne            147
            //   111: aload_0        
            //   112: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.sendBuffer:Lokio/Buffer;
            //   115: invokevirtual   okio/Buffer.size:()J
            //   118: lconst_0       
            //   119: lcmp           
            //   120: ifgt            142
            //   123: iconst_1       
            //   124: istore_3       
            //   125: iload_3        
            //   126: ifne            35
            //   129: aload_0        
            //   130: iconst_1       
            //   131: invokespecial   okhttp3/internal/http2/Http2Stream$FramedDataSink.emitDataFrame:(Z)V
            //   134: goto            111
            //   137: iconst_0       
            //   138: istore_3       
            //   139: goto            107
            //   142: iconst_0       
            //   143: istore_3       
            //   144: goto            125
            //   147: aload_0        
            //   148: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   151: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   154: aload_0        
            //   155: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   158: getfield        okhttp3/internal/http2/Http2Stream.id:I
            //   161: iconst_1       
            //   162: aconst_null    
            //   163: lconst_0       
            //   164: invokevirtual   okhttp3/internal/http2/Http2Connection.writeData:(IZLokio/Buffer;J)V
            //   167: goto            35
            //   170: astore_1       
            //   171: aload_2        
            //   172: monitorexit    
            //   173: aload_1        
            //   174: athrow         
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  13     22     88     93     Any
            //  42     49     170    175    Any
            //  85     87     88     93     Any
            //  89     91     88     93     Any
            //  171    173    170    175    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0067:
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
        public void flush() throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: ifeq            58
            //     6: aload_0        
            //     7: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    10: astore_1       
            //    11: aload_1        
            //    12: monitorenter   
            //    13: aload_0        
            //    14: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    17: invokevirtual   okhttp3/internal/http2/Http2Stream.checkOutNotClosed:()V
            //    20: aload_1        
            //    21: monitorexit    
            //    22: aload_0        
            //    23: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.sendBuffer:Lokio/Buffer;
            //    26: invokevirtual   okio/Buffer.size:()J
            //    29: lconst_0       
            //    30: lcmp           
            //    31: ifgt            81
            //    34: iconst_1       
            //    35: istore_2       
            //    36: iload_2        
            //    37: ifne            86
            //    40: aload_0        
            //    41: iconst_0       
            //    42: invokespecial   okhttp3/internal/http2/Http2Stream$FramedDataSink.emitDataFrame:(Z)V
            //    45: aload_0        
            //    46: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    49: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //    52: invokevirtual   okhttp3/internal/http2/Http2Connection.flush:()V
            //    55: goto            22
            //    58: aload_0        
            //    59: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSink.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    62: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
            //    65: ifeq            6
            //    68: new             Ljava/lang/AssertionError;
            //    71: dup            
            //    72: invokespecial   java/lang/AssertionError.<init>:()V
            //    75: athrow         
            //    76: astore_3       
            //    77: aload_1        
            //    78: monitorexit    
            //    79: aload_3        
            //    80: athrow         
            //    81: iconst_0       
            //    82: istore_2       
            //    83: goto            36
            //    86: return         
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  13     22     76     81     Any
            //  77     79     76     81     Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokestatic:boolean(Thread::holdsLock, getfield:Http2Stream[expected:Object](FramedDataSink::this$0, this:FramedDataSink)))
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
        public Timeout timeout() {
            return Http2Stream.this.writeTimeout;
        }
        
        @Override
        public void write(final Buffer buffer, final long n) throws IOException {
            assert !Thread.holdsLock(Http2Stream.this);
            this.sendBuffer.write(buffer, n);
            while (true) {
                int n2;
                if (this.sendBuffer.size() < 16384L) {
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                if (n2 != 0) {
                    break;
                }
                this.emitDataFrame(false);
            }
        }
    }
    
    private final class FramedDataSource implements Source
    {
        boolean closed;
        boolean finished;
        private final long maxByteCount;
        private final Buffer readBuffer;
        private final Buffer receiveBuffer;
        
        static {
            boolean $assertionsDisabled2 = false;
            if (!Http2Stream.class.desiredAssertionStatus()) {
                $assertionsDisabled2 = true;
            }
            $assertionsDisabled = $assertionsDisabled2;
        }
        
        FramedDataSource(final long maxByteCount) {
            this.receiveBuffer = new Buffer();
            this.readBuffer = new Buffer();
            this.maxByteCount = maxByteCount;
        }
        
        private void checkNotClosed() throws IOException {
            if (this.closed) {
                throw new IOException("stream closed");
            }
            if (Http2Stream.this.errorCode == null) {
                return;
            }
            throw new StreamResetException(Http2Stream.this.errorCode);
        }
        
        private void waitUntilReadable() throws IOException {
            Http2Stream.this.readTimeout.enter();
            try {
                while (this.readBuffer.size() == 0L && !this.finished && !this.closed && Http2Stream.this.errorCode == null) {
                    Http2Stream.this.waitForIo();
                }
            }
            finally {
                Http2Stream.this.readTimeout.exitAndThrowIfTimedOut();
            }
        }
        
        @Override
        public void close() throws IOException {
            synchronized (Http2Stream.this) {
                this.closed = true;
                this.readBuffer.clear();
                Http2Stream.this.notifyAll();
                monitorexit(Http2Stream.this);
                Http2Stream.this.cancelStreamIfNecessary();
            }
        }
        
        @Override
        public long read(final Buffer p0, final long p1) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: istore          4
            //     3: lload_2        
            //     4: lconst_0       
            //     5: lcmp           
            //     6: iflt            44
            //     9: iconst_1       
            //    10: istore          5
            //    12: iload           5
            //    14: ifne            50
            //    17: new             Ljava/lang/IllegalArgumentException;
            //    20: dup            
            //    21: new             Ljava/lang/StringBuilder;
            //    24: dup            
            //    25: invokespecial   java/lang/StringBuilder.<init>:()V
            //    28: ldc             "byteCount < 0: "
            //    30: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    33: lload_2        
            //    34: invokevirtual   java/lang/StringBuilder.append:(J)Ljava/lang/StringBuilder;
            //    37: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //    40: invokespecial   java/lang/IllegalArgumentException.<init>:(Ljava/lang/String;)V
            //    43: athrow         
            //    44: iconst_0       
            //    45: istore          5
            //    47: goto            12
            //    50: aload_0        
            //    51: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    54: astore          6
            //    56: aload           6
            //    58: monitorenter   
            //    59: aload_0        
            //    60: invokespecial   okhttp3/internal/http2/Http2Stream$FramedDataSource.waitUntilReadable:()V
            //    63: aload_0        
            //    64: invokespecial   okhttp3/internal/http2/Http2Stream$FramedDataSource.checkNotClosed:()V
            //    67: aload_0        
            //    68: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.readBuffer:Lokio/Buffer;
            //    71: invokevirtual   okio/Buffer.size:()J
            //    74: lconst_0       
            //    75: lcmp           
            //    76: ifne            86
            //    79: aload           6
            //    81: monitorexit    
            //    82: ldc2_w          -1
            //    85: lreturn        
            //    86: aload_0        
            //    87: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.readBuffer:Lokio/Buffer;
            //    90: aload_1        
            //    91: lload_2        
            //    92: aload_0        
            //    93: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.readBuffer:Lokio/Buffer;
            //    96: invokevirtual   okio/Buffer.size:()J
            //    99: invokestatic    java/lang/Math.min:(JJ)J
            //   102: invokevirtual   okio/Buffer.read:(Lokio/Buffer;J)J
            //   105: lstore_2       
            //   106: aload_0        
            //   107: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   110: astore_1       
            //   111: aload_1        
            //   112: aload_1        
            //   113: getfield        okhttp3/internal/http2/Http2Stream.unacknowledgedBytesRead:J
            //   116: lload_2        
            //   117: ladd           
            //   118: putfield        okhttp3/internal/http2/Http2Stream.unacknowledgedBytesRead:J
            //   121: aload_0        
            //   122: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   125: getfield        okhttp3/internal/http2/Http2Stream.unacknowledgedBytesRead:J
            //   128: aload_0        
            //   129: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   132: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   135: getfield        okhttp3/internal/http2/Http2Connection.okHttpSettings:Lokhttp3/internal/http2/Settings;
            //   138: invokevirtual   okhttp3/internal/http2/Settings.getInitialWindowSize:()I
            //   141: iconst_2       
            //   142: idiv           
            //   143: i2l            
            //   144: lcmp           
            //   145: ifge            297
            //   148: iconst_1       
            //   149: istore          5
            //   151: iload           5
            //   153: ifne            188
            //   156: aload_0        
            //   157: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   160: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   163: aload_0        
            //   164: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   167: getfield        okhttp3/internal/http2/Http2Stream.id:I
            //   170: aload_0        
            //   171: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   174: getfield        okhttp3/internal/http2/Http2Stream.unacknowledgedBytesRead:J
            //   177: invokevirtual   okhttp3/internal/http2/Http2Connection.writeWindowUpdateLater:(IJ)V
            //   180: aload_0        
            //   181: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   184: lconst_0       
            //   185: putfield        okhttp3/internal/http2/Http2Stream.unacknowledgedBytesRead:J
            //   188: aload           6
            //   190: monitorexit    
            //   191: aload_0        
            //   192: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   195: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   198: astore_1       
            //   199: aload_1        
            //   200: monitorenter   
            //   201: aload_0        
            //   202: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   205: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   208: astore          6
            //   210: aload           6
            //   212: aload           6
            //   214: getfield        okhttp3/internal/http2/Http2Connection.unacknowledgedBytesRead:J
            //   217: lload_2        
            //   218: ladd           
            //   219: putfield        okhttp3/internal/http2/Http2Connection.unacknowledgedBytesRead:J
            //   222: aload_0        
            //   223: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   226: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   229: getfield        okhttp3/internal/http2/Http2Connection.unacknowledgedBytesRead:J
            //   232: aload_0        
            //   233: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   236: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   239: getfield        okhttp3/internal/http2/Http2Connection.okHttpSettings:Lokhttp3/internal/http2/Settings;
            //   242: invokevirtual   okhttp3/internal/http2/Settings.getInitialWindowSize:()I
            //   245: iconst_2       
            //   246: idiv           
            //   247: i2l            
            //   248: lcmp           
            //   249: ifge            309
            //   252: iload           4
            //   254: istore          5
            //   256: iload           5
            //   258: ifne            293
            //   261: aload_0        
            //   262: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   265: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   268: iconst_0       
            //   269: aload_0        
            //   270: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   273: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   276: getfield        okhttp3/internal/http2/Http2Connection.unacknowledgedBytesRead:J
            //   279: invokevirtual   okhttp3/internal/http2/Http2Connection.writeWindowUpdateLater:(IJ)V
            //   282: aload_0        
            //   283: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   286: getfield        okhttp3/internal/http2/Http2Stream.connection:Lokhttp3/internal/http2/Http2Connection;
            //   289: lconst_0       
            //   290: putfield        okhttp3/internal/http2/Http2Connection.unacknowledgedBytesRead:J
            //   293: aload_1        
            //   294: monitorexit    
            //   295: lload_2        
            //   296: lreturn        
            //   297: iconst_0       
            //   298: istore          5
            //   300: goto            151
            //   303: astore_1       
            //   304: aload           6
            //   306: monitorexit    
            //   307: aload_1        
            //   308: athrow         
            //   309: iconst_0       
            //   310: istore          5
            //   312: goto            256
            //   315: astore          6
            //   317: aload_1        
            //   318: monitorexit    
            //   319: aload           6
            //   321: athrow         
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  59     82     303    309    Any
            //  86     148    303    309    Any
            //  156    188    303    309    Any
            //  188    191    303    309    Any
            //  201    252    315    322    Any
            //  261    293    315    322    Any
            //  293    295    315    322    Any
            //  304    307    303    309    Any
            //  317    319    315    322    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0256:
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
        
        void receive(final BufferedSource p0, final long p1) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     6: lload_2        
            //     7: lconst_0       
            //     8: lcmp           
            //     9: ifgt            124
            //    12: iconst_1       
            //    13: istore          4
            //    15: iload           4
            //    17: ifne            248
            //    20: aload_0        
            //    21: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //    24: astore          5
            //    26: aload           5
            //    28: monitorenter   
            //    29: aload_0        
            //    30: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.finished:Z
            //    33: istore          6
            //    35: aload_0        
            //    36: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.readBuffer:Lokio/Buffer;
            //    39: invokevirtual   okio/Buffer.size:()J
            //    42: lload_2        
            //    43: ladd           
            //    44: aload_0        
            //    45: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.maxByteCount:J
            //    48: lcmp           
            //    49: ifgt            130
            //    52: iconst_1       
            //    53: istore          4
            //    55: iload           4
            //    57: ifne            136
            //    60: iconst_1       
            //    61: istore          4
            //    63: aload           5
            //    65: monitorexit    
            //    66: iload           4
            //    68: ifne            148
            //    71: iload           6
            //    73: ifne            166
            //    76: aload_1        
            //    77: aload_0        
            //    78: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.receiveBuffer:Lokio/Buffer;
            //    81: lload_2        
            //    82: invokeinterface okio/BufferedSource.read:(Lokio/Buffer;J)J
            //    87: lstore          7
            //    89: lload           7
            //    91: ldc2_w          -1
            //    94: lcmp           
            //    95: ifne            174
            //    98: new             Ljava/io/EOFException;
            //   101: dup            
            //   102: invokespecial   java/io/EOFException.<init>:()V
            //   105: athrow         
            //   106: aload_0        
            //   107: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   110: invokestatic    java/lang/Thread.holdsLock:(Ljava/lang/Object;)Z
            //   113: ifeq            6
            //   116: new             Ljava/lang/AssertionError;
            //   119: dup            
            //   120: invokespecial   java/lang/AssertionError.<init>:()V
            //   123: athrow         
            //   124: iconst_0       
            //   125: istore          4
            //   127: goto            15
            //   130: iconst_0       
            //   131: istore          4
            //   133: goto            55
            //   136: iconst_0       
            //   137: istore          4
            //   139: goto            63
            //   142: astore_1       
            //   143: aload           5
            //   145: monitorexit    
            //   146: aload_1        
            //   147: athrow         
            //   148: aload_1        
            //   149: lload_2        
            //   150: invokeinterface okio/BufferedSource.skip:(J)V
            //   155: aload_0        
            //   156: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   159: getstatic       okhttp3/internal/http2/ErrorCode.FLOW_CONTROL_ERROR:Lokhttp3/internal/http2/ErrorCode;
            //   162: invokevirtual   okhttp3/internal/http2/Http2Stream.closeLater:(Lokhttp3/internal/http2/ErrorCode;)V
            //   165: return         
            //   166: aload_1        
            //   167: lload_2        
            //   168: invokeinterface okio/BufferedSource.skip:(J)V
            //   173: return         
            //   174: lload_2        
            //   175: lload           7
            //   177: lsub           
            //   178: lstore_2       
            //   179: aload_0        
            //   180: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   183: astore          5
            //   185: aload           5
            //   187: monitorenter   
            //   188: aload_0        
            //   189: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.readBuffer:Lokio/Buffer;
            //   192: invokevirtual   okio/Buffer.size:()J
            //   195: lconst_0       
            //   196: lcmp           
            //   197: ifne            232
            //   200: iconst_1       
            //   201: istore          4
            //   203: aload_0        
            //   204: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.readBuffer:Lokio/Buffer;
            //   207: aload_0        
            //   208: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.receiveBuffer:Lokio/Buffer;
            //   211: invokevirtual   okio/Buffer.writeAll:(Lokio/Source;)J
            //   214: pop2           
            //   215: iload           4
            //   217: ifne            238
            //   220: aload           5
            //   222: monitorexit    
            //   223: goto            6
            //   226: astore_1       
            //   227: aload           5
            //   229: monitorexit    
            //   230: aload_1        
            //   231: athrow         
            //   232: iconst_0       
            //   233: istore          4
            //   235: goto            203
            //   238: aload_0        
            //   239: getfield        okhttp3/internal/http2/Http2Stream$FramedDataSource.this$0:Lokhttp3/internal/http2/Http2Stream;
            //   242: invokevirtual   java/lang/Object.notifyAll:()V
            //   245: goto            220
            //   248: return         
            //    Exceptions:
            //  throws java.io.IOException
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  29     52     142    148    Any
            //  63     66     142    148    Any
            //  143    146    142    148    Any
            //  188    200    226    232    Any
            //  203    215    226    232    Any
            //  220    223    226    232    Any
            //  227    230    226    232    Any
            //  238    245    226    232    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokestatic:boolean(Thread::holdsLock, getfield:Http2Stream[expected:Object](FramedDataSource::this$0, this:FramedDataSource)))
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
        public Timeout timeout() {
            return Http2Stream.this.readTimeout;
        }
    }
    
    class StreamTimeout extends AsyncTimeout
    {
        public void exitAndThrowIfTimedOut() throws IOException {
            if (!this.exit()) {
                return;
            }
            throw this.newTimeoutException(null);
        }
        
        @Override
        protected IOException newTimeoutException(final IOException cause) {
            final SocketTimeoutException ex = new SocketTimeoutException("timeout");
            if (cause != null) {
                ex.initCause(cause);
            }
            return ex;
        }
        
        @Override
        protected void timedOut() {
            Http2Stream.this.closeLater(ErrorCode.CANCEL);
        }
    }
}
