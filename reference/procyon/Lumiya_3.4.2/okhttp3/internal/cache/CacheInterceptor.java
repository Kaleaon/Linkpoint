// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.cache;

import okhttp3.internal.http.HttpMethod;
import okhttp3.Request;
import okhttp3.internal.Internal;
import okhttp3.Headers;
import okio.Sink;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.Timeout;
import okio.Buffer;
import java.io.IOException;
import okhttp3.internal.Util;
import java.util.concurrent.TimeUnit;
import okio.BufferedSource;
import okio.BufferedSink;
import okio.Source;
import okio.Okio;
import okhttp3.Response;
import okhttp3.Interceptor;

public final class CacheInterceptor implements Interceptor
{
    final InternalCache cache;
    
    public CacheInterceptor(final InternalCache cache) {
        this.cache = cache;
    }
    
    private Response cacheWritingResponse(final CacheRequest cacheRequest, final Response response) throws IOException {
        if (cacheRequest == null) {
            return response;
        }
        final Sink body = cacheRequest.body();
        if (body != null) {
            return response.newBuilder().body(new RealResponseBody(response.headers(), Okio.buffer(new Source() {
                boolean cacheRequestClosed;
                final /* synthetic */ BufferedSink val$cacheBody = Okio.buffer(body);
                final /* synthetic */ BufferedSource val$source = response.body().source();
                
                @Override
                public void close() throws IOException {
                    if (!this.cacheRequestClosed && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
                        this.cacheRequestClosed = true;
                        cacheRequest.abort();
                    }
                    this.val$source.close();
                }
                
                @Override
                public long read(final Buffer buffer, long read) throws IOException {
                    while (true) {
                        try {
                            read = this.val$source.read(buffer, read);
                            if (read != -1L) {
                                break;
                            }
                            if (this.cacheRequestClosed) {
                                return -1L;
                            }
                        }
                        catch (final IOException ex) {
                            if (!this.cacheRequestClosed) {
                                this.cacheRequestClosed = true;
                                cacheRequest.abort();
                            }
                            throw ex;
                        }
                        this.cacheRequestClosed = true;
                        this.val$cacheBody.close();
                        return -1L;
                    }
                    buffer.copyTo(this.val$cacheBody.buffer(), buffer.size() - read, read);
                    this.val$cacheBody.emitCompleteSegments();
                    return read;
                }
                
                @Override
                public Timeout timeout() {
                    return this.val$source.timeout();
                }
            }))).build();
        }
        return response;
    }
    
    private static Headers combine(final Headers headers, final Headers headers2) {
        final int n = 0;
        final Headers.Builder builder = new Headers.Builder();
        for (int size = headers.size(), i = 0; i < size; ++i) {
            final String name = headers.name(i);
            final String value = headers.value(i);
            if (!"Warning".equalsIgnoreCase(name) || !value.startsWith("1")) {
                if (!isEndToEnd(name) || headers2.get(name) == null) {
                    Internal.instance.addLenient(builder, name, value);
                }
            }
        }
        for (int size2 = headers2.size(), j = n; j < size2; ++j) {
            final String name2 = headers2.name(j);
            if (!"Content-Length".equalsIgnoreCase(name2) && isEndToEnd(name2)) {
                Internal.instance.addLenient(builder, name2, headers2.value(j));
            }
        }
        return builder.build();
    }
    
