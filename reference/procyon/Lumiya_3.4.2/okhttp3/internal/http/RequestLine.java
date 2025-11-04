// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import okhttp3.HttpUrl;
import java.net.Proxy;
import okhttp3.Request;

public final class RequestLine
{
    private RequestLine() {
    }
    
    public static String get(final Request request, final Proxy.Type type) {
        final StringBuilder sb = new StringBuilder();
        sb.append(request.method());
        sb.append(' ');
        if (!includeAuthorityInRequestLine(request, type)) {
            sb.append(requestPath(request.url()));
        }
        else {
            sb.append(request.url());
        }
        sb.append(" HTTP/1.1");
        return sb.toString();
    }
    
    private static boolean includeAuthorityInRequestLine(final Request request, final Proxy.Type type) {
        boolean b = false;
        if (!request.isHttps() && type == Proxy.Type.HTTP) {
            b = true;
        }
        return b;
    }
    
    public static String requestPath(final HttpUrl httpUrl) {
        final String encodedPath = httpUrl.encodedPath();
        final String encodedQuery = httpUrl.encodedQuery();
        String string;
        if (encodedQuery == null) {
            string = encodedPath;
        }
        else {
            string = encodedPath + '?' + encodedQuery;
        }
        return string;
    }
}
