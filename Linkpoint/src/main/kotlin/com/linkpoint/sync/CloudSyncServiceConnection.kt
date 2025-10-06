package com.linkpoint.sync

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.support.v4.app.NotificationCompat
import com.google.common.base.Strings
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.cloud.common.Bundleable
import com.linkpoint.cloud.common.CloudSyncMessenger
import com.linkpoint.cloud.common.LogMessagesCompleted
import com.linkpoint.cloud.common.LogMessagesFlushed
import com.linkpoint.cloud.common.LogSyncStart
import com.linkpoint.cloud.common.LogSyncStatus
import com.linkpoint.cloud.common.MessageType
import com.linkpoint.licensing.LicenseChecker
import com.linkpoint.slproto.avatar.SLMoveEvents
import com.linkpoint.slproto.users.manager.UserManager
import java.util.List
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.Nullable

class CloudSyncServiceConnection : ServiceConnection {

    /* renamed from: -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ Int[] f229comlumiyaviewerlumiyacloudcommonLogSyncStatus$StatusSwitchesValues = null
    private const val REQUIRED_PLUGIN_VERSION: Int = 1
    private val Context context
    private val Handler fromPluginHandler = Handler() {

        /* renamed from: -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues  reason: not valid java name */
        private const val /* synthetic */ Int[] f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues = null
        final /* synthetic */ Int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$cloud$common$MessageType

        /* renamed from: -getcom-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues  reason: not valid java name */
        @JvmStatic
private /* synthetic */ Int[] m384getcomlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues() {
            if (f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues != null) {
                return f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues
            }
            Int[] iArr = Int[MessageType.values().length]
            try {
                iArr[MessageType.LogFlushMessages.ordinal()] = 4
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[MessageType.LogMessageBatch.ordinal()] = 5
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[MessageType.LogMessagesCompleted.ordinal()] = 1
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[MessageType.LogMessagesFlushed.ordinal()] = 2
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[MessageType.LogSyncStart.ordinal()] = 6
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[MessageType.LogSyncStatus.ordinal()] = 3
            } catch (NoSuchFieldError e6) {
            }
            f230comlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues = iArr
            return iArr
        }

