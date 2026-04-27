// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.io.IOException;

public interface Interceptor
{
    Response intercept(final Chain p0) throws IOException;
    
    public interface Chain
    {
        Connection connection();
        
        Response proceed(final Request p0) throws IOException;
        
        Request request();
    }
}
