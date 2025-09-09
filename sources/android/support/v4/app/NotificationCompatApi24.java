package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompatBase;
import android.support.v4.app.RemoteInputCompatBase;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiresApi(24)
class NotificationCompatApi24 {

    public static class Builder implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions {
        private Notification.Builder b;
        private int mGroupAlertBehavior;

        public Builder(Context context, Notification notification, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, RemoteViews remoteViews, int i, PendingIntent pendingIntent, PendingIntent pendingIntent2, Bitmap bitmap, int i2, int i3, boolean z, boolean z2, boolean z3, int i4, CharSequence charSequence4, boolean z4, String str, ArrayList<String> arrayList, Bundle bundle, int i5, int i6, Notification notification2, String str2, boolean z5, String str3, CharSequence[] charSequenceArr, RemoteViews remoteViews2, RemoteViews remoteViews3, RemoteViews remoteViews4, int i7) {
            this.b = new Notification.Builder(context).setWhen(notification.when).setShowWhen(z2).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, remoteViews).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS).setOngoing((notification.flags & 2) != 0).setOnlyAlertOnce((notification.flags & 8) != 0).setAutoCancel((notification.flags & 16) != 0).setDefaults(notification.defaults).setContentTitle(charSequence).setContentText(charSequence2).setSubText(charSequence4).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification.deleteIntent).setFullScreenIntent(pendingIntent2, (notification.flags & 128) != 0).setLargeIcon(bitmap).setNumber(i).setUsesChronometer(z3).setPriority(i4).setProgress(i2, i3, z).setLocalOnly(z4).setExtras(bundle).setGroup(str2).setGroupSummary(z5).setSortKey(str3).setCategory(str).setColor(i5).setVisibility(i6).setPublicVersion(notification2).setRemoteInputHistory(charSequenceArr);
            if (remoteViews2 != null) {
                this.b.setCustomContentView(remoteViews2);
            }
            if (remoteViews3 != null) {
                this.b.setCustomBigContentView(remoteViews3);
            }
            if (remoteViews4 != null) {
                this.b.setCustomHeadsUpContentView(remoteViews4);
            }
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                this.b.addPerson(it.next());
            }
            this.mGroupAlertBehavior = i7;
        }

        private void removeSoundAndVibration(Notification notification) {
            notification.sound = null;
            notification.vibrate = null;
            notification.defaults &= -2;
            notification.defaults &= -3;
        }

        public void addAction(NotificationCompatBase.Action action) {
            NotificationCompatApi24.addAction(this.b, action);
        }

        public Notification build() {
            Notification build = this.b.build();
            if (this.mGroupAlertBehavior != 0) {
                if (!(build.getGroup() == null || (build.flags & 512) == 0 || this.mGroupAlertBehavior != 2)) {
                    removeSoundAndVibration(build);
                }
                if (build.getGroup() != null && (build.flags & 512) == 0 && this.mGroupAlertBehavior == 1) {
                    removeSoundAndVibration(build);
                }
            }
            return build;
        }

        public Notification.Builder getBuilder() {
            return this.b;
        }
    }

    NotificationCompatApi24() {
    }

    public static void addAction(Notification.Builder builder, NotificationCompatBase.Action action) {
        Notification.Action.Builder builder2 = new Notification.Action.Builder(action.getIcon(), action.getTitle(), action.getActionIntent());
        if (action.getRemoteInputs() != null) {
            for (RemoteInput addRemoteInput : RemoteInputCompatApi20.fromCompat(action.getRemoteInputs())) {
                builder2.addRemoteInput(addRemoteInput);
            }
        }
        Bundle bundle = action.getExtras() == null ? new Bundle() : new Bundle(action.getExtras());
        bundle.putBoolean("android.support.allowGeneratedReplies", action.getAllowGeneratedReplies());
        builder2.setAllowGeneratedReplies(action.getAllowGeneratedReplies());
        builder2.addExtras(bundle);
        builder.addAction(builder2.build());
    }

    public static void addMessagingStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, CharSequence charSequence, CharSequence charSequence2, List<CharSequence> list, List<Long> list2, List<CharSequence> list3, List<String> list4, List<Uri> list5) {
        Notification.MessagingStyle conversationTitle = new Notification.MessagingStyle(charSequence).setConversationTitle(charSequence2);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= list.size()) {
                conversationTitle.setBuilder(notificationBuilderWithBuilderAccessor.getBuilder());
                return;
            }
            Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(list.get(i2), list2.get(i2).longValue(), list3.get(i2));
            if (list4.get(i2) != null) {
                message.setData(list4.get(i2), list5.get(i2));
            }
            conversationTitle.addMessage(message);
            i = i2 + 1;
        }
    }

    public static NotificationCompatBase.Action getAction(Notification notification, int i, NotificationCompatBase.Action.Factory factory, RemoteInputCompatBase.RemoteInput.Factory factory2) {
        return getActionCompatFromAction(notification.actions[i], factory, factory2);
    }

    private static NotificationCompatBase.Action getActionCompatFromAction(Notification.Action action, NotificationCompatBase.Action.Factory factory, RemoteInputCompatBase.RemoteInput.Factory factory2) {
        boolean z = false;
        RemoteInputCompatBase.RemoteInput[] compat = RemoteInputCompatApi20.toCompat(action.getRemoteInputs(), factory2);
        if (action.getExtras().getBoolean("android.support.allowGeneratedReplies") || action.getAllowGeneratedReplies()) {
            z = true;
        }
        return factory.build(action.icon, action.title, action.actionIntent, action.getExtras(), compat, (RemoteInputCompatBase.RemoteInput[]) null, z);
    }

    private static Notification.Action getActionFromActionCompat(NotificationCompatBase.Action action) {
        Notification.Action.Builder builder = new Notification.Action.Builder(action.getIcon(), action.getTitle(), action.getActionIntent());
        Bundle bundle = action.getExtras() == null ? new Bundle() : new Bundle(action.getExtras());
        bundle.putBoolean("android.support.allowGeneratedReplies", action.getAllowGeneratedReplies());
        builder.setAllowGeneratedReplies(action.getAllowGeneratedReplies());
        builder.addExtras(bundle);
        RemoteInputCompatBase.RemoteInput[] remoteInputs = action.getRemoteInputs();
        if (remoteInputs != null) {
            for (RemoteInput addRemoteInput : RemoteInputCompatApi20.fromCompat(remoteInputs)) {
                builder.addRemoteInput(addRemoteInput);
            }
        }
        return builder.build();
    }

    public static NotificationCompatBase.Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> arrayList, NotificationCompatBase.Action.Factory factory, RemoteInputCompatBase.RemoteInput.Factory factory2) {
        if (arrayList == null) {
            return null;
        }
        NotificationCompatBase.Action[] newArray = factory.newArray(arrayList.size());
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= newArray.length) {
                return newArray;
            }
            newArray[i2] = getActionCompatFromAction((Notification.Action) arrayList.get(i2), factory, factory2);
            i = i2 + 1;
        }
    }

    public static ArrayList<Parcelable> getParcelableArrayListForActions(NotificationCompatBase.Action[] actionArr) {
        if (actionArr == null) {
            return null;
        }
        ArrayList<Parcelable> arrayList = new ArrayList<>(actionArr.length);
        for (NotificationCompatBase.Action actionFromActionCompat : actionArr) {
            arrayList.add(getActionFromActionCompat(actionFromActionCompat));
        }
        return arrayList;
    }
}