    static boolean isEndToEnd(final String s) {
        final boolean b = false;
        boolean b2;
        if ("Connection".equalsIgnoreCase(s)) {
            b2 = b;
        }
        else {
            b2 = b;
            if (!"Keep-Alive".equalsIgnoreCase(s)) {
                b2 = b;
                if (!"Proxy-Authenticate".equalsIgnoreCase(s)) {
                    b2 = b;
                    if (!"Proxy-Authorization".equalsIgnoreCase(s)) {
                        b2 = b;
                        if (!"TE".equalsIgnoreCase(s)) {
                            b2 = b;
                            if (!"Trailers".equalsIgnoreCase(s)) {
                                b2 = b;
                                if (!"Transfer-Encoding".equalsIgnoreCase(s)) {
                                    b2 = b;
                                    if (!"Upgrade".equalsIgnoreCase(s)) {
                                        b2 = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    private CacheRequest maybeCache(final Response response, final Request request, final InternalCache internalCache) throws IOException {
        if (internalCache == null) {
            return null;
        }
        if (CacheStrategy.isCacheable(response, request)) {
            return internalCache.put(response);
        }
        if (HttpMethod.invalidatesCache(request.method())) {
            try {
                internalCache.remove(request);
            }
            catch (final IOException ex) {}
        }
        return null;
    }
    
    private static Response stripBody(final Response response) {
        Response build;
        if (response == null) {
            build = response;
        }
        else {
            build = response;
            if (response.body() != null) {
                build = response.newBuilder().body(null).build();
            }
        }
        return build;
    }
    
    @Override
    public Response intercept(final Chain p0) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        okhttp3/internal/cache/CacheInterceptor.cache:Lokhttp3/internal/cache/InternalCache;
        //     4: ifnonnull       113
        //     7: aconst_null    
        //     8: astore_2       
        //     9: new             Lokhttp3/internal/cache/CacheStrategy$Factory;
        //    12: dup            
        //    13: invokestatic    java/lang/System.currentTimeMillis:()J
        //    16: aload_1        
        //    17: invokeinterface okhttp3/Interceptor$Chain.request:()Lokhttp3/Request;
        //    22: aload_2        
        //    23: invokespecial   okhttp3/internal/cache/CacheStrategy$Factory.<init>:(JLokhttp3/Request;Lokhttp3/Response;)V
        //    26: invokevirtual   okhttp3/internal/cache/CacheStrategy$Factory.get:()Lokhttp3/internal/cache/CacheStrategy;
        //    29: astore_3       
        //    30: aload_3        
        //    31: getfield        okhttp3/internal/cache/CacheStrategy.networkRequest:Lokhttp3/Request;
        //    34: astore          4
        //    36: aload_3        
        //    37: getfield        okhttp3/internal/cache/CacheStrategy.cacheResponse:Lokhttp3/Response;
        //    40: astore          5
        //    42: aload_0        
        //    43: getfield        okhttp3/internal/cache/CacheInterceptor.cache:Lokhttp3/internal/cache/InternalCache;
        //    46: ifnonnull       132
        //    49: aload_2        
        //    50: ifnonnull       145
        //    53: aload           4
        //    55: ifnull          160
        //    58: aload           4
        //    60: ifnull          220
        //    63: aload_1        
        //    64: aload           4
        //    66: invokeinterface okhttp3/Interceptor$Chain.proceed:(Lokhttp3/Request;)Lokhttp3/Response;
        //    71: astore_3       
        //    72: aload_3        
        //    73: ifnull          237
        //    76: aload           5
        //    78: ifnonnull       272
        //    81: aload_3        
        //    82: invokevirtual   okhttp3/Response.newBuilder:()Lokhttp3/Response$Builder;
        //    85: aload           5
        //    87: invokestatic    okhttp3/internal/cache/CacheInterceptor.stripBody:(Lokhttp3/Response;)Lokhttp3/Response;
        //    90: invokevirtual   okhttp3/Response$Builder.cacheResponse:(Lokhttp3/Response;)Lokhttp3/Response$Builder;
        //    93: aload_3        
        //    94: invokestatic    okhttp3/internal/cache/CacheInterceptor.stripBody:(Lokhttp3/Response;)Lokhttp3/Response;
        //    97: invokevirtual   okhttp3/Response$Builder.networkResponse:(Lokhttp3/Response;)Lokhttp3/Response$Builder;
        //   100: invokevirtual   okhttp3/Response$Builder.build:()Lokhttp3/Response;
        //   103: astore_1       
        //   104: aload_1        
        //   105: invokestatic    okhttp3/internal/http/HttpHeaders.hasBody:(Lokhttp3/Response;)Z
        //   108: ifne            376
        //   111: aload_1        
        //   112: areturn        
        //   113: aload_0        
        //   114: getfield        okhttp3/internal/cache/CacheInterceptor.cache:Lokhttp3/internal/cache/InternalCache;
        //   117: aload_1        
        //   118: invokeinterface okhttp3/Interceptor$Chain.request:()Lokhttp3/Request;
        //   123: invokeinterface okhttp3/internal/cache/InternalCache.get:(Lokhttp3/Request;)Lokhttp3/Response;
        //   128: astore_2       
        //   129: goto            9
        //   132: aload_0        
        //   133: getfield        okhttp3/internal/cache/CacheInterceptor.cache:Lokhttp3/internal/cache/InternalCache;
        //   136: aload_3        
        //   137: invokeinterface okhttp3/internal/cache/InternalCache.trackResponse:(Lokhttp3/internal/cache/CacheStrategy;)V
        //   142: goto            49
        //   145: aload           5
        //   147: ifnonnull       53
        //   150: aload_2        
        //   151: invokevirtual   okhttp3/Response.body:()Lokhttp3/ResponseBody;
        //   154: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //   157: goto            53
        //   160: aload           5
        //   162: ifnonnull       58
        //   165: new             Lokhttp3/Response$Builder;
        //   168: dup            
        //   169: invokespecial   okhttp3/Response$Builder.<init>:()V
        //   172: aload_1        
        //   173: invokeinterface okhttp3/Interceptor$Chain.request:()Lokhttp3/Request;
        //   178: invokevirtual   okhttp3/Response$Builder.request:(Lokhttp3/Request;)Lokhttp3/Response$Builder;
        //   181: getstatic       okhttp3/Protocol.HTTP_1_1:Lokhttp3/Protocol;
        //   184: invokevirtual   okhttp3/Response$Builder.protocol:(Lokhttp3/Protocol;)Lokhttp3/Response$Builder;
        //   187: sipush          504
        //   190: invokevirtual   okhttp3/Response$Builder.code:(I)Lokhttp3/Response$Builder;
        //   193: ldc             "Unsatisfiable Request (only-if-cached)"
        //   195: invokevirtual   okhttp3/Response$Builder.message:(Ljava/lang/String;)Lokhttp3/Response$Builder;
        //   198: getstatic       okhttp3/internal/Util.EMPTY_RESPONSE:Lokhttp3/ResponseBody;
        //   201: invokevirtual   okhttp3/Response$Builder.body:(Lokhttp3/ResponseBody;)Lokhttp3/Response$Builder;
        //   204: ldc2_w          -1
        //   207: invokevirtual   okhttp3/Response$Builder.sentRequestAtMillis:(J)Lokhttp3/Response$Builder;
        //   210: invokestatic    java/lang/System.currentTimeMillis:()J
        //   213: invokevirtual   okhttp3/Response$Builder.receivedResponseAtMillis:(J)Lokhttp3/Response$Builder;
        //   216: invokevirtual   okhttp3/Response$Builder.build:()Lokhttp3/Response;
        //   219: areturn        
        //   220: aload           5
        //   222: invokevirtual   okhttp3/Response.newBuilder:()Lokhttp3/Response$Builder;
        //   225: aload           5
        //   227: invokestatic    okhttp3/internal/cache/CacheInterceptor.stripBody:(Lokhttp3/Response;)Lokhttp3/Response;
        //   230: invokevirtual   okhttp3/Response$Builder.cacheResponse:(Lokhttp3/Response;)Lokhttp3/Response$Builder;
        //   233: invokevirtual   okhttp3/Response$Builder.build:()Lokhttp3/Response;
        //   236: areturn        
        //   237: aload_2        
        //   238: ifnull          76
        //   241: aload_2        
        //   242: invokevirtual   okhttp3/Response.body:()Lokhttp3/ResponseBody;
        //   245: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //   248: goto            76
        //   251: astore_1       
        //   252: iconst_0       
        //   253: ifeq            258
        //   256: aload_1        
        //   257: athrow         
        //   258: aload_2        
        //   259: ifnull          256
        //   262: aload_2        
        //   263: invokevirtual   okhttp3/Response.body:()Lokhttp3/ResponseBody;
        //   266: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //   269: goto            256
        //   272: aload_3        
        //   273: invokevirtual   okhttp3/Response.code:()I
        //   276: sipush          304
        //   279: if_icmpeq       293
        //   282: aload           5
        //   284: invokevirtual   okhttp3/Response.body:()Lokhttp3/ResponseBody;
        //   287: invokestatic    okhttp3/internal/Util.closeQuietly:(Ljava/io/Closeable;)V
        //   290: goto            81
        //   293: aload           5
        //   295: invokevirtual   okhttp3/Response.newBuilder:()Lokhttp3/Response$Builder;
        //   298: aload           5
        //   300: invokevirtual   okhttp3/Response.headers:()Lokhttp3/Headers;
        //   303: aload_3        
        //   304: invokevirtual   okhttp3/Response.headers:()Lokhttp3/Headers;
        //   307: invokestatic    okhttp3/internal/cache/CacheInterceptor.combine:(Lokhttp3/Headers;Lokhttp3/Headers;)Lokhttp3/Headers;
        //   310: invokevirtual   okhttp3/Response$Builder.headers:(Lokhttp3/Headers;)Lokhttp3/Response$Builder;
        //   313: aload_3        
        //   314: invokevirtual   okhttp3/Response.sentRequestAtMillis:()J
        //   317: invokevirtual   okhttp3/Response$Builder.sentRequestAtMillis:(J)Lokhttp3/Response$Builder;
        //   320: aload_3        
        //   321: invokevirtual   okhttp3/Response.receivedResponseAtMillis:()J
        //   324: invokevirtual   okhttp3/Response$Builder.receivedResponseAtMillis:(J)Lokhttp3/Response$Builder;
        //   327: aload           5
        //   329: invokestatic    okhttp3/internal/cache/CacheInterceptor.stripBody:(Lokhttp3/Response;)Lokhttp3/Response;
        //   332: invokevirtual   okhttp3/Response$Builder.cacheResponse:(Lokhttp3/Response;)Lokhttp3/Response$Builder;
        //   335: aload_3        
        //   336: invokestatic    okhttp3/internal/cache/CacheInterceptor.stripBody:(Lokhttp3/Response;)Lokhttp3/Response;
        //   339: invokevirtual   okhttp3/Response$Builder.networkResponse:(Lokhttp3/Response;)Lokhttp3/Response$Builder;
        //   342: invokevirtual   okhttp3/Response$Builder.build:()Lokhttp3/Response;
        //   345: astore_1       
        //   346: aload_3        
        //   347: invokevirtual   okhttp3/Response.body:()Lokhttp3/ResponseBody;
        //   350: invokevirtual   okhttp3/ResponseBody.close:()V
        //   353: aload_0        
        //   354: getfield        okhttp3/internal/cache/CacheInterceptor.cache:Lokhttp3/internal/cache/InternalCache;
        //   357: invokeinterface okhttp3/internal/cache/InternalCache.trackConditionalCacheHit:()V
        //   362: aload_0        
        //   363: getfield        okhttp3/internal/cache/CacheInterceptor.cache:Lokhttp3/internal/cache/InternalCache;
        //   366: aload           5
        //   368: aload_1        
        //   369: invokeinterface okhttp3/internal/cache/InternalCache.update:(Lokhttp3/Response;Lokhttp3/Response;)V
        //   374: aload_1        
        //   375: areturn        
        //   376: aload_0        
        //   377: aload_0        
        //   378: aload_1        
        //   379: aload_3        
        //   380: invokevirtual   okhttp3/Response.request:()Lokhttp3/Request;
        //   383: aload_0        
        //   384: getfield        okhttp3/internal/cache/CacheInterceptor.cache:Lokhttp3/internal/cache/InternalCache;
        //   387: invokespecial   okhttp3/internal/cache/CacheInterceptor.maybeCache:(Lokhttp3/Response;Lokhttp3/Request;Lokhttp3/internal/cache/InternalCache;)Lokhttp3/internal/cache/CacheRequest;
        //   390: aload_1        
        //   391: invokespecial   okhttp3/internal/cache/CacheInterceptor.cacheWritingResponse:(Lokhttp3/internal/cache/CacheRequest;Lokhttp3/Response;)Lokhttp3/Response;
        //   394: astore_1       
        //   395: goto            111
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  63     72     251    272    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(var_5_28:Response, aconstnull:Response())
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
}
