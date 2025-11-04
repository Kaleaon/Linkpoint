// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.security.cert.CertificateEncodingException;
import okio.BufferedSink;
import java.security.cert.CertificateException;
import java.util.Collections;
import okio.Buffer;
import java.util.ArrayList;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.util.List;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Source;
import okio.ForwardingSource;
import okio.ForwardingSink;
import okio.Sink;
import java.util.NoSuchElementException;
import okio.Okio;
import java.util.Iterator;
import okio.BufferedSource;
import okio.ByteString;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.CacheRequest;
import java.io.IOException;
import okhttp3.internal.io.FileSystem;
import java.io.File;
import okhttp3.internal.cache.InternalCache;
import okhttp3.internal.cache.DiskLruCache;
import java.io.Flushable;
import java.io.Closeable;

public final class Cache implements Closeable, Flushable
{
    private static final int ENTRY_BODY = 1;
    private static final int ENTRY_COUNT = 2;
    private static final int ENTRY_METADATA = 0;
    private static final int VERSION = 201105;
    final DiskLruCache cache;
    private int hitCount;
    final InternalCache internalCache;
    private int networkCount;
    private int requestCount;
    int writeAbortCount;
    int writeSuccessCount;
    
    public Cache(final File file, final long n) {
        this(file, n, FileSystem.SYSTEM);
    }
    
    Cache(final File file, final long n, final FileSystem fileSystem) {
        this.internalCache = new InternalCache() {
            @Override
            public Response get(final Request request) throws IOException {
                return Cache.this.get(request);
            }
            
            @Override
            public CacheRequest put(final Response response) throws IOException {
                return Cache.this.put(response);
            }
            
            @Override
            public void remove(final Request request) throws IOException {
                Cache.this.remove(request);
            }
            
            @Override
            public void trackConditionalCacheHit() {
                Cache.this.trackConditionalCacheHit();
            }
            
            @Override
            public void trackResponse(final CacheStrategy cacheStrategy) {
                Cache.this.trackResponse(cacheStrategy);
            }
            
            @Override
            public void update(final Response response, final Response response2) {
                Cache.this.update(response, response2);
            }
        };
        this.cache = DiskLruCache.create(fileSystem, file, 201105, 2, n);
    }
    
    private void abortQuietly(final DiskLruCache.Editor editor) {
        if (editor != null) {
            try {
                editor.abort();
            }
            catch (final IOException ex) {}
        }
    }
    
    public static String key(final HttpUrl httpUrl) {
        return ByteString.encodeUtf8(httpUrl.toString()).md5().hex();
    }
    
