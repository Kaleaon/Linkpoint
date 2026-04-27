// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.io.IOException;

public interface Callback
{
    void onFailure(final Call p0, final IOException p1);
    
    void onResponse(final Call p0, final Response p1) throws IOException;
}
