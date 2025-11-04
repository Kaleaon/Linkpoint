// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media.app;

import android.support.annotation.RequiresApi;
import android.media.session.MediaSession$Token;
import android.os.Parcelable;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.BundleCompat;
import android.app.Notification;
import android.support.v4.media.session.MediaSessionCompat;
import android.app.PendingIntent;
import android.support.annotation.RestrictTo;
import android.app.Notification$Style;
import android.app.Notification$MediaStyle;
import android.app.Notification$DecoratedMediaCustomViewStyle;
import android.os.Build$VERSION;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.mediacompat.R;
import android.widget.RemoteViews;

public class NotificationCompat
{
    private NotificationCompat() {
    }
    
    public static class DecoratedMediaCustomViewStyle extends MediaStyle
    {
        private void setBackgroundColor(final RemoteViews remoteViews) {
            int n;
            if (this.mBuilder.getColor() == 0) {
                n = this.mBuilder.mContext.getResources().getColor(R.color.notification_material_background_media_default_color);
            }
            else {
                n = this.mBuilder.getColor();
            }
            remoteViews.setInt(R.id.status_bar_latest_event_content, "setBackgroundColor", n);
        }
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @Override
        public void apply(final NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            if (Build$VERSION.SDK_INT < 24) {
                super.apply(notificationBuilderWithBuilderAccessor);
            }
            else {
                notificationBuilderWithBuilderAccessor.getBuilder().setStyle((Notification$Style)((MediaStyle)this).fillInMediaStyle((Notification$MediaStyle)new Notification$DecoratedMediaCustomViewStyle()));
            }
        }
        
        @Override
        int getBigContentViewLayoutResource(int n) {
            if (n > 3) {
                n = R.layout.notification_template_big_media_custom;
            }
            else {
                n = R.layout.notification_template_big_media_narrow_custom;
            }
            return n;
        }
        
        @Override
        int getContentViewLayoutResource() {
            int n;
            if (this.mBuilder.getContentView() == null) {
                n = super.getContentViewLayoutResource();
            }
            else {
                n = R.layout.notification_template_media_custom;
            }
            return n;
        }
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @Override
        public RemoteViews makeBigContentView(final NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            if (Build$VERSION.SDK_INT >= 24) {
                return null;
            }
            RemoteViews remoteViews;
            if (this.mBuilder.getBigContentView() == null) {
                remoteViews = this.mBuilder.getContentView();
            }
            else {
                remoteViews = this.mBuilder.getBigContentView();
            }
            if (remoteViews != null) {
                final RemoteViews generateBigContentView = ((MediaStyle)this).generateBigContentView();
                ((Style)this).buildIntoRemoteViews(generateBigContentView, remoteViews);
                if (Build$VERSION.SDK_INT >= 21) {
                    this.setBackgroundColor(generateBigContentView);
                }
                return generateBigContentView;
            }
            return null;
        }
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @Override
        public RemoteViews makeContentView(final NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            boolean b = false;
            if (Build$VERSION.SDK_INT < 24) {
                boolean b2;
                if (this.mBuilder.getContentView() == null) {
                    b2 = false;
                }
                else {
                    b2 = true;
                }
                if (Build$VERSION.SDK_INT < 21) {
                    final RemoteViews generateContentView = ((MediaStyle)this).generateContentView();
                    if (b2) {
                        ((Style)this).buildIntoRemoteViews(generateContentView, this.mBuilder.getContentView());
                        return generateContentView;
                    }
                }
                else {
                    if (b2 || this.mBuilder.getBigContentView() != null) {
                        b = true;
                    }
                    if (b) {
                        final RemoteViews generateContentView2 = ((MediaStyle)this).generateContentView();
                        if (b2) {
                            ((Style)this).buildIntoRemoteViews(generateContentView2, this.mBuilder.getContentView());
                        }
                        this.setBackgroundColor(generateContentView2);
                        return generateContentView2;
                    }
                }
                return null;
            }
            return null;
        }
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @Override
        public RemoteViews makeHeadsUpContentView(final NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            if (Build$VERSION.SDK_INT >= 24) {
                return null;
            }
            RemoteViews remoteViews;
            if (this.mBuilder.getHeadsUpContentView() == null) {
                remoteViews = this.mBuilder.getContentView();
            }
            else {
                remoteViews = this.mBuilder.getHeadsUpContentView();
            }
            if (remoteViews != null) {
                final RemoteViews generateBigContentView = ((MediaStyle)this).generateBigContentView();
                ((Style)this).buildIntoRemoteViews(generateBigContentView, remoteViews);
                if (Build$VERSION.SDK_INT >= 21) {
                    this.setBackgroundColor(generateBigContentView);
                }
                return generateBigContentView;
            }
            return null;
        }
    }
    
    public static class MediaStyle extends Style
    {
        private static final int MAX_MEDIA_BUTTONS = 5;
        private static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
        int[] mActionsToShowInCompact;
        PendingIntent mCancelButtonIntent;
        boolean mShowCancelButton;
        MediaSessionCompat.Token mToken;
        
        public MediaStyle() {
            this.mActionsToShowInCompact = null;
        }
        
        public MediaStyle(final NotificationCompat.Builder builder) {
            this.mActionsToShowInCompact = null;
            ((Style)this).setBuilder(builder);
        }
        
