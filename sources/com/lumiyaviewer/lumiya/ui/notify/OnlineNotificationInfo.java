// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.notify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.ui.login.LoginActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.notify:
//            NotificationChannels

public class OnlineNotificationInfo
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues[];
    private final String contentText;
    private final boolean hasProgress;
    private final String titleText;
    private final boolean visible;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connecting.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Idle.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues = ai;
        return ai;
    }

    public OnlineNotificationInfo(boolean flag, Context context, String s, SLGridConnection slgridconnection, ChatterNameRetriever chatternameretriever, CurrentLocationInfo currentlocationinfo)
    {
        if (!flag || slgridconnection == null)
        {
            visible = false;
            titleText = null;
            contentText = null;
            hasProgress = false;
            return;
        }
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_SLGridConnection$ConnectionStateSwitchesValues()[slgridconnection.getConnectionState().ordinal()])
        {
        default:
            visible = false;
            titleText = null;
            contentText = null;
            hasProgress = false;
            return;

        case 2: // '\002'
            visible = true;
            titleText = s;
            if (slgridconnection.getIsReconnecting())
            {
                contentText = String.format(context.getResources().getString(0x7f090138), new Object[] {
                    s
                });
            } else
            {
                contentText = String.format(context.getResources().getString(0x7f090137), new Object[] {
                    s
                });
            }
            hasProgress = true;
            return;

        case 1: // '\001'
            visible = true;
            slgridconnection = chatternameretriever.getResolvedName();
            break;
        }
        int i;
        if (slgridconnection != null)
        {
            slgridconnection = (new StringBuilder()).append(s).append(": ").append(slgridconnection).toString();
        } else
        {
            slgridconnection = s;
        }
        titleText = slgridconnection;
        if (currentlocationinfo == null) goto _L2; else goto _L1
_L1:
        slgridconnection = currentlocationinfo.parcelData();
        if (slgridconnection != null)
        {
            slgridconnection = slgridconnection.getName();
        } else
        {
            slgridconnection = null;
        }
        if (slgridconnection == null || slgridconnection.equals("(loading)")) goto _L2; else goto _L3
_L3:
        i = currentlocationinfo.nearbyUsers();
        slgridconnection = String.format(context.getResources().getString(0x7f090136), new Object[] {
            slgridconnection, Integer.valueOf(i)
        });
_L5:
        chatternameretriever = slgridconnection;
        if (slgridconnection == null)
        {
            chatternameretriever = String.format(context.getResources().getString(0x7f090135), new Object[] {
                s
            });
        }
        contentText = chatternameretriever;
        hasProgress = false;
        return;
_L2:
        slgridconnection = null;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (obj instanceof OnlineNotificationInfo)
        {
            obj = (OnlineNotificationInfo)obj;
            boolean flag = flag1;
            if (Objects.equal(titleText, ((OnlineNotificationInfo) (obj)).titleText))
            {
                flag = flag1;
                if (Objects.equal(contentText, ((OnlineNotificationInfo) (obj)).contentText))
                {
                    flag = flag1;
                    if (visible == ((OnlineNotificationInfo) (obj)).visible)
                    {
                        flag = flag1;
                        if (hasProgress == ((OnlineNotificationInfo) (obj)).hasProgress)
                        {
                            flag = true;
                        }
                    }
                }
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public Notification getNotification(Context context)
    {
        if (visible)
        {
            android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context, NotificationChannels.getInstance().getChannelName(NotificationChannels.Channel.OnlineStatus));
            builder.setSmallIcon(0x7f02007b).setContentTitle(titleText).setContentText(contentText).setDefaults(0).setOngoing(true).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, com/lumiyaviewer/lumiya/ui/login/LoginActivity), 0x8000000)).setOnlyAlertOnce(true);
            if (hasProgress)
            {
                builder.setProgress(0, 0, true);
            }
            return builder.build();
        } else
        {
            return null;
        }
    }
}
