package com.lumiyaviewer.lumiya.ui.notify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.avatar.SLMoveEvents;
import com.lumiyaviewer.lumiya.ui.login.LoginActivity;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;

/* loaded from: classes.dex */
public class OnlineNotificationInfo {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f465x8f75539 = null;
    private final String contentText;
    private final boolean hasProgress;
    private final String titleText;
    private final boolean visible;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m678x1c568815() {
        if (f465x8f75539 != null) {
            return f465x8f75539;
        }
        int[] iArr = new int[SLGridConnection.ConnectionState.valuesCustom().length];
        try {
            iArr[SLGridConnection.ConnectionState.Connected.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[SLGridConnection.ConnectionState.Connecting.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[SLGridConnection.ConnectionState.Idle.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f465x8f75539 = iArr;
        return iArr;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00be  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public OnlineNotificationInfo(boolean r7, android.content.Context r8, java.lang.String r9, com.lumiyaviewer.lumiya.slproto.SLGridConnection r10, com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever r11, @android.support.annotation.Nullable com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo r12) {
        /*
            r6 = this;
            r5 = 1
            r4 = 0
            r1 = 0
            r6.<init>()
            if (r7 == 0) goto La
            if (r10 != 0) goto L13
        La:
            r6.visible = r4
            r6.titleText = r1
            r6.contentText = r1
            r6.hasProgress = r4
        L12:
            return
        L13:
            int[] r0 = m678x1c568815()
            com.lumiyaviewer.lumiya.slproto.SLGridConnection$ConnectionState r2 = r10.getConnectionState()
            int r2 = r2.ordinal()
            r0 = r0[r2]
            switch(r0) {
                case 1: goto L65;
                case 2: goto L2d;
                default: goto L24;
            }
        L24:
            r6.visible = r4
            r6.titleText = r1
            r6.contentText = r1
            r6.hasProgress = r4
            goto L12
        L2d:
            r6.visible = r5
            r6.titleText = r9
            boolean r0 = r10.getIsReconnecting()
            if (r0 == 0) goto L4f
            android.content.res.Resources r0 = r8.getResources()
            r1 = 2131296568(0x7f090138, float:1.8211056E38)
            java.lang.String r0 = r0.getString(r1)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r9
            java.lang.String r0 = java.lang.String.format(r0, r1)
            r6.contentText = r0
        L4c:
            r6.hasProgress = r5
            goto L12
        L4f:
            android.content.res.Resources r0 = r8.getResources()
            r1 = 2131296567(0x7f090137, float:1.8211054E38)
            java.lang.String r0 = r0.getString(r1)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r9
            java.lang.String r0 = java.lang.String.format(r0, r1)
            r6.contentText = r0
            goto L4c
        L65:
            r6.visible = r5
            java.lang.String r0 = r11.getResolvedName()
            if (r0 == 0) goto Ldb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.StringBuilder r2 = r2.append(r9)
            java.lang.String r3 = ": "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r0 = r0.toString()
        L85:
            r6.titleText = r0
            if (r12 == 0) goto Ld9
            com.lumiyaviewer.lumiya.slproto.users.ParcelData r0 = r12.parcelData()
            if (r0 == 0) goto Ld7
            java.lang.String r0 = r0.getName()
        L93:
            if (r0 == 0) goto Ld9
            java.lang.String r2 = "(loading)"
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto Ld9
            int r1 = r12.nearbyUsers()
            android.content.res.Resources r2 = r8.getResources()
            r3 = 2131296566(0x7f090136, float:1.8211052E38)
            java.lang.String r2 = r2.getString(r3)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r1)
            r3[r5] = r0
            java.lang.String r0 = java.lang.String.format(r2, r3)
        Lbc:
            if (r0 != 0) goto Ld1
            android.content.res.Resources r0 = r8.getResources()
            r1 = 2131296565(0x7f090135, float:1.821105E38)
            java.lang.String r0 = r0.getString(r1)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r9
            java.lang.String r0 = java.lang.String.format(r0, r1)
        Ld1:
            r6.contentText = r0
            r6.hasProgress = r4
            goto L12
        Ld7:
            r0 = r1
            goto L93
        Ld9:
            r0 = r1
            goto Lbc
        Ldb:
            r0 = r9
            goto L85
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.notify.OnlineNotificationInfo.<init>(boolean, android.content.Context, java.lang.String, com.lumiyaviewer.lumiya.slproto.SLGridConnection, com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever, com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo):void");
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OnlineNotificationInfo)) {
            return false;
        }
        OnlineNotificationInfo onlineNotificationInfo = (OnlineNotificationInfo) obj;
        return Objects.equal(this.titleText, onlineNotificationInfo.titleText) && Objects.equal(this.contentText, onlineNotificationInfo.contentText) && this.visible == onlineNotificationInfo.visible && this.hasProgress == onlineNotificationInfo.hasProgress;
    }

    public Notification getNotification(Context context) {
        if (!this.visible) {
            return null;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationChannels.getInstance().getChannelName(NotificationChannels.Channel.OnlineStatus));
        builder.setSmallIcon(R.drawable.ic_online_notify).setContentTitle(this.titleText).setContentText(this.contentText).setDefaults(0).setOngoing(true).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) LoginActivity.class), SLMoveEvents.AGENT_CONTROL_AWAY)).setOnlyAlertOnce(true);
        if (this.hasProgress) {
            builder.setProgress(0, 0, true);
        }
        return builder.build();
    }
}
