// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media.session;

import android.net.Uri;
import android.os.Bundle;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import android.media.session.MediaSession;
import android.support.annotation.RequiresApi;

@RequiresApi(24)
class MediaSessionCompatApi24
{
    private static final String TAG = "MediaSessionCompatApi24";
    
    public static Object createCallback(final Callback callback) {
        return new CallbackProxy(callback);
    }
    
    public static String getCallingPackage(final Object o) {
        final MediaSession obj = (MediaSession)o;
        try {
            return (String)obj.getClass().getMethod("getCallingPackage", (Class<?>[])new Class[0]).invoke(obj, new Object[0]);
        }
        catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            Log.e("MediaSessionCompatApi24", "Cannot execute MediaSession.getCallingPackage()", (Throwable)ex);
            return null;
        }
    }
    
    public interface Callback extends MediaSessionCompatApi23.Callback
    {
        void onPrepare();
        
        void onPrepareFromMediaId(final String p0, final Bundle p1);
        
        void onPrepareFromSearch(final String p0, final Bundle p1);
        
        void onPrepareFromUri(final Uri p0, final Bundle p1);
    }
    
    static class CallbackProxy<T extends MediaSessionCompatApi24.Callback> extends MediaSessionCompatApi23.CallbackProxy<T>
    {
        public CallbackProxy(final T t) {
            super(t);
        }
        
        public void onPrepare() {
            this.mCallback.onPrepare();
        }
        
        public void onPrepareFromMediaId(final String s, final Bundle bundle) {
            this.mCallback.onPrepareFromMediaId(s, bundle);
        }
        
        public void onPrepareFromSearch(final String s, final Bundle bundle) {
            this.mCallback.onPrepareFromSearch(s, bundle);
        }
        
        public void onPrepareFromUri(final Uri uri, final Bundle bundle) {
            this.mCallback.onPrepareFromUri(uri, bundle);
        }
    }
}
