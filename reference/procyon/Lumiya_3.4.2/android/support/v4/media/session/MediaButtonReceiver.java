// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media.session;

import android.os.RemoteException;
import android.content.BroadcastReceiver$PendingResult;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.os.Build$VERSION;
import android.content.pm.PackageManager;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.content.Intent;
import android.content.ComponentName;
import android.util.Log;
import android.app.PendingIntent;
import android.content.Context;
import android.content.BroadcastReceiver;

public class MediaButtonReceiver extends BroadcastReceiver
{
    private static final String TAG = "MediaButtonReceiver";
    
    public static PendingIntent buildMediaButtonPendingIntent(final Context context, final long n) {
        final ComponentName mediaButtonReceiverComponent = getMediaButtonReceiverComponent(context);
        if (mediaButtonReceiverComponent != null) {
            return buildMediaButtonPendingIntent(context, mediaButtonReceiverComponent, n);
        }
        Log.w("MediaButtonReceiver", "A unique media button receiver could not be found in the given context, so couldn't build a pending intent.");
        return null;
    }
    
    public static PendingIntent buildMediaButtonPendingIntent(final Context context, final ComponentName component, final long lng) {
        if (component == null) {
            Log.w("MediaButtonReceiver", "The component name of media button receiver should be provided.");
            return null;
        }
        final int keyCode = PlaybackStateCompat.toKeyCode(lng);
        if (keyCode != 0) {
            final Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            intent.setComponent(component);
            intent.putExtra("android.intent.extra.KEY_EVENT", (Parcelable)new KeyEvent(0, keyCode));
            return PendingIntent.getBroadcast(context, keyCode, intent, 0);
        }
        Log.w("MediaButtonReceiver", "Cannot build a media button pending intent with the given action: " + lng);
        return null;
    }
    
    static ComponentName getMediaButtonReceiverComponent(final Context context) {
        final Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
        intent.setPackage(context.getPackageName());
        final List queryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (queryBroadcastReceivers.size() != 1) {
            if (queryBroadcastReceivers.size() > 1) {
                Log.w("MediaButtonReceiver", "More than one BroadcastReceiver that handles android.intent.action.MEDIA_BUTTON was found, returning null.");
            }
            return null;
        }
        final ResolveInfo resolveInfo = queryBroadcastReceivers.get(0);
        return new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
    }
    
    private static ComponentName getServiceComponentByAction(final Context context, final String str) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(str);
        intent.setPackage(context.getPackageName());
        final List queryIntentServices = packageManager.queryIntentServices(intent, 0);
        if (queryIntentServices.size() == 1) {
            final ResolveInfo resolveInfo = queryIntentServices.get(0);
            return new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
        }
        if (!queryIntentServices.isEmpty()) {
            throw new IllegalStateException("Expected 1 service that handles " + str + ", found " + queryIntentServices.size());
        }
        return null;
    }
    
    public static KeyEvent handleIntent(final MediaSessionCompat mediaSessionCompat, final Intent intent) {
        if (mediaSessionCompat != null && intent != null && "android.intent.action.MEDIA_BUTTON".equals(intent.getAction()) && intent.hasExtra("android.intent.extra.KEY_EVENT")) {
            final KeyEvent keyEvent = (KeyEvent)intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            mediaSessionCompat.getController().dispatchMediaButtonEvent(keyEvent);
            return keyEvent;
        }
        return null;
    }
    
    private static void startForegroundService(final Context context, final Intent intent) {
        if (Build$VERSION.SDK_INT < 26) {
            context.startService(intent);
        }
        else {
            context.startForegroundService(intent);
        }
    }
    
    public void onReceive(Context applicationContext, final Intent obj) {
        if (obj == null || !"android.intent.action.MEDIA_BUTTON".equals(obj.getAction()) || !obj.hasExtra("android.intent.extra.KEY_EVENT")) {
            Log.d("MediaButtonReceiver", "Ignore unsupported intent: " + obj);
            return;
        }
        final ComponentName serviceComponentByAction = getServiceComponentByAction(applicationContext, "android.intent.action.MEDIA_BUTTON");
        if (serviceComponentByAction != null) {
            obj.setComponent(serviceComponentByAction);
            startForegroundService(applicationContext, obj);
            return;
        }
        final ComponentName serviceComponentByAction2 = getServiceComponentByAction(applicationContext, "android.media.browse.MediaBrowserService");
        if (serviceComponentByAction2 == null) {
            throw new IllegalStateException("Could not find any Service that handles android.intent.action.MEDIA_BUTTON or implements a media browser service.");
        }
        final BroadcastReceiver$PendingResult goAsync = this.goAsync();
        applicationContext = applicationContext.getApplicationContext();
        final MediaButtonConnectionCallback mediaButtonConnectionCallback = new MediaButtonConnectionCallback(applicationContext, obj, goAsync);
        final MediaBrowserCompat mediaBrowser = new MediaBrowserCompat(applicationContext, serviceComponentByAction2, (MediaBrowserCompat.ConnectionCallback)mediaButtonConnectionCallback, null);
        mediaButtonConnectionCallback.setMediaBrowser(mediaBrowser);
        mediaBrowser.connect();
    }
    
    private static class MediaButtonConnectionCallback extends ConnectionCallback
    {
        private final Context mContext;
        private final Intent mIntent;
        private MediaBrowserCompat mMediaBrowser;
        private final BroadcastReceiver$PendingResult mPendingResult;
        
        MediaButtonConnectionCallback(final Context mContext, final Intent mIntent, final BroadcastReceiver$PendingResult mPendingResult) {
            this.mContext = mContext;
            this.mIntent = mIntent;
            this.mPendingResult = mPendingResult;
        }
        
        private void finish() {
            this.mMediaBrowser.disconnect();
            this.mPendingResult.finish();
        }
        
        @Override
        public void onConnected() {
            while (true) {
                try {
                    new MediaControllerCompat(this.mContext, this.mMediaBrowser.getSessionToken()).dispatchMediaButtonEvent((KeyEvent)this.mIntent.getParcelableExtra("android.intent.extra.KEY_EVENT"));
                    this.finish();
                }
                catch (final RemoteException ex) {
                    Log.e("MediaButtonReceiver", "Failed to create a media controller", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
        
        @Override
        public void onConnectionFailed() {
            this.finish();
        }
        
        @Override
        public void onConnectionSuspended() {
            this.finish();
        }
        
        void setMediaBrowser(final MediaBrowserCompat mMediaBrowser) {
            this.mMediaBrowser = mMediaBrowser;
        }
    }
}
