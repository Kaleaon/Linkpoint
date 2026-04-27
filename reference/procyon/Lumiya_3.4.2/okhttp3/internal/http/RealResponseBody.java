// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http;

import okhttp3.MediaType;
import okio.BufferedSource;
import okhttp3.Headers;
import okhttp3.ResponseBody;

public final class RealResponseBody extends ResponseBody
{
    private final Headers headers;
    private final BufferedSource source;
    
    public RealResponseBody(final Headers headers, final BufferedSource source) {
        this.headers = headers;
        this.source = source;
    }
    
    @Override
    public long contentLength() {
        return HttpHeaders.contentLength(this.headers);
    }
    
    @Override
    public MediaType contentType() {
        MediaType parse = null;
        final String value = this.headers.get("Content-Type");
        if (value != null) {
            parse = MediaType.parse(value);
        }
        return parse;
    }
    
    @Override
    public BufferedSource source() {
        return this.source;
    }
}
