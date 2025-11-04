// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.app;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcelable;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.BundleCompat;
import android.os.Build$VERSION;
import android.support.v4.media.session.MediaSessionCompat;
import android.app.Notification;

@Deprecated
public class NotificationCompat extends android.support.v4.app.NotificationCompat
{
    @Deprecated
    public NotificationCompat() {
    }
    
    @Deprecated
    public static MediaSessionCompat.Token getMediaSession(final Notification notification) {
        final Bundle extras = android.support.v4.app.NotificationCompat.getExtras(notification);
        if (extras != null) {
            if (Build$VERSION.SDK_INT < 21) {
                final IBinder binder = BundleCompat.getBinder(extras, "android.mediaSession");
                if (binder != null) {
                    final Parcel obtain = Parcel.obtain();
                    obtain.writeStrongBinder(binder);
                    obtain.setDataPosition(0);
                    final MediaSessionCompat.Token token = (MediaSessionCompat.Token)MediaSessionCompat.Token.CREATOR.createFromParcel(obtain);
                    obtain.recycle();
                    return token;
                }
            }
            else {
                final Parcelable parcelable = extras.getParcelable("android.mediaSession");
                if (parcelable != null) {
                    return MediaSessionCompat.Token.fromToken(parcelable);
                }
            }
        }
        return null;
    }
    
    @Deprecated
    public static class Builder extends android.support.v4.app.NotificationCompat.Builder
    {
        @Deprecated
        public Builder(final Context context) {
            super(context);
        }
    }
    
    @Deprecated
    public static class DecoratedCustomViewStyle extends android.support.v4.app.NotificationCompat.DecoratedCustomViewStyle
    {
        @Deprecated
        public DecoratedCustomViewStyle() {
        }
    }
    
    @Deprecated
    public static class DecoratedMediaCustomViewStyle extends android.support.v4.media.app.NotificationCompat.DecoratedMediaCustomViewStyle
    {
        @Deprecated
        public DecoratedMediaCustomViewStyle() {
        }
    }
    
    @Deprecated
    public static class MediaStyle extends android.support.v4.media.app.NotificationCompat.MediaStyle
    {
        @Deprecated
        public MediaStyle() {
        }
        
        @Deprecated
        public MediaStyle(final Builder builder) {
            super(builder);
        }
        
        @Deprecated
        public MediaStyle setCancelButtonIntent(final PendingIntent cancelButtonIntent) {
            return (MediaStyle)super.setCancelButtonIntent(cancelButtonIntent);
        }
        
        @Deprecated
        public MediaStyle setMediaSession(final MediaSessionCompat.Token mediaSession) {
            return (MediaStyle)super.setMediaSession(mediaSession);
        }
        
        @Deprecated
        public MediaStyle setShowActionsInCompactView(final int... showActionsInCompactView) {
            return (MediaStyle)super.setShowActionsInCompactView(showActionsInCompactView);
        }
        
        @Deprecated
        public MediaStyle setShowCancelButton(final boolean showCancelButton) {
            return (MediaStyle)super.setShowCancelButton(showCancelButton);
        }
    }
}
