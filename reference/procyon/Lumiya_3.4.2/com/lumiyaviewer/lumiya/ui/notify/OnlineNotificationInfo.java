// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.notify;

import android.app.PendingIntent;
import android.content.Intent;
import com.lumiyaviewer.lumiya.ui.login.LoginActivity;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import android.support.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;

public class OnlineNotificationInfo
{
    private final String contentText;
    private final boolean hasProgress;
    private final String titleText;
    private final boolean visible;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues() {
        if (OnlineNotificationInfo.-com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues != null) {
            return OnlineNotificationInfo.-com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues = new int[SLGridConnection.ConnectionState.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues[SLGridConnection.ConnectionState.Connected.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues[SLGridConnection.ConnectionState.Connecting.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues[SLGridConnection.ConnectionState.Idle.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues = -com-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    public OnlineNotificationInfo(final boolean b, final Context context, final String s, final SLGridConnection slGridConnection, final ChatterNameRetriever chatterNameRetriever, @Nullable final CurrentLocationInfo currentLocationInfo) {
        if (!b || slGridConnection == null) {
            this.visible = false;
            this.titleText = null;
            this.contentText = null;
            this.hasProgress = false;
        }
        else {
            switch (-getcom-lumiyaviewer-lumiya-slproto-SLGridConnection$ConnectionStateSwitchesValues()[slGridConnection.getConnectionState().ordinal()]) {
                default: {
                    this.visible = false;
                    this.titleText = null;
                    this.contentText = null;
                    this.hasProgress = false;
                    break;
                }
                case 2: {
                    this.visible = true;
                    this.titleText = s;
                    if (slGridConnection.getIsReconnecting()) {
                        this.contentText = String.format(context.getResources().getString(2131296568), s);
                    }
                    else {
                        this.contentText = String.format(context.getResources().getString(2131296567), s);
                    }
                    this.hasProgress = true;
                    break;
                }
                case 1: {
                    this.visible = true;
                    final String resolvedName = chatterNameRetriever.getResolvedName();
                    String string;
                    if (resolvedName != null) {
                        string = s + ": " + resolvedName;
                    }
                    else {
                        string = s;
                    }
                    this.titleText = string;
                    while (true) {
                        Label_0345: {
                            if (currentLocationInfo == null) {
                                break Label_0345;
                            }
                            final ParcelData parcelData = currentLocationInfo.parcelData();
                            String name;
                            if (parcelData != null) {
                                name = parcelData.getName();
                            }
                            else {
                                name = null;
                            }
                            if (name == null || name.equals("(loading)")) {
                                break Label_0345;
                            }
                            final String format = String.format(context.getResources().getString(2131296566), name, currentLocationInfo.nearbyUsers());
                            String format2 = format;
                            if (format == null) {
                                format2 = String.format(context.getResources().getString(2131296565), s);
                            }
                            this.contentText = format2;
                            this.hasProgress = false;
                            break;
                        }
                        final String format = null;
                        continue;
                    }
                }
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o instanceof OnlineNotificationInfo) {
            final OnlineNotificationInfo onlineNotificationInfo = (OnlineNotificationInfo)o;
            boolean b2 = b;
            if (Objects.equal(this.titleText, onlineNotificationInfo.titleText)) {
                b2 = b;
                if (Objects.equal(this.contentText, onlineNotificationInfo.contentText)) {
                    b2 = b;
                    if (this.visible == onlineNotificationInfo.visible) {
                        b2 = b;
                        if (this.hasProgress == onlineNotificationInfo.hasProgress) {
                            b2 = true;
                        }
                    }
                }
            }
            return b2;
        }
        return false;
    }
    
    public Notification getNotification(final Context context) {
        if (this.visible) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationChannels.getInstance().getChannelName(NotificationChannels.Channel.OnlineStatus));
            builder.setSmallIcon(2130837627).setContentTitle(this.titleText).setContentText(this.contentText).setDefaults(0).setOngoing(true).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, (Class)LoginActivity.class), 134217728)).setOnlyAlertOnce(true);
            if (this.hasProgress) {
                builder.setProgress(0, 0, true);
            }
            return builder.build();
        }
        return null;
    }
}
