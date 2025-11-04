// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.cache;

import java.io.IOException;
import okhttp3.Response;
import okhttp3.Request;

public interface InternalCache
{
    Response get(final Request p0) throws IOException;
    
    CacheRequest put(final Response p0) throws IOException;
    
    void remove(final Request p0) throws IOException;
    
    void trackConditionalCacheHit();
    
    void trackResponse(final CacheStrategy p0);
    
    void update(final Response p0, final Response p1);
}
