// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.connection;

import java.io.IOException;
import okhttp3.Request;
import okhttp3.Connection;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;

public final class ConnectInterceptor implements Interceptor
{
    public final OkHttpClient client;
    
    public ConnectInterceptor(final OkHttpClient client) {
        this.client = client;
    }
    
    @Override
    public Response intercept(final Chain chain) throws IOException {
        boolean b = false;
        final RealInterceptorChain realInterceptorChain = (RealInterceptorChain)chain;
        final Request request = realInterceptorChain.request();
        final StreamAllocation streamAllocation = realInterceptorChain.streamAllocation();
        if (!request.method().equals("GET")) {
            b = true;
        }
        return realInterceptorChain.proceed(request, streamAllocation, streamAllocation.newStream(this.client, b), streamAllocation.connection());
    }
}
