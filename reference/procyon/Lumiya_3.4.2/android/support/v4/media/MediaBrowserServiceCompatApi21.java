// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media;

import java.util.Iterator;
import java.util.ArrayList;
import android.os.Parcel;
import android.media.browse.MediaBrowser$MediaItem;
import java.util.List;
import android.service.media.MediaBrowserService$Result;
import android.service.media.MediaBrowserService$BrowserRoot;
import android.os.Bundle;
import android.media.session.MediaSession$Token;
import android.os.IBinder;
import android.content.Intent;
import android.service.media.MediaBrowserService;
import android.content.Context;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class MediaBrowserServiceCompatApi21
{
    public static Object createService(final Context context, final ServiceCompatProxy serviceCompatProxy) {
        return new MediaBrowserServiceAdaptor(context, serviceCompatProxy);
    }
    
    public static void notifyChildrenChanged(final Object o, final String s) {
        ((MediaBrowserService)o).notifyChildrenChanged(s);
    }
    
    public static IBinder onBind(final Object o, final Intent intent) {
        return ((MediaBrowserService)o).onBind(intent);
    }
    
    public static void onCreate(final Object o) {
        ((MediaBrowserService)o).onCreate();
    }
    
    public static void setSessionToken(final Object o, final Object o2) {
        ((MediaBrowserService)o).setSessionToken((MediaSession$Token)o2);
    }
    
    static class BrowserRoot
    {
        final Bundle mExtras;
        final String mRootId;
        
        BrowserRoot(final String mRootId, final Bundle mExtras) {
            this.mRootId = mRootId;
            this.mExtras = mExtras;
        }
    }
    
    static class MediaBrowserServiceAdaptor extends MediaBrowserService
    {
        final ServiceCompatProxy mServiceProxy;
        
        MediaBrowserServiceAdaptor(final Context context, final ServiceCompatProxy mServiceProxy) {
            this.attachBaseContext(context);
            this.mServiceProxy = mServiceProxy;
        }
        
        public MediaBrowserService$BrowserRoot onGetRoot(final String s, final int n, Bundle bundle) {
            final MediaBrowserService$BrowserRoot mediaBrowserService$BrowserRoot = null;
            final ServiceCompatProxy mServiceProxy = this.mServiceProxy;
            if (bundle != null) {
                bundle = new Bundle(bundle);
            }
            else {
                bundle = null;
            }
            final BrowserRoot onGetRoot = mServiceProxy.onGetRoot(s, n, bundle);
            MediaBrowserService$BrowserRoot mediaBrowserService$BrowserRoot2 = mediaBrowserService$BrowserRoot;
            if (onGetRoot != null) {
                mediaBrowserService$BrowserRoot2 = new MediaBrowserService$BrowserRoot(onGetRoot.mRootId, onGetRoot.mExtras);
            }
            return mediaBrowserService$BrowserRoot2;
        }
        
        public void onLoadChildren(final String s, final MediaBrowserService$Result<List<MediaBrowser$MediaItem>> mediaBrowserService$Result) {
            this.mServiceProxy.onLoadChildren(s, (ResultWrapper<List<Parcel>>)new ResultWrapper(mediaBrowserService$Result));
        }
    }
    
    static class ResultWrapper<T>
    {
        MediaBrowserService$Result mResultObj;
        
        ResultWrapper(final MediaBrowserService$Result mResultObj) {
            this.mResultObj = mResultObj;
        }
        
        public void detach() {
            this.mResultObj.detach();
        }
        
        List<MediaBrowser$MediaItem> parcelListToItemList(final List<Parcel> list) {
            if (list != null) {
                final ArrayList list2 = new ArrayList();
                for (final Parcel parcel : list) {
                    parcel.setDataPosition(0);
                    list2.add(MediaBrowser$MediaItem.CREATOR.createFromParcel(parcel));
                    parcel.recycle();
                }
                return list2;
            }
            return null;
        }
        
        public void sendResult(final T t) {
            if (!(t instanceof List)) {
                if (!(t instanceof Parcel)) {
                    this.mResultObj.sendResult((Object)null);
                }
                else {
                    final Parcel parcel = (Parcel)t;
                    parcel.setDataPosition(0);
                    this.mResultObj.sendResult(MediaBrowser$MediaItem.CREATOR.createFromParcel(parcel));
                    parcel.recycle();
                }
            }
            else {
                this.mResultObj.sendResult((Object)this.parcelListToItemList((List<Parcel>)t));
            }
        }
    }
    
    public interface ServiceCompatProxy
    {
        BrowserRoot onGetRoot(final String p0, final int p1, final Bundle p2);
        
        void onLoadChildren(final String p0, final ResultWrapper<List<Parcel>> p1);
    }
}