        fun handleMessage(Message message) {
            if (message.what == 100 && (message.obj instanceof Bundle)) {
                Bundle bundle = (Bundle) message.obj
                if (bundle.containsKey("message") && bundle.containsKey("messageType")) {
                    try {
                        switch (m384getcomlumiyaviewerlumiyacloudcommonMessageTypeSwitchesValues()[MessageType.valueOf(bundle.getString("messageType")).ordinal()]) {
                            case 1:
                                CloudSyncServiceConnection.this.onLogMessagesCompleted(LogMessagesCompleted(bundle.getBundle("message")))
                                return
                            case 2:
                                CloudSyncServiceConnection.this.onLogMessagesFlushed(LogMessagesFlushed(bundle.getBundle("message")))
                                return
                            case 3:
                                CloudSyncServiceConnection.this.onLogSyncStatus(LogSyncStatus(bundle.getBundle("message")))
                                return
                            default:
                                return
                        }
                    } catch (Exception e) {
                        Debug.Warning(e)
                    }
                    Debug.Warning(e)
                }
            }
        }
    }
    private val Messenger fromPluginMessenger
    private val Handler mainThreadHandler = Handler()
    private val AtomicBoolean syncingStarted = AtomicBoolean(false)
    private Messenger toPluginMessenger
    private val UserManager userManager

    /* renamed from: -getcom-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ Int[] m379getcomlumiyaviewerlumiyacloudcommonLogSyncStatus$StatusSwitchesValues() {
        if (f229comlumiyaviewerlumiyacloudcommonLogSyncStatus$StatusSwitchesValues != null) {
            return f229comlumiyaviewerlumiyacloudcommonLogSyncStatus$StatusSwitchesValues
        }
        Int[] iArr = Int[LogSyncStatus.Status.values().length]
        try {
            iArr[LogSyncStatus.Status.AppVersionRejected.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LogSyncStatus.Status.GoogleDriveError.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[LogSyncStatus.Status.Ready.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f229comlumiyaviewerlumiyacloudcommonLogSyncStatus$StatusSwitchesValues = iArr
        return iArr
    }

    public CloudSyncServiceConnection(Context context2, UserManager userManager2) {
        this.context = context2
        this.userManager = userManager2
        this.fromPluginMessenger = Messenger(this.fromPluginHandler)
    }

    @JvmStatic
    Boolean checkPluginInstalled(Context context2) {
        Intent intent = Intent()
        intent.setComponent(ComponentName("com.linkpoint.cloud", "com.linkpoint.cloud.DriveSyncService"))
        List<ResolveInfo> queryIntentServices = context2.getPackageManager().queryIntentServices(intent, 0)
        return queryIntentServices != null && queryIntentServices.size() > 0
    }

    /* access modifiers changed from: private */
    fun onLogMessagesCompleted(LogMessagesCompleted logMessagesCompleted) {
        Debug.Printf("LinkpointCloud: written messages until %d for agent %s", Long.valueOf(logMessagesCompleted.lastWrittenMessageID), logMessagesCompleted.agentUUID)
        if (this.userManager.getUserID().equals(logMessagesCompleted.agentUUID)) {
            this.userManager.getSyncManager().onMessagesWritten(logMessagesCompleted.lastWrittenMessageID)
        }
    }

    /* access modifiers changed from: private */
    fun onLogMessagesFlushed(LogMessagesFlushed logMessagesFlushed) {
        Debug.Printf("LinkpointCloud: flushed some messages for agent %s", logMessagesFlushed.agentUUID)
        if (this.userManager.getUserID().equals(logMessagesFlushed.agentUUID)) {
            this.userManager.getSyncManager().onMessagesFlushed(logMessagesFlushed.messageIDs)
        }
    }

    /* access modifiers changed from: private */
    fun onLogSyncStatus(LogSyncStatus logSyncStatus) {
        String string
        Debug.Printf("LinkpointCloud: got logSyncStatus %s, plugin version %d", logSyncStatus.status, Integer.valueOf(logSyncStatus.pluginVersionCode))
        if (this.toPluginMessenger != null) {
            switch (m379getcomlumiyaviewerlumiyacloudcommonLogSyncStatus$StatusSwitchesValues()[logSyncStatus.status.ordinal()]) {
                case 1:
                    Intent intent = Intent("android.intent.action.VIEW")
                    intent.setData(Uri.parse(LicenseChecker.APP_STORE_URL))
                    showSyncingError(this.context.getString(R.string.cloud_sync_app_outdated), this.context.getString(R.string.cloud_sync_app_outdated_long), intent)
                    return
                case 2:
                    Intent intent2 = Intent("android.intent.action.VIEW")
                    intent2.setData(Uri.parse("https://drive.google.com/"))
                    String string2 = this.context.getString(R.string.cloud_sync_drive_error)
                    if (Strings.isNullOrEmpty(logSyncStatus.errorMessage)) {
                        string = this.context.getString(R.string.cloud_sync_drive_error_long)
                    } else {
                        string = this.context.getString(R.string.cloud_sync_drive_error_message, Object[]{logSyncStatus.errorMessage})
                    }
                    showSyncingError(string2, string, intent2)
                    return
                case 3:
                    if (logSyncStatus.pluginVersionCode >= 1) {
                        this.syncingStarted.set(true)
                        this.userManager.getSyncManager().startSyncing(this)
                        return
                    }
                    Intent intent3 = Intent("android.intent.action.VIEW")
                    intent3.setData(Uri.parse(LicenseChecker.CLOUD_PLUGIN_URL))
                    showSyncingError(this.context.getString(R.string.cloud_sync_plugin_outdated), this.context.getString(R.string.cloud_sync_plugin_outdated_long), intent3)
                    return
                default:
                    return
            }
        }
    }

    fun disconnect() {
        this.mainThreadHandler.post($Lambda$WmOOQW2pFqpMpXOyAP45N3kh7mE(this))
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_sync_CloudSyncServiceConnection_8286  reason: not valid java name */
    public /* synthetic */ Unit m383lambda$com_lumiyaviewer_lumiya_sync_CloudSyncServiceConnection_8286() {
        Debug.Printf("LinkpointCloud: disconnecting from sync service", Object[0])
        this.context.unbindService(this)
    }

    fun onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Debug.Printf("LinkpointCloud: service connected", Object[0])
        this.toPluginMessenger = Messenger(iBinder)
        try {
            sendMessage(MessageType.LogSyncStart, LogSyncStart(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode, this.userManager.getUserID()))
        } catch (PackageManager.NameNotFoundException e) {
            Debug.Warning(e)
        }
    }

    fun onServiceDisconnected(ComponentName componentName) {
        Debug.Printf("LinkpointCloud: service disconnected", Object[0])
    }

    public Boolean sendMessage(MessageType messageType, Bundleable bundleable) {
        if (this.toPluginMessenger != null) {
            return CloudSyncMessenger.sendMessage(this.toPluginMessenger, messageType, bundleable, this.fromPluginMessenger)
        }
        return false
    }

    fun showSyncingError(String str, String str2, Intent intent) {
        NotificationCompat.Builder builder = NotificationCompat.Builder(this.context)
        builder.setSmallIcon(R.drawable.ic_cloud_sync_notify).setContentTitle(str).setContentText(str2).setDefaults(0).setOngoing(false).setAutoCancel(true).setContentIntent(PendingIntent.getActivity(this.context, 0, intent, SLMoveEvents.AGENT_CONTROL_AWAY)).setOnlyAlertOnce(true)
        ((NotificationManager) this.context.getSystemService("notification")).notify(R.id.google_drive_problem_notify, builder.build())
    }

    fun stopSyncing() {
        if (!this.syncingStarted.getAndSet(false)) {
            disconnect()
        } else if (this.toPluginMessenger != null) {
            this.userManager.getSyncManager().stopSyncing()
        }
    }
}
