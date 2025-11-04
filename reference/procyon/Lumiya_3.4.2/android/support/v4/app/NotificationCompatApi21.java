// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import java.util.Iterator;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.app.Notification;
import android.content.Context;
import android.widget.RemoteViews;
import android.app.Notification$Builder;
import java.util.Set;
import android.app.PendingIntent;
import android.os.Parcelable;
import android.os.Bundle;
import android.app.RemoteInput$Builder;
import android.app.RemoteInput;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class NotificationCompatApi21
{
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_ON_READ = "on_read";
    private static final String KEY_ON_REPLY = "on_reply";
    private static final String KEY_PARTICIPANTS = "participants";
    private static final String KEY_REMOTE_INPUT = "remote_input";
    private static final String KEY_TEXT = "text";
    private static final String KEY_TIMESTAMP = "timestamp";
    
    private static RemoteInput fromCompatRemoteInput(final RemoteInputCompatBase.RemoteInput remoteInput) {
        return new RemoteInput$Builder(remoteInput.getResultKey()).setLabel(remoteInput.getLabel()).setChoices(remoteInput.getChoices()).setAllowFreeFormInput(remoteInput.getAllowFreeFormInput()).addExtras(remoteInput.getExtras()).build();
    }
    
    static Bundle getBundleForUnreadConversation(final NotificationCompatBase.UnreadConversation unreadConversation) {
        int i = 0;
        String s = null;
        if (unreadConversation != null) {
            final Bundle bundle = new Bundle();
            if (unreadConversation.getParticipants() != null && unreadConversation.getParticipants().length > 1) {
                s = unreadConversation.getParticipants()[0];
            }
            Parcelable[] array;
            for (array = new Parcelable[unreadConversation.getMessages().length]; i < array.length; ++i) {
                final Bundle bundle2 = new Bundle();
                bundle2.putString("text", unreadConversation.getMessages()[i]);
                bundle2.putString("author", s);
                array[i] = (Parcelable)bundle2;
            }
            bundle.putParcelableArray("messages", array);
            final RemoteInputCompatBase.RemoteInput remoteInput = unreadConversation.getRemoteInput();
            if (remoteInput != null) {
                bundle.putParcelable("remote_input", (Parcelable)fromCompatRemoteInput(remoteInput));
            }
            bundle.putParcelable("on_reply", (Parcelable)unreadConversation.getReplyPendingIntent());
            bundle.putParcelable("on_read", (Parcelable)unreadConversation.getReadPendingIntent());
            bundle.putStringArray("participants", unreadConversation.getParticipants());
            bundle.putLong("timestamp", unreadConversation.getLatestTimestamp());
            return bundle;
        }
        return null;
    }
    
    static NotificationCompatBase.UnreadConversation getUnreadConversationFromBundle(final Bundle bundle, final NotificationCompatBase.UnreadConversation.Factory factory, final RemoteInputCompatBase.RemoteInput.Factory factory2) {
        final boolean b = false;
        if (bundle == null) {
            return null;
        }
        final Parcelable[] parcelableArray = bundle.getParcelableArray("messages");
        String[] array = null;
        Label_0022: {
            if (parcelableArray != null) {
                array = new String[parcelableArray.length];
                int i = 0;
                while (true) {
                    while (i < array.length) {
                        int n = b ? 1 : 0;
                        if (parcelableArray[i] instanceof Bundle) {
                            array[i] = ((Bundle)parcelableArray[i]).getString("text");
                            n = (b ? 1 : 0);
                            if (array[i] != null) {
                                ++i;
                                continue;
                            }
                        }
                        if (n == 0) {
                            return null;
                        }
                        break Label_0022;
                    }
                    int n = 1;
                    continue;
                }
            }
            array = null;
        }
        final PendingIntent pendingIntent = (PendingIntent)bundle.getParcelable("on_read");
        final PendingIntent pendingIntent2 = (PendingIntent)bundle.getParcelable("on_reply");
        final RemoteInput remoteInput = (RemoteInput)bundle.getParcelable("remote_input");
        final String[] stringArray = bundle.getStringArray("participants");
        if (stringArray != null && stringArray.length == 1) {
            RemoteInputCompatBase.RemoteInput compatRemoteInput;
            if (remoteInput == null) {
                compatRemoteInput = null;
            }
            else {
                compatRemoteInput = toCompatRemoteInput(remoteInput, factory2);
            }
            return factory.build(array, compatRemoteInput, pendingIntent2, pendingIntent, stringArray, bundle.getLong("timestamp"));
        }
        return null;
    }
    
    private static RemoteInputCompatBase.RemoteInput toCompatRemoteInput(final RemoteInput remoteInput, final RemoteInputCompatBase.RemoteInput.Factory factory) {
        return factory.build(remoteInput.getResultKey(), remoteInput.getLabel(), remoteInput.getChoices(), remoteInput.getAllowFreeFormInput(), remoteInput.getExtras(), null);
    }
    
    public static class Builder implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions
    {
        private Notification$Builder b;
        private RemoteViews mBigContentView;
        private RemoteViews mContentView;
        private Bundle mExtras;
        private int mGroupAlertBehavior;
        private RemoteViews mHeadsUpContentView;
        
        public Builder(final Context context, final Notification notification, final CharSequence contentTitle, final CharSequence contentText, final CharSequence contentInfo, final RemoteViews remoteViews, final int number, final PendingIntent contentIntent, final PendingIntent pendingIntent, final Bitmap largeIcon, final int n, final int n2, final boolean b, final boolean showWhen, final boolean usesChronometer, final int priority, final CharSequence subText, final boolean localOnly, final String category, final ArrayList<String> list, final Bundle bundle, final int color, final int visibility, final Notification publicVersion, final String group, final boolean groupSummary, final String sortKey, final RemoteViews mContentView, final RemoteViews mBigContentView, final RemoteViews mHeadsUpContentView, final int mGroupAlertBehavior) {
            this.b = new Notification$Builder(context).setWhen(notification.when).setShowWhen(showWhen).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, remoteViews).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS).setOngoing((notification.flags & 0x2) != 0x0).setOnlyAlertOnce((notification.flags & 0x8) != 0x0).setAutoCancel((notification.flags & 0x10) != 0x0).setDefaults(notification.defaults).setContentTitle(contentTitle).setContentText(contentText).setSubText(subText).setContentInfo(contentInfo).setContentIntent(contentIntent).setDeleteIntent(notification.deleteIntent).setFullScreenIntent(pendingIntent, (notification.flags & 0x80) != 0x0).setLargeIcon(largeIcon).setNumber(number).setUsesChronometer(usesChronometer).setPriority(priority).setProgress(n, n2, b).setLocalOnly(localOnly).setGroup(group).setGroupSummary(groupSummary).setSortKey(sortKey).setCategory(category).setColor(color).setVisibility(visibility).setPublicVersion(publicVersion);
            this.mExtras = new Bundle();
            if (bundle != null) {
                this.mExtras.putAll(bundle);
            }
            final Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                this.b.addPerson((String)iterator.next());
            }
            this.mContentView = mContentView;
            this.mBigContentView = mBigContentView;
            this.mHeadsUpContentView = mHeadsUpContentView;
            this.mGroupAlertBehavior = mGroupAlertBehavior;
        }
        
        private void removeSoundAndVibration(final Notification notification) {
            notification.sound = null;
            notification.vibrate = null;
            notification.defaults &= 0xFFFFFFFE;
            notification.defaults &= 0xFFFFFFFD;
        }
        
        @Override
        public void addAction(final NotificationCompatBase.Action action) {
            NotificationCompatApi20.addAction(this.b, action);
        }
        
        @Override
        public Notification build() {
            this.b.setExtras(this.mExtras);
            final Notification build = this.b.build();
            if (this.mContentView != null) {
                build.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                build.bigContentView = this.mBigContentView;
            }
            if (this.mHeadsUpContentView != null) {
                build.headsUpContentView = this.mHeadsUpContentView;
            }
            if (this.mGroupAlertBehavior != 0) {
                if (build.getGroup() != null && (build.flags & 0x200) != 0x0 && this.mGroupAlertBehavior == 2) {
                    this.removeSoundAndVibration(build);
                }
                if (build.getGroup() != null && (build.flags & 0x200) == 0x0 && this.mGroupAlertBehavior == 1) {
                    this.removeSoundAndVibration(build);
                }
            }
            return build;
        }
        
        @Override
        public Notification$Builder getBuilder() {
            return this.b;
        }
    }
}
