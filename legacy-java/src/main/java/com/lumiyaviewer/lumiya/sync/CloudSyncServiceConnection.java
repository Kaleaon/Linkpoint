package com.lumiyaviewer.lumiya.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.cloud.common.Bundleable;
import com.lumiyaviewer.lumiya.cloud.common.CloudSyncMessenger;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesCompleted;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesFlushed;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStart;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import com.lumiyaviewer.lumiya.licensing.LicenseChecker;
import com.lumiyaviewer.lumiya.slproto.avatar.SLMoveEvents;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class CloudSyncServiceConnection implements ServiceConnection {

    /* renamed from: -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f229x7f80e748 = null;
    private static final int REQUIRED_PLUGIN_VERSION = 1;
    private final Context context;

    @Nullable
    private Messenger toPluginMessenger;
    private final UserManager userManager;
    private final Handler mainThreadHandler = new Handler();
    private final AtomicBoolean syncingStarted = new AtomicBoolean(false);
    private final Handler fromPluginHandler = new Handler() { // from class: com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection.1

        /* renamed from: -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues, reason: not valid java name */
        private static final /* synthetic */ int[] f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$cloud$common$MessageType;

        /* renamed from: -getcom-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues, reason: not valid java name */
        private static /* synthetic */ int[] m394x26663ee2() {
            if (f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues != null) {
                return f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues;
            }
            int[] iArr = new int[MessageType.values().length];
            try {
                iArr[MessageType.LogFlushMessages.ordinal()] = 4;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[MessageType.LogMessageBatch.ordinal()] = 5;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[MessageType.LogMessagesCompleted.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[MessageType.LogMessagesFlushed.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[MessageType.LogSyncStart.ordinal()] = 6;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[MessageType.LogSyncStatus.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues = iArr;
            return iArr;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 100 && (message.obj instanceof Bundle)) {
                Bundle bundle = (Bundle) message.obj;
                if (bundle.containsKey("message") && bundle.containsKey("messageType")) {
                    try {
                        switch (m394x26663ee2()[MessageType.valueOf(bundle.getString("messageType")).ordinal()]) {
                            case 1:
                                CloudSyncServiceConnection.this.onLogMessagesCompleted(new LogMessagesCompleted(bundle.getBundle("message")));
                                break;
                            case 2:
                                CloudSyncServiceConnection.this.onLogMessagesFlushed(new LogMessagesFlushed(bundle.getBundle("message")));
                                break;
                            case 3:
                                CloudSyncServiceConnection.this.onLogSyncStatus(new LogSyncStatus(bundle.getBundle("message")));
                                break;
                        }
                    } catch (Exception e) {
                        Debug.Warning(e);
                    }
                }
            }
        }
    };
    private final Messenger fromPluginMessenger = new Messenger(this.fromPluginHandler);

    /* renamed from: -getcom-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m389xe2fcf3ec() {
        if (f229x7f80e748 != null) {
            return f229x7f80e748;
        }
        int[] iArr = new int[LogSyncStatus.Status.values().length];
        try {
            iArr[LogSyncStatus.Status.AppVersionRejected.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LogSyncStatus.Status.GoogleDriveError.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[LogSyncStatus.Status.Ready.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f229x7f80e748 = iArr;
        return iArr;
    }

    public CloudSyncServiceConnection(Context context, UserManager userManager) {
        this.context = context;
        this.userManager = userManager;
    }

    public static boolean checkPluginInstalled(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.cloud", "com.lumiyaviewer.lumiya.cloud.DriveSyncService"));
        List<ResolveInfo> queryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLogMessagesCompleted(LogMessagesCompleted logMessagesCompleted) {
        Debug.Printf("LumiyaCloud: written messages until %d for agent %s", Long.valueOf(logMessagesCompleted.lastWrittenMessageID), logMessagesCompleted.agentUUID);
        if (this.userManager.getUserID().equals(logMessagesCompleted.agentUUID)) {
            this.userManager.getSyncManager().onMessagesWritten(logMessagesCompleted.lastWrittenMessageID);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLogMessagesFlushed(LogMessagesFlushed logMessagesFlushed) {
        Debug.Printf("LumiyaCloud: flushed some messages for agent %s", logMessagesFlushed.agentUUID);
        if (this.userManager.getUserID().equals(logMessagesFlushed.agentUUID)) {
            this.userManager.getSyncManager().onMessagesFlushed(logMessagesFlushed.messageIDs);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLogSyncStatus(LogSyncStatus logSyncStatus) {
        Debug.Printf("LumiyaCloud: got logSyncStatus %s, plugin version %d", logSyncStatus.status, Integer.valueOf(logSyncStatus.pluginVersionCode));
        if (this.toPluginMessenger != null) {
            switch (m389xe2fcf3ec()[logSyncStatus.status.ordinal()]) {
                case 1:
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse(LicenseChecker.APP_STORE_URL));
                    showSyncingError(this.context.getString(R.string.cloud_sync_app_outdated), this.context.getString(R.string.cloud_sync_app_outdated_long), intent);
                    return;
                case 2:
                    Intent intent2 = new Intent("android.intent.action.VIEW");
                    intent2.setData(Uri.parse("https://drive.google.com/"));
                    showSyncingError(this.context.getString(R.string.cloud_sync_drive_error), Strings.isNullOrEmpty(logSyncStatus.errorMessage) ? this.context.getString(R.string.cloud_sync_drive_error_long) : this.context.getString(R.string.cloud_sync_drive_error_message, logSyncStatus.errorMessage), intent2);
                    return;
                case 3:
                    if (logSyncStatus.pluginVersionCode >= 1) {
                        this.syncingStarted.set(true);
                        this.userManager.getSyncManager().startSyncing(this);
                        return;
                    } else {
                        Intent intent3 = new Intent("android.intent.action.VIEW");
                        intent3.setData(Uri.parse(LicenseChecker.CLOUD_PLUGIN_URL));
                        showSyncingError(this.context.getString(R.string.cloud_sync_plugin_outdated), this.context.getString(R.string.cloud_sync_plugin_outdated_long), intent3);
                        return;
                    }
                default:
                    return;
            }
        }
    }

    public void disconnect() {
        this.mainThreadHandler.post(new Runnable() { // from class: com.lumiyaviewer.lumiya.sync.-$Lambda$WmOOQW2pFqpMpXOyAP45N3kh7mE
            private final /* synthetic */ void $m$0() {
                ((CloudSyncServiceConnection) this).m393x59c24a30();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_sync_CloudSyncServiceConnection_8286, reason: not valid java name */
    public /* synthetic */ void m393x59c24a30() {
        Debug.Printf("LumiyaCloud: disconnecting from sync service", new Object[0]);
        this.context.unbindService(this);
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Debug.Printf("LumiyaCloud: service connected", new Object[0]);
        this.toPluginMessenger = new Messenger(iBinder);
        try {
            sendMessage(MessageType.LogSyncStart, new LogSyncStart(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode, this.userManager.getUserID()));
        } catch (PackageManager.NameNotFoundException e) {
            Debug.Warning(e);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        Debug.Printf("LumiyaCloud: service disconnected", new Object[0]);
    }

    public boolean sendMessage(MessageType messageType, Bundleable bundleable) {
        if (this.toPluginMessenger != null) {
            return CloudSyncMessenger.sendMessage(this.toPluginMessenger, messageType, bundleable, this.fromPluginMessenger);
        }
        return false;
    }

    public void showSyncingError(String str, String str2, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);
        builder.setSmallIcon(R.drawable.ic_cloud_sync_notify).setContentTitle(str).setContentText(str2).setDefaults(0).setOngoing(false).setAutoCancel(true).setContentIntent(PendingIntent.getActivity(this.context, 0, intent, SLMoveEvents.AGENT_CONTROL_AWAY)).setOnlyAlertOnce(true);
        ((NotificationManager) this.context.getSystemService("notification")).notify(R.id.google_drive_problem_notify, builder.build());
    }

    public void stopSyncing() {
        if (!this.syncingStarted.getAndSet(false)) {
            disconnect();
        } else if (this.toPluginMessenger != null) {
            this.userManager.getSyncManager().stopSyncing();
        }
    }
}
