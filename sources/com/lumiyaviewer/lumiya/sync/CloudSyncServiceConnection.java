// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.cloud.common.Bundleable;
import com.lumiyaviewer.lumiya.cloud.common.CloudSyncMessenger;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesCompleted;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesFlushed;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStart;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import com.lumiyaviewer.lumiya.slproto.users.manager.SyncManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class CloudSyncServiceConnection
    implements ServiceConnection
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_LogSyncStatus$StatusSwitchesValues[];
    private static final int REQUIRED_PLUGIN_VERSION = 1;
    private final Context context;
    private final Handler fromPluginHandler = new Handler() {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_MessageTypeSwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$cloud$common$MessageType[];
        final CloudSyncServiceConnection this$0;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_MessageTypeSwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_MessageTypeSwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_MessageTypeSwitchesValues;
            }
            int ai[] = new int[MessageType.values().length];
            try
            {
                ai[MessageType.LogFlushMessages.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror5) { }
            try
            {
                ai[MessageType.LogMessageBatch.ordinal()] = 5;
            }
            catch (NoSuchFieldError nosuchfielderror4) { }
            try
            {
                ai[MessageType.LogMessagesCompleted.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[MessageType.LogMessagesFlushed.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[MessageType.LogSyncStart.ordinal()] = 6;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[MessageType.LogSyncStatus.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_MessageTypeSwitchesValues = ai;
            return ai;
        }

        public void handleMessage(Message message)
        {
            if (message.what != 100 || !(message.obj instanceof Bundle))
            {
                break MISSING_BLOCK_LABEL_157;
            }
            message = (Bundle)message.obj;
            if (!message.containsKey("message") || !message.containsKey("messageType"))
            {
                break MISSING_BLOCK_LABEL_157;
            }
            MessageType messagetype = MessageType.valueOf(message.getString("messageType"));
            _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_MessageTypeSwitchesValues()[messagetype.ordinal()];
            JVM INSTR tableswitch 1 3: default 157
        //                       1 115
        //                       2 136
        //                       3 88;
               goto _L1 _L2 _L3 _L4
_L1:
            break MISSING_BLOCK_LABEL_157;
_L4:
            CloudSyncServiceConnection._2D_wrap2(CloudSyncServiceConnection.this, new LogSyncStatus(message.getBundle("message")));
            return;
_L2:
            try
            {
                CloudSyncServiceConnection._2D_wrap0(CloudSyncServiceConnection.this, new LogMessagesCompleted(message.getBundle("message")));
                return;
            }
            // Misplaced declaration of an exception variable
            catch (Message message)
            {
                Debug.Warning(message);
                return;
            }
_L3:
            CloudSyncServiceConnection._2D_wrap1(CloudSyncServiceConnection.this, new LogMessagesFlushed(message.getBundle("message")));
            return;
        }

            
            {
                this$0 = CloudSyncServiceConnection.this;
                super();
            }
    };
    private final Messenger fromPluginMessenger;
    private final Handler mainThreadHandler = new Handler();
    private final AtomicBoolean syncingStarted = new AtomicBoolean(false);
    private Messenger toPluginMessenger;
    private final UserManager userManager;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_LogSyncStatus$StatusSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_LogSyncStatus$StatusSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_LogSyncStatus$StatusSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus.Status.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus.Status.AppVersionRejected.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus.Status.GoogleDriveError.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus.Status.Ready.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_LogSyncStatus$StatusSwitchesValues = ai;
        return ai;
    }

    static void _2D_wrap0(CloudSyncServiceConnection cloudsyncserviceconnection, LogMessagesCompleted logmessagescompleted)
    {
        cloudsyncserviceconnection.onLogMessagesCompleted(logmessagescompleted);
    }

    static void _2D_wrap1(CloudSyncServiceConnection cloudsyncserviceconnection, LogMessagesFlushed logmessagesflushed)
    {
        cloudsyncserviceconnection.onLogMessagesFlushed(logmessagesflushed);
    }

    static void _2D_wrap2(CloudSyncServiceConnection cloudsyncserviceconnection, LogSyncStatus logsyncstatus)
    {
        cloudsyncserviceconnection.onLogSyncStatus(logsyncstatus);
    }

    public CloudSyncServiceConnection(Context context1, UserManager usermanager)
    {
        context = context1;
        userManager = usermanager;
        fromPluginMessenger = new Messenger(fromPluginHandler);
    }

    public static boolean checkPluginInstalled(Context context1)
    {
        boolean flag1 = false;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.cloud", "com.lumiyaviewer.lumiya.cloud.DriveSyncService"));
        context1 = context1.getPackageManager().queryIntentServices(intent, 0);
        boolean flag = flag1;
        if (context1 != null)
        {
            flag = flag1;
            if (context1.size() > 0)
            {
                flag = true;
            }
        }
        return flag;
    }

    private void onLogMessagesCompleted(LogMessagesCompleted logmessagescompleted)
    {
        Debug.Printf("LumiyaCloud: written messages until %d for agent %s", new Object[] {
            Long.valueOf(logmessagescompleted.lastWrittenMessageID), logmessagescompleted.agentUUID
        });
        if (userManager.getUserID().equals(logmessagescompleted.agentUUID))
        {
            userManager.getSyncManager().onMessagesWritten(logmessagescompleted.lastWrittenMessageID);
        }
    }

    private void onLogMessagesFlushed(LogMessagesFlushed logmessagesflushed)
    {
        Debug.Printf("LumiyaCloud: flushed some messages for agent %s", new Object[] {
            logmessagesflushed.agentUUID
        });
        if (userManager.getUserID().equals(logmessagesflushed.agentUUID))
        {
            userManager.getSyncManager().onMessagesFlushed(logmessagesflushed.messageIDs);
        }
    }

    private void onLogSyncStatus(LogSyncStatus logsyncstatus)
    {
        Debug.Printf("LumiyaCloud: got logSyncStatus %s, plugin version %d", new Object[] {
            logsyncstatus.status, Integer.valueOf(logsyncstatus.pluginVersionCode)
        });
        if (toPluginMessenger == null) goto _L2; else goto _L1
_L1:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_cloud_2D_common_2D_LogSyncStatus$StatusSwitchesValues()[logsyncstatus.status.ordinal()];
        JVM INSTR tableswitch 1 3: default 72
    //                   1 145
    //                   2 189
    //                   3 73;
           goto _L2 _L3 _L4 _L5
_L2:
        return;
_L5:
        if (logsyncstatus.pluginVersionCode >= 1)
        {
            syncingStarted.set(true);
            userManager.getSyncManager().startSyncing(this);
            return;
        } else
        {
            logsyncstatus = new Intent("android.intent.action.VIEW");
            logsyncstatus.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud"));
            showSyncingError(context.getString(0x7f0900c8), context.getString(0x7f0900c9), logsyncstatus);
            return;
        }
_L3:
        logsyncstatus = new Intent("android.intent.action.VIEW");
        logsyncstatus.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya"));
        showSyncingError(context.getString(0x7f0900c1), context.getString(0x7f0900c2), logsyncstatus);
        return;
_L4:
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://drive.google.com/"));
        String s = context.getString(0x7f0900c3);
        if (Strings.isNullOrEmpty(logsyncstatus.errorMessage))
        {
            logsyncstatus = context.getString(0x7f0900c4);
        } else
        {
            logsyncstatus = context.getString(0x7f0900c5, new Object[] {
                logsyncstatus.errorMessage
            });
        }
        showSyncingError(s, logsyncstatus, intent);
        return;
    }

    public void disconnect()
    {
        mainThreadHandler.post(new _2D_.Lambda.WmOOQW2pFqpMpXOyAP45N3kh7mE(this));
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_sync_CloudSyncServiceConnection_8286()
    {
        Debug.Printf("LumiyaCloud: disconnecting from sync service", new Object[0]);
        context.unbindService(this);
    }

    public void onServiceConnected(ComponentName componentname, IBinder ibinder)
    {
        Debug.Printf("LumiyaCloud: service connected", new Object[0]);
        toPluginMessenger = new Messenger(ibinder);
        try
        {
            componentname = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sendMessage(MessageType.LogSyncStart, new LogSyncStart(((PackageInfo) (componentname)).versionCode, userManager.getUserID()));
            return;
        }
        // Misplaced declaration of an exception variable
        catch (ComponentName componentname)
        {
            Debug.Warning(componentname);
        }
    }

    public void onServiceDisconnected(ComponentName componentname)
    {
        Debug.Printf("LumiyaCloud: service disconnected", new Object[0]);
    }

    public boolean sendMessage(MessageType messagetype, Bundleable bundleable)
    {
        if (toPluginMessenger != null)
        {
            return CloudSyncMessenger.sendMessage(toPluginMessenger, messagetype, bundleable, fromPluginMessenger);
        } else
        {
            return false;
        }
    }

    public void showSyncingError(String s, String s1, Intent intent)
    {
        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context);
        builder.setSmallIcon(0x7f020070).setContentTitle(s).setContentText(s1).setDefaults(0).setOngoing(false).setAutoCancel(true).setContentIntent(PendingIntent.getActivity(context, 0, intent, 0x8000000)).setOnlyAlertOnce(true);
        ((NotificationManager)context.getSystemService("notification")).notify(0x7f10000a, builder.build());
    }

    public void stopSyncing()
    {
        if (syncingStarted.getAndSet(false))
        {
            if (toPluginMessenger != null)
            {
                userManager.getSyncManager().stopSyncing();
            }
            return;
        } else
        {
            disconnect();
            return;
        }
    }
}
