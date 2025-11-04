// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import okhttp3.internal.NamedRunnable;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.connection.StreamAllocation;
import java.util.List;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.CallServerInterceptor;
import okhttp3.internal.connection.ConnectInterceptor;
import okhttp3.internal.cache.CacheInterceptor;
import okhttp3.internal.http.BridgeInterceptor;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.http.RetryAndFollowUpInterceptor;

final class RealCall implements Call
{
    final OkHttpClient client;
    private boolean executed;
    final boolean forWebSocket;
    final Request originalRequest;
    final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;
    
    RealCall(final OkHttpClient client, final Request originalRequest, final boolean forWebSocket) {
        this.client = client;
        this.originalRequest = originalRequest;
        this.forWebSocket = forWebSocket;
        this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);
    }
    
    private void captureCallStackTrace() {
        this.retryAndFollowUpInterceptor.setCallStackTrace(Platform.get().getStackTraceForCloseable("response.body().close()"));
    }
    
    @Override
    public void cancel() {
        this.retryAndFollowUpInterceptor.cancel();
    }
    
    @Override
    public RealCall clone() {
        return new RealCall(this.client, this.originalRequest, this.forWebSocket);
    }
    
    @Override
    public void enqueue(final Callback callback) {
        synchronized (this) {
            if (!this.executed) {
                this.executed = true;
                monitorexit(this);
                this.captureCallStackTrace();
                this.client.dispatcher().enqueue(new AsyncCall(callback));
                return;
            }
            throw new IllegalStateException("Already Executed");
        }
    }
    
    @Override
    public Response execute() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_0        
        //     3: getfield        okhttp3/RealCall.executed:Z
        //     6: ifne            53
        //     9: aload_0        
        //    10: iconst_1       
        //    11: putfield        okhttp3/RealCall.executed:Z
        //    14: aload_0        
        //    15: monitorexit    
        //    16: aload_0        
        //    17: invokespecial   okhttp3/RealCall.captureCallStackTrace:()V
        //    20: aload_0        
        //    21: getfield        okhttp3/RealCall.client:Lokhttp3/OkHttpClient;
        //    24: invokevirtual   okhttp3/OkHttpClient.dispatcher:()Lokhttp3/Dispatcher;
        //    27: aload_0        
        //    28: invokevirtual   okhttp3/Dispatcher.executed:(Lokhttp3/RealCall;)V
        //    31: aload_0        
        //    32: invokevirtual   okhttp3/RealCall.getResponseWithInterceptorChain:()Lokhttp3/Response;
        //    35: astore_1       
        //    36: aload_1        
        //    37: ifnull          70
        //    40: aload_0        
        //    41: getfield        okhttp3/RealCall.client:Lokhttp3/OkHttpClient;
        //    44: invokevirtual   okhttp3/OkHttpClient.dispatcher:()Lokhttp3/Dispatcher;
        //    47: aload_0        
        //    48: invokevirtual   okhttp3/Dispatcher.finished:(Lokhttp3/RealCall;)V
        //    51: aload_1        
        //    52: areturn        
        //    53: new             Ljava/lang/IllegalStateException;
        //    56: astore_1       
        //    57: aload_1        
        //    58: ldc             "Already Executed"
        //    60: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //    63: aload_1        
        //    64: athrow         
        //    65: astore_1       
        //    66: aload_0        
        //    67: monitorexit    
        //    68: aload_1        
        //    69: athrow         
        //    70: new             Ljava/io/IOException;
        //    73: astore_1       
        //    74: aload_1        
        //    75: ldc             "Canceled"
        //    77: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //    80: aload_1        
        //    81: athrow         
        //    82: astore_1       
        //    83: aload_0        
        //    84: getfield        okhttp3/RealCall.client:Lokhttp3/OkHttpClient;
        //    87: invokevirtual   okhttp3/OkHttpClient.dispatcher:()Lokhttp3/Dispatcher;
        //    90: aload_0        
        //    91: invokevirtual   okhttp3/Dispatcher.finished:(Lokhttp3/RealCall;)V
        //    94: aload_1        
        //    95: athrow         
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  2      16     65     70     Any
        //  20     36     82     96     Any
        //  53     65     65     70     Any
        //  66     68     65     70     Any
        //  70     82     82     96     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0053:
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
    
    Response getResponseWithInterceptorChain() throws IOException {
        final ArrayList list = new ArrayList();
        list.addAll(this.client.interceptors());
        list.add(this.retryAndFollowUpInterceptor);
        list.add(new BridgeInterceptor(this.client.cookieJar()));
        list.add(new CacheInterceptor(this.client.internalCache()));
        list.add(new ConnectInterceptor(this.client));
        if (!this.forWebSocket) {
            list.addAll(this.client.networkInterceptors());
        }
        list.add(new CallServerInterceptor(this.forWebSocket));
        return ((Interceptor.Chain)new RealInterceptorChain(list, null, null, null, 0, this.originalRequest)).proceed(this.originalRequest);
    }
    
    @Override
    public boolean isCanceled() {
        return this.retryAndFollowUpInterceptor.isCanceled();
    }
    
    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return this.executed;
        }
    }
    
    String redactedUrl() {
        return this.originalRequest.url().redact();
    }
    
    @Override
    public Request request() {
        return this.originalRequest;
    }
    
    StreamAllocation streamAllocation() {
        return this.retryAndFollowUpInterceptor.streamAllocation();
    }
    
    String toLoggableString() {
        final StringBuilder sb = new StringBuilder();
        String str;
        if (!this.isCanceled()) {
            str = "";
        }
        else {
            str = "canceled ";
        }
        final StringBuilder append = sb.append(str);
        String str2;
        if (!this.forWebSocket) {
            str2 = "call";
        }
        else {
            str2 = "web socket";
        }
        return append.append(str2).append(" to ").append(this.redactedUrl()).toString();
    }
    
    final class AsyncCall extends NamedRunnable
    {
        private final Callback responseCallback;
        
        AsyncCall(final Callback responseCallback) {
            super("OkHttp %s", new Object[] { RealCall.this.redactedUrl() });
            this.responseCallback = responseCallback;
        }
        
        @Override
        protected void execute() {
            int n = 1;
            while (true) {
                try {
                    try {
                        Object o = RealCall.this.getResponseWithInterceptorChain();
                        Label_0054: {
                            if (RealCall.this.retryAndFollowUpInterceptor.isCanceled()) {
                                break Label_0054;
                            }
                            while (true) {
                                try {
                                    this.responseCallback.onResponse(RealCall.this, (Response)o);
                                    return;
                                    final Callback responseCallback = this.responseCallback;
                                    o = RealCall.this;
                                    responseCallback.onFailure((Call)o, new IOException("Canceled"));
                                    return;
                                }
                                catch (final IOException ex) {}
                                if (n == 0) {
                                    this.responseCallback.onFailure(RealCall.this, (IOException)o);
                                }
                                else {
                                    Platform.get().log(4, "Callback failure for " + RealCall.this.toLoggableString(), (Throwable)o);
                                }
                                RealCall.this.client.dispatcher().finished(this);
                            }
                        }
                    }
                    finally {
                        RealCall.this.client.dispatcher().finished(this);
                    }
                }
                catch (final IOException o) {
                    n = 0;
                    continue;
                }
                break;
            }
        }
        
        RealCall get() {
            return RealCall.this;
        }
        
        String host() {
            return RealCall.this.originalRequest.url().host();
        }
        
        Request request() {
            return RealCall.this.originalRequest;
        }
    }
}