    static int readInt(final BufferedSource bufferedSource) throws IOException {
        final int n = 1;
        try {
            final long decimalLong = bufferedSource.readDecimalLong();
            final String utf8LineStrict = bufferedSource.readUtf8LineStrict();
            int n2;
            if (decimalLong < 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                int n3;
                if (decimalLong > 2147483647L) {
                    n3 = n;
                }
                else {
                    n3 = 0;
                }
                if (n3 == 0 && utf8LineStrict.isEmpty()) {
                    return (int)decimalLong;
                }
            }
            throw new IOException("expected an int but was \"" + decimalLong + utf8LineStrict + "\"");
        }
        catch (final NumberFormatException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public void close() throws IOException {
        this.cache.close();
    }
    
    public void delete() throws IOException {
        this.cache.delete();
    }
    
    public File directory() {
        return this.cache.getDirectory();
    }
    
    public void evictAll() throws IOException {
        this.cache.evictAll();
    }
    
    @Override
    public void flush() throws IOException {
        this.cache.flush();
    }
    
    Response get(final Request p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     4: invokestatic    okhttp3/Cache.key:(Lokhttp3/HttpUrl;)Ljava/lang/String;
        //     7: astore_2       
        //     8: aload_0        
        //     9: getfield        okhttp3/Cache.cache:Lokhttp3/internal/cache/DiskLruCache;
        //    12: aload_2        
        //    13: invokevirtual   okhttp3/internal/cache/DiskLruCache.get:(Ljava/lang/String;)Lokhttp3/internal/cache/DiskLruCache$Snapshot;
        //    16: astore_2       
        //    17: aload_2        
        //    18: ifnull          51
        //    21: new             Lokhttp3/Cache$Entry;
        //    24: dup            
        //    25: aload_2        
        //    26: iconst_0       
        //    27: invokevirtual   okhttp3/internal/cache/DiskLruCache$Snapshot.getSource:(I)Lokio/Source;
        //    30: invokespecial   okhttp3/Cache$Entry.<init>:(Lokio/Source;)V
        //    33: astore_3       
        //    34: aload_3        
        //    35: aload_2        
        //    36: invokevirtual   okhttp3/Cache$Entry.response:(Lokhttp3/internal/cache/DiskLruCache$Snapshot;)Lokhttp3/Response;
        //    39: astore_2       
        //    40: aload_3        
        //    41: aload_1        
        //    42: aload_2        
        //    43: invokevirtual   okhttp3/Cache$Entry.matches:(Lokhttp3/Request;Lokhttp3/Response;)Z
        //    46: ifeq            63
        //    49: aload_2        
        //    50: areturn        
        //    51: aconst_null    
        //    52: areturn        
        //    53: astore_1       
        //    54: aconst_null    
        //    55: areturn        
        //    56: astore_1       
        //    57: aload_2        
        //    58: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //    61: aconst_null    
        //    62: areturn        
        //    63: aload_2        
        //    64: invokevirtual   okhttp3/Response.body:()Lokhttp3/ResponseBody;
        //    67: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //    70: aconst_null    
        //    71: areturn        
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  8      17     53     56     Ljava/io/IOException;
        //  21     34     56     63     Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0051:
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
    
    public int hitCount() {
        synchronized (this) {
            return this.hitCount;
        }
    }
    
    public void initialize() throws IOException {
        this.cache.initialize();
    }
    
    public boolean isClosed() {
        return this.cache.isClosed();
    }
    
    public long maxSize() {
        return this.cache.getMaxSize();
    }
    
    public int networkCount() {
        synchronized (this) {
            return this.networkCount;
        }
    }
    
    CacheRequest put(final Response p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     4: invokevirtual   okhttp3/Request.method:()Ljava/lang/String;
        //     7: astore_2       
        //     8: aload_1        
        //     9: invokevirtual   okhttp3/Response.request:()Lokhttp3/Request;
        //    12: invokevirtual   okhttp3/Request.method:()Ljava/lang/String;
        //    15: invokestatic    okhttp3/internal/http/HttpMethod.invalidatesCache:(Ljava/lang/String;)Z
        //    18: ifne            85
        //    21: aload_2        
        //    22: ldc             "GET"
        //    24: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    27: ifeq            95
        //    30: aload_1        
        //    31: invokestatic    okhttp3/internal/http/HttpHeaders.hasVaryAll:(Lokhttp3/Response;)Z
        //    34: ifne            97
        //    37: new             Lokhttp3/Cache$Entry;
        //    40: dup            
        //    41: aload_1        
        //    42: invokespecial   okhttp3/Cache$Entry.<init>:(Lokhttp3/Response;)V
        //    45: astore_2       
        //    46: aload_0        
        //    47: getfield        okhttp3/Cache.cache:Lokhttp3/internal/cache/DiskLruCache;
        //    50: aload_1        
        //    51: invokevirtual   okhttp3/Response.request:()Lokhttp3/Request;
        //    54: invokevirtual   okhttp3/Request.url:()Lokhttp3/HttpUrl;
        //    57: invokestatic    okhttp3/Cache.key:(Lokhttp3/HttpUrl;)Ljava/lang/String;
        //    60: invokevirtual   okhttp3/internal/cache/DiskLruCache.edit:(Ljava/lang/String;)Lokhttp3/internal/cache/DiskLruCache$Editor;
        //    63: astore_1       
        //    64: aload_1        
        //    65: ifnull          99
        //    68: aload_2        
        //    69: aload_1        
        //    70: invokevirtual   okhttp3/Cache$Entry.writeTo:(Lokhttp3/internal/cache/DiskLruCache$Editor;)V
        //    73: new             Lokhttp3/Cache$CacheRequestImpl;
        //    76: dup            
        //    77: aload_0        
        //    78: aload_1        
        //    79: invokespecial   okhttp3/Cache$CacheRequestImpl.<init>:(Lokhttp3/Cache;Lokhttp3/internal/cache/DiskLruCache$Editor;)V
        //    82: astore_2       
        //    83: aload_2        
        //    84: areturn        
        //    85: aload_0        
        //    86: aload_1        
        //    87: invokevirtual   okhttp3/Response.request:()Lokhttp3/Request;
        //    90: invokevirtual   okhttp3/Cache.remove:(Lokhttp3/Request;)V
        //    93: aconst_null    
        //    94: areturn        
        //    95: aconst_null    
        //    96: areturn        
        //    97: aconst_null    
        //    98: areturn        
        //    99: aconst_null    
        //   100: areturn        
        //   101: astore_1       
        //   102: aconst_null    
        //   103: astore_1       
        //   104: aload_0        
        //   105: aload_1        
        //   106: invokespecial   okhttp3/Cache.abortQuietly:(Lokhttp3/internal/cache/DiskLruCache$Editor;)V
        //   109: aconst_null    
        //   110: areturn        
        //   111: astore_2       
        //   112: goto            104
        //   115: astore_1       
        //   116: goto            93
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  46     64     101    104    Ljava/io/IOException;
        //  68     83     111    115    Ljava/io/IOException;
        //  85     93     115    119    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 66 out of bounds for length 66
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
    
    void remove(final Request request) throws IOException {
        this.cache.remove(key(request.url()));
    }
    
    public int requestCount() {
        synchronized (this) {
            return this.requestCount;
        }
    }
    
    public long size() throws IOException {
        return this.cache.size();
    }
    
    void trackConditionalCacheHit() {
        synchronized (this) {
            ++this.hitCount;
        }
    }
    
    void trackResponse(final CacheStrategy cacheStrategy) {
        while (true) {
            Label_0049: {
                synchronized (this) {
                    ++this.requestCount;
                    if (cacheStrategy.networkRequest == null) {
                        if (cacheStrategy.cacheResponse != null) {
                            break Label_0049;
                        }
                    }
                    else {
                        ++this.networkCount;
                    }
                    return;
                }
            }
            ++this.hitCount;
        }
    }
    
    void update(Response edit, final Response response) {
        final Entry entry = new Entry(response);
        final DiskLruCache.Snapshot snapshot = ((CacheResponseBody)edit.body()).snapshot;
        while (true) {
            try {
                edit = (Response)snapshot.edit();
                if (edit != null) {
                    try {
                        entry.writeTo((DiskLruCache.Editor)edit);
                        ((DiskLruCache.Editor)edit).commit();
                        return;
                    }
                    catch (final IOException ex) {}
                    this.abortQuietly((DiskLruCache.Editor)edit);
                }
            }
            catch (final IOException ex2) {
                edit = null;
                continue;
            }
            break;
        }
    }
    
    public Iterator<String> urls() throws IOException {
        return new Iterator<String>() {
            boolean canRemove;
            final Iterator<DiskLruCache.Snapshot> delegate = Cache.this.cache.snapshots();
            String nextUrl;
            
            @Override
            public boolean hasNext() {
                if (this.nextUrl == null) {
                    this.canRemove = false;
                    while (this.delegate.hasNext()) {
                        final DiskLruCache.Snapshot snapshot = this.delegate.next();
                        try {
                            this.nextUrl = Okio.buffer(snapshot.getSource(0)).readUtf8LineStrict();
                            return true;
                        }
                        catch (final IOException ex) {}
                        finally {
                            snapshot.close();
                        }
                    }
                    return false;
                }
                return true;
            }
            
            @Override
            public String next() {
                if (this.hasNext()) {
                    final String nextUrl = this.nextUrl;
                    this.nextUrl = null;
                    this.canRemove = true;
                    return nextUrl;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                if (this.canRemove) {
                    this.delegate.remove();
                    return;
                }
                throw new IllegalStateException("remove() before next()");
            }
        };
    }
    
    public int writeAbortCount() {
        synchronized (this) {
            return this.writeAbortCount;
        }
    }
    
    public int writeSuccessCount() {
        synchronized (this) {
            return this.writeSuccessCount;
        }
    }
    
    private final class CacheRequestImpl implements CacheRequest
    {
        private Sink body;
        private Sink cacheOut;
        boolean done;
        private final DiskLruCache.Editor editor;
        
        public CacheRequestImpl(final DiskLruCache.Editor editor) {
            this.editor = editor;
            this.cacheOut = editor.newSink(1);
            this.body = new ForwardingSink(this.cacheOut) {
                @Override
                public void close() throws IOException {
                    synchronized (Cache.this) {
                        if (!CacheRequestImpl.this.done) {
                            CacheRequestImpl.this.done = true;
                            final Cache this$0 = Cache.this;
                            ++this$0.writeSuccessCount;
                            monitorexit(Cache.this);
                            super.close();
                            editor.commit();
                        }
                    }
                }
            };
        }
        
        @Override
        public void abort() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        okhttp3/Cache$CacheRequestImpl.this$0:Lokhttp3/Cache;
            //     4: astore_1       
            //     5: aload_1        
            //     6: monitorenter   
            //     7: aload_0        
            //     8: getfield        okhttp3/Cache$CacheRequestImpl.done:Z
            //    11: ifne            51
            //    14: aload_0        
            //    15: iconst_1       
            //    16: putfield        okhttp3/Cache$CacheRequestImpl.done:Z
            //    19: aload_0        
            //    20: getfield        okhttp3/Cache$CacheRequestImpl.this$0:Lokhttp3/Cache;
            //    23: astore_2       
            //    24: aload_2        
            //    25: aload_2        
            //    26: getfield        okhttp3/Cache.writeAbortCount:I
            //    29: iconst_1       
            //    30: iadd           
            //    31: putfield        okhttp3/Cache.writeAbortCount:I
            //    34: aload_1        
            //    35: monitorexit    
            //    36: aload_0        
            //    37: getfield        okhttp3/Cache$CacheRequestImpl.cacheOut:Lokio/Sink;
            //    40: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
            //    43: aload_0        
            //    44: getfield        okhttp3/Cache$CacheRequestImpl.editor:Lokhttp3/internal/cache/DiskLruCache$Editor;
            //    47: invokevirtual   okhttp3/internal/cache/DiskLruCache$Editor.abort:()V
            //    50: return         
            //    51: aload_1        
            //    52: monitorexit    
            //    53: return         
            //    54: astore_2       
            //    55: aload_1        
            //    56: monitorexit    
            //    57: aload_2        
            //    58: athrow         
            //    59: astore_1       
            //    60: goto            50
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                 
            //  -----  -----  -----  -----  ---------------------
            //  7      36     54     59     Any
            //  43     50     59     63     Ljava/io/IOException;
            //  51     53     54     59     Any
            //  55     57     54     59     Any
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
        public Sink body() {
            return this.body;
        }
    }
    
    private static class CacheResponseBody extends ResponseBody
    {
        private final BufferedSource bodySource;
        private final String contentLength;
        private final String contentType;
        final DiskLruCache.Snapshot snapshot;
        
        public CacheResponseBody(final DiskLruCache.Snapshot snapshot, final String contentType, final String contentLength) {
            this.snapshot = snapshot;
            this.contentType = contentType;
            this.contentLength = contentLength;
            this.bodySource = Okio.buffer(new ForwardingSource(snapshot.getSource(1)) {
                @Override
                public void close() throws IOException {
                    snapshot.close();
                    super.close();
                }
            });
        }
        
        @Override
        public long contentLength() {
            long long1 = -1L;
            try {
                if (this.contentLength != null) {
                    long1 = Long.parseLong(this.contentLength);
                }
                return long1;
            }
            catch (final NumberFormatException ex) {
                return -1L;
            }
        }
        
        @Override
        public MediaType contentType() {
            MediaType parse = null;
            if (this.contentType != null) {
                parse = MediaType.parse(this.contentType);
            }
            return parse;
        }
        
        @Override
        public BufferedSource source() {
            return this.bodySource;
        }
    }
    
    private static final class Entry
    {
        private static final String RECEIVED_MILLIS;
        private static final String SENT_MILLIS;
        private final int code;
        private final Handshake handshake;
        private final String message;
        private final Protocol protocol;
        private final long receivedResponseMillis;
        private final String requestMethod;
        private final Headers responseHeaders;
        private final long sentRequestMillis;
        private final String url;
        private final Headers varyHeaders;
        
        static {
            SENT_MILLIS = Platform.get().getPrefix() + "-Sent-Millis";
            RECEIVED_MILLIS = Platform.get().getPrefix() + "-Received-Millis";
        }
        
        public Entry(final Response response) {
            this.url = response.request().url().toString();
            this.varyHeaders = HttpHeaders.varyHeaders(response);
            this.requestMethod = response.request().method();
            this.protocol = response.protocol();
            this.code = response.code();
            this.message = response.message();
            this.responseHeaders = response.headers();
            this.handshake = response.handshake();
            this.sentRequestMillis = response.sentRequestAtMillis();
            this.receivedResponseMillis = response.receivedResponseAtMillis();
        }
        
        public Entry(final Source source) throws IOException {
            Object o;
            while (true) {
                final long n = 0L;
                TlsVersion forJavaName = null;
                final int n2 = 0;
                while (true) {
                    BufferedSource buffer = null;
                    Label_0422: {
                        try {
                            buffer = Okio.buffer(source);
                            this.url = buffer.readUtf8LineStrict();
                            this.requestMethod = buffer.readUtf8LineStrict();
                            o = new Headers.Builder();
                            for (int int1 = Cache.readInt(buffer), i = 0; i < int1; ++i) {
                                ((Headers.Builder)o).addLenient(buffer.readUtf8LineStrict());
                            }
                            this.varyHeaders = ((Headers.Builder)o).build();
                            o = StatusLine.parse(buffer.readUtf8LineStrict());
                            this.protocol = ((StatusLine)o).protocol;
                            this.code = ((StatusLine)o).code;
                            this.message = ((StatusLine)o).message;
                            Object certificateList = new Headers.Builder();
                            for (int int2 = Cache.readInt(buffer), j = n2; j < int2; ++j) {
                                ((Headers.Builder)certificateList).addLenient(buffer.readUtf8LineStrict());
                            }
                            o = ((Headers.Builder)certificateList).get(Entry.SENT_MILLIS);
                            Object s = ((Headers.Builder)certificateList).get(Entry.RECEIVED_MILLIS);
                            ((Headers.Builder)certificateList).removeAll(Entry.SENT_MILLIS);
                            ((Headers.Builder)certificateList).removeAll(Entry.RECEIVED_MILLIS);
                            long long1;
                            if (o == null) {
                                long1 = 0L;
                            }
                            else {
                                long1 = Long.parseLong((String)o);
                            }
                            this.sentRequestMillis = long1;
                            long long2;
                            if (s == null) {
                                long2 = n;
                            }
                            else {
                                long2 = Long.parseLong((String)s);
                            }
                            this.receivedResponseMillis = long2;
                            this.responseHeaders = ((Headers.Builder)certificateList).build();
                            if (!this.isHttps()) {
                                this.handshake = null;
                            }
                            else {
                                o = buffer.readUtf8LineStrict();
                                if (((String)o).length() > 0) {
                                    break;
                                }
                                s = CipherSuite.forJavaName(buffer.readUtf8LineStrict());
                                o = this.readCertificateList(buffer);
                                certificateList = this.readCertificateList(buffer);
                                if (!buffer.exhausted()) {
                                    break Label_0422;
                                }
                                this.handshake = Handshake.get(forJavaName, (CipherSuite)s, (List<Certificate>)o, (List<Certificate>)certificateList);
                            }
                            return;
                        }
                        finally {
                            source.close();
                        }
                        break;
                    }
                    forJavaName = TlsVersion.forJavaName(buffer.readUtf8LineStrict());
                    continue;
                }
            }
            throw new IOException("expected \"\" but was \"" + (String)o + "\"");
        }
        
        private boolean isHttps() {
            return this.url.startsWith("https://");
        }
        
        private List<Certificate> readCertificateList(final BufferedSource bufferedSource) throws IOException {
            final int int1 = Cache.readInt(bufferedSource);
            Label_0039: {
                if (int1 == -1) {
                    break Label_0039;
                }
                try {
                    final CertificateFactory instance = CertificateFactory.getInstance("X.509");
                    final ArrayList list = new ArrayList(int1);
                    for (int i = 0; i < int1; ++i) {
                        final String utf8LineStrict = bufferedSource.readUtf8LineStrict();
                        final Buffer buffer = new Buffer();
                        buffer.write(ByteString.decodeBase64(utf8LineStrict));
                        list.add((Object)instance.generateCertificate(buffer.inputStream()));
                    }
                    return (List<Certificate>)list;
                    return Collections.emptyList();
                }
                catch (final CertificateException ex) {
                    throw new IOException(ex.getMessage());
                }
            }
        }
        
        private void writeCertList(final BufferedSink bufferedSink, final List<Certificate> list) throws IOException {
            try {
                bufferedSink.writeDecimalLong(list.size()).writeByte(10);
                for (int size = list.size(), i = 0; i < size; ++i) {
                    bufferedSink.writeUtf8(ByteString.of(((Certificate)list.get(i)).getEncoded()).base64()).writeByte(10);
                }
            }
            catch (final CertificateEncodingException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        
        public boolean matches(final Request request, final Response response) {
            final boolean b = false;
            boolean b2;
            if (!this.url.equals(request.url().toString())) {
                b2 = b;
            }
            else {
                b2 = b;
                if (this.requestMethod.equals(request.method())) {
                    b2 = b;
                    if (HttpHeaders.varyMatches(response, this.varyHeaders, request)) {
                        b2 = true;
                    }
                }
            }
            return b2;
        }
        
        public Response response(final DiskLruCache.Snapshot snapshot) {
            return new Response.Builder().request(new Request.Builder().url(this.url).method(this.requestMethod, null).headers(this.varyHeaders).build()).protocol(this.protocol).code(this.code).message(this.message).headers(this.responseHeaders).body(new CacheResponseBody(snapshot, this.responseHeaders.get("Content-Type"), this.responseHeaders.get("Content-Length"))).handshake(this.handshake).sentRequestAtMillis(this.sentRequestMillis).receivedResponseAtMillis(this.receivedResponseMillis).build();
        }
        
        public void writeTo(final DiskLruCache.Editor editor) throws IOException {
            final int n = 0;
            final BufferedSink buffer = Okio.buffer(editor.newSink(0));
            buffer.writeUtf8(this.url).writeByte(10);
            buffer.writeUtf8(this.requestMethod).writeByte(10);
            buffer.writeDecimalLong(this.varyHeaders.size()).writeByte(10);
            for (int size = this.varyHeaders.size(), i = 0; i < size; ++i) {
                buffer.writeUtf8(this.varyHeaders.name(i)).writeUtf8(": ").writeUtf8(this.varyHeaders.value(i)).writeByte(10);
            }
            buffer.writeUtf8(new StatusLine(this.protocol, this.code, this.message).toString()).writeByte(10);
            buffer.writeDecimalLong(this.responseHeaders.size() + 2).writeByte(10);
            for (int size2 = this.responseHeaders.size(), j = n; j < size2; ++j) {
                buffer.writeUtf8(this.responseHeaders.name(j)).writeUtf8(": ").writeUtf8(this.responseHeaders.value(j)).writeByte(10);
            }
            buffer.writeUtf8(Entry.SENT_MILLIS).writeUtf8(": ").writeDecimalLong(this.sentRequestMillis).writeByte(10);
            buffer.writeUtf8(Entry.RECEIVED_MILLIS).writeUtf8(": ").writeDecimalLong(this.receivedResponseMillis).writeByte(10);
            if (this.isHttps()) {
                buffer.writeByte(10);
                buffer.writeUtf8(this.handshake.cipherSuite().javaName()).writeByte(10);
                this.writeCertList(buffer, this.handshake.peerCertificates());
                this.writeCertList(buffer, this.handshake.localCertificates());
                if (this.handshake.tlsVersion() != null) {
                    buffer.writeUtf8(this.handshake.tlsVersion().javaName()).writeByte(10);
                }
            }
            buffer.close();
        }
    }
}