        private RemoteViews generateMediaActionButton(final Action action) {
            int n = 0;
            if (action.getActionIntent() == null) {
                n = 1;
            }
            final RemoteViews remoteViews = new RemoteViews(this.mBuilder.mContext.getPackageName(), R.layout.notification_media_action);
            remoteViews.setImageViewResource(R.id.action0, action.getIcon());
            if (n == 0) {
                remoteViews.setOnClickPendingIntent(R.id.action0, action.getActionIntent());
            }
            if (Build$VERSION.SDK_INT >= 15) {
                remoteViews.setContentDescription(R.id.action0, action.getTitle());
            }
            return remoteViews;
        }
        
        public static MediaSessionCompat.Token getMediaSession(final Notification notification) {
            final Bundle extras = NotificationCompat.getExtras(notification);
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
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @Override
        public void apply(final NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            if (Build$VERSION.SDK_INT < 21) {
                if (this.mShowCancelButton) {
                    notificationBuilderWithBuilderAccessor.getBuilder().setOngoing(true);
                }
            }
            else {
                notificationBuilderWithBuilderAccessor.getBuilder().setStyle((Notification$Style)this.fillInMediaStyle(new Notification$MediaStyle()));
            }
        }
        
        @RequiresApi(21)
        Notification$MediaStyle fillInMediaStyle(final Notification$MediaStyle notification$MediaStyle) {
            if (this.mActionsToShowInCompact != null) {
                notification$MediaStyle.setShowActionsInCompactView(this.mActionsToShowInCompact);
            }
            if (this.mToken != null) {
                notification$MediaStyle.setMediaSession((MediaSession$Token)this.mToken.getToken());
            }
            return notification$MediaStyle;
        }
        
        RemoteViews generateBigContentView() {
            final int min = Math.min(this.mBuilder.mActions.size(), 5);
            final RemoteViews applyStandardTemplate = ((Style)this).applyStandardTemplate(false, this.getBigContentViewLayoutResource(min), false);
            applyStandardTemplate.removeAllViews(R.id.media_actions);
            if (min > 0) {
                for (int i = 0; i < min; ++i) {
                    applyStandardTemplate.addView(R.id.media_actions, this.generateMediaActionButton(this.mBuilder.mActions.get(i)));
                }
            }
            if (!this.mShowCancelButton) {
                applyStandardTemplate.setViewVisibility(R.id.cancel_action, 8);
            }
            else {
                applyStandardTemplate.setViewVisibility(R.id.cancel_action, 0);
                applyStandardTemplate.setInt(R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
                applyStandardTemplate.setOnClickPendingIntent(R.id.cancel_action, this.mCancelButtonIntent);
            }
            return applyStandardTemplate;
        }
        
        RemoteViews generateContentView() {
            final RemoteViews applyStandardTemplate = ((Style)this).applyStandardTemplate(false, this.getContentViewLayoutResource(), true);
            final int size = this.mBuilder.mActions.size();
            int min;
            if (this.mActionsToShowInCompact != null) {
                min = Math.min(this.mActionsToShowInCompact.length, 3);
            }
            else {
                min = 0;
            }
            applyStandardTemplate.removeAllViews(R.id.media_actions);
            if (min > 0) {
                for (int i = 0; i < min; ++i) {
                    if (i >= size) {
                        throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", i, size - 1));
                    }
                    applyStandardTemplate.addView(R.id.media_actions, this.generateMediaActionButton(this.mBuilder.mActions.get(this.mActionsToShowInCompact[i])));
                }
            }
            if (!this.mShowCancelButton) {
                applyStandardTemplate.setViewVisibility(R.id.end_padder, 0);
                applyStandardTemplate.setViewVisibility(R.id.cancel_action, 8);
            }
            else {
                applyStandardTemplate.setViewVisibility(R.id.end_padder, 8);
                applyStandardTemplate.setViewVisibility(R.id.cancel_action, 0);
                applyStandardTemplate.setOnClickPendingIntent(R.id.cancel_action, this.mCancelButtonIntent);
                applyStandardTemplate.setInt(R.id.cancel_action, "setAlpha", this.mBuilder.mContext.getResources().getInteger(R.integer.cancel_button_image_alpha));
            }
            return applyStandardTemplate;
        }
        
        int getBigContentViewLayoutResource(int n) {
            if (n > 3) {
                n = R.layout.notification_template_big_media;
            }
            else {
                n = R.layout.notification_template_big_media_narrow;
            }
            return n;
        }
        
        int getContentViewLayoutResource() {
            return R.layout.notification_template_media;
        }
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @Override
        public RemoteViews makeBigContentView(final NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            if (Build$VERSION.SDK_INT < 21) {
                return this.generateBigContentView();
            }
            return null;
        }
        
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        @Override
        public RemoteViews makeContentView(final NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            if (Build$VERSION.SDK_INT < 21) {
                return this.generateContentView();
            }
            return null;
        }
        
        public MediaStyle setCancelButtonIntent(final PendingIntent mCancelButtonIntent) {
            this.mCancelButtonIntent = mCancelButtonIntent;
            return this;
        }
        
        public MediaStyle setMediaSession(final MediaSessionCompat.Token mToken) {
            this.mToken = mToken;
            return this;
        }
        
        public MediaStyle setShowActionsInCompactView(final int... mActionsToShowInCompact) {
            this.mActionsToShowInCompact = mActionsToShowInCompact;
            return this;
        }
        
        public MediaStyle setShowCancelButton(final boolean mShowCancelButton) {
            if (Build$VERSION.SDK_INT < 21) {
                this.mShowCancelButton = mShowCancelButton;
            }
            return this;
        }
    }
}
