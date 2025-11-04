// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.Collections;
import java.util.List;

public interface CookieJar
{
    public static final CookieJar NO_COOKIES = new CookieJar() {
        @Override
        public List<Cookie> loadForRequest(final HttpUrl httpUrl) {
            return Collections.emptyList();
        }
        
        @Override
        public void saveFromResponse(final HttpUrl httpUrl, final List<Cookie> list) {
        }
    };
    
    List<Cookie> loadForRequest(final HttpUrl p0);
    
    void saveFromResponse(final HttpUrl p0, final List<Cookie> p1);
}
