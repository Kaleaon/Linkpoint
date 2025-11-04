// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3.internal.http2;

import java.util.List;
import java.io.IOException;
import okio.BufferedSource;

public interface PushObserver
{
    public static final PushObserver CANCEL = new PushObserver() {
        @Override
        public boolean onData(final int n, final BufferedSource bufferedSource, final int n2, final boolean b) throws IOException {
            bufferedSource.skip(n2);
            return true;
        }
        
        @Override
        public boolean onHeaders(final int n, final List<Header> list, final boolean b) {
            return true;
        }
        
        @Override
        public boolean onRequest(final int n, final List<Header> list) {
            return true;
        }
        
        @Override
        public void onReset(final int n, final ErrorCode errorCode) {
        }
    };
    
    boolean onData(final int p0, final BufferedSource p1, final int p2, final boolean p3) throws IOException;
    
    boolean onHeaders(final int p0, final List<Header> p1, final boolean p2);
    
    boolean onRequest(final int p0, final List<Header> p1);
    
    void onReset(final int p0, final ErrorCode p1);
}
