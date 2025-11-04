// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import okhttp3.ResponseBody;
import okhttp3.Response;
import java.io.IOException;
import okio.Sink;
import okhttp3.Request;

public interface HttpCodec
{
    public static final int DISCARD_STREAM_TIMEOUT_MILLIS = 100;
    
    void cancel();
    
    Sink createRequestBody(final Request p0, final long p1);
    
    void finishRequest() throws IOException;
    
    ResponseBody openResponseBody(final Response p0) throws IOException;
    
    Response.Builder readResponseHeaders() throws IOException;
    
    void writeRequestHeaders(final Request p0) throws IOException;
}
