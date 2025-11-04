// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import com.lumiyaviewer.lumiya.cloud.common.CloudSyncMessenger;
import android.content.pm.PackageManager$NameNotFoundException;
import com.lumiyaviewer.lumiya.cloud.common.Bundleable;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStart;
import android.os.IBinder;
import com.google.common.base.Strings;
import android.net.Uri;
import java.util.List;
import android.content.ComponentName;
import android.content.Intent;
import com.lumiyaviewer.lumiya.Debug;
import android.os.Bundle;
import android.os.Message;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesFlushed;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesCompleted;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import android.os.Messenger;
import android.os.Handler;
import android.content.Context;
import android.content.ServiceConnection;

public class CloudSyncServiceConnection implements ServiceConnection
{
    private static final int REQUIRED_PLUGIN_VERSION = 1;
    private final Context context;
    private final Handler fromPluginHandler;
    private final Messenger fromPluginMessenger;
    private final Handler mainThreadHandler;
    private final AtomicBoolean syncingStarted;
    @Nullable
    private Messenger toPluginMessenger;
    private final UserManager userManager;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues() {
        if (CloudSyncServiceConnection.-com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues != null) {
            return CloudSyncServiceConnection.-com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues = new int[LogSyncStatus.Status.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues[LogSyncStatus.Status.AppVersionRejected.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues[LogSyncStatus.Status.GoogleDriveError.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues[LogSyncStatus.Status.Ready.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues = -com-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues;
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
    
    public CloudSyncServiceConnection(final Context context, final UserManager userManager) {
        this.mainThreadHandler = new Handler();
        this.syncingStarted = new AtomicBoolean(false);
        this.fromPluginHandler = new Handler() {
            private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues() {
                if (CloudSyncServiceConnection$1.-com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues != null) {
                    return CloudSyncServiceConnection$1.-com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues;
                }
                int[] -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues = new int[MessageType.values().length];
                while (true) {
                    try {
                        -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues[MessageType.LogFlushMessages.ordinal()] = 4;
                        try {
                            -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues[MessageType.LogMessageBatch.ordinal()] = 5;
                            try {
                                -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues[MessageType.LogMessagesCompleted.ordinal()] = 1;
                                try {
                                    -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues[MessageType.LogMessagesFlushed.ordinal()] = 2;
                                    try {
                                        -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues[MessageType.LogSyncStart.ordinal()] = 6;
                                        try {
                                            -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues[MessageType.LogSyncStatus.ordinal()] = 3;
                                            return -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues = -com-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues;
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError2) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError3) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError4) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError5) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError6) {
                        continue;
                    }
                    break;
                }
            }
            
            public void handleMessage(final Message message) {
                if (message.what != 100 || !(message.obj instanceof Bundle)) {
                    return;
                }
                final Bundle bundle = (Bundle)message.obj;
                if (!bundle.containsKey("message") || !bundle.containsKey("messageType")) {
                    return;
                }
                while (true) {
                    Label_0151: {
                        Label_0124: {
                            try {
                                switch (-getcom-lumiyaviewer-lumiya-cloud-common-MessageTypeSwitchesValues()[MessageType.valueOf(bundle.getString("messageType")).ordinal()]) {
                                    case 3: {
                                        CloudSyncServiceConnection.this.onLogSyncStatus(new LogSyncStatus(bundle.getBundle("message")));
                                        break;
                                    }
                                    case 1: {
                                        break Label_0124;
                                    }
                                    case 2: {
                                        break Label_0151;
                                    }
                                }
                                return;
                            }
                            catch (final Exception ex) {
                                Debug.Warning(ex);
                                return;
                            }
                            return;
                        }
                        CloudSyncServiceConnection.this.onLogMessagesCompleted(new LogMessagesCompleted(bundle.getBundle("message")));
                        return;
                    }
                    CloudSyncServiceConnection.this.onLogMessagesFlushed(new LogMessagesFlushed(bundle.getBundle("message")));
                }
            }
        };
        this.context = context;
        this.userManager = userManager;
        this.fromPluginMessenger = new Messenger(this.fromPluginHandler);
    }
    
    public static boolean checkPluginInstalled(final Context context) {
        final boolean b = false;
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.cloud", "com.lumiyaviewer.lumiya.cloud.DriveSyncService"));
        final List queryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        boolean b2 = b;
        if (queryIntentServices != null) {
            b2 = b;
            if (queryIntentServices.size() > 0) {
                b2 = true;
            }
        }
        return b2;
    }
    
    private void onLogMessagesCompleted(final LogMessagesCompleted logMessagesCompleted) {
        Debug.Printf("LumiyaCloud: written messages until %d for agent %s", logMessagesCompleted.lastWrittenMessageID, logMessagesCompleted.agentUUID);
        if (this.userManager.getUserID().equals(logMessagesCompleted.agentUUID)) {
            this.userManager.getSyncManager().onMessagesWritten(logMessagesCompleted.lastWrittenMessageID);
        }
    }
    
    private void onLogMessagesFlushed(final LogMessagesFlushed logMessagesFlushed) {
        Debug.Printf("LumiyaCloud: flushed some messages for agent %s", logMessagesFlushed.agentUUID);
        if (this.userManager.getUserID().equals(logMessagesFlushed.agentUUID)) {
            this.userManager.getSyncManager().onMessagesFlushed(logMessagesFlushed.messageIDs);
        }
    }
    
    private void onLogSyncStatus(final LogSyncStatus logSyncStatus) {
        Debug.Printf("LumiyaCloud: got logSyncStatus %s, plugin version %d", logSyncStatus.status, logSyncStatus.pluginVersionCode);
        if (this.toPluginMessenger != null) {
            switch (-getcom-lumiyaviewer-lumiya-cloud-common-LogSyncStatus$StatusSwitchesValues()[logSyncStatus.status.ordinal()]) {
                case 3: {
                    if (logSyncStatus.pluginVersionCode >= 1) {
                        this.syncingStarted.set(true);
                        this.userManager.getSyncManager().startSyncing(this);
                        break;
                    }
                    final Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud"));
                    this.showSyncingError(this.context.getString(2131296456), this.context.getString(2131296457), intent);
                    break;
                }
                case 1: {
                    final Intent intent2 = new Intent("android.intent.action.VIEW");
                    intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya"));
                    this.showSyncingError(this.context.getString(2131296449), this.context.getString(2131296450), intent2);
                    break;
                }
                case 2: {
                    final Intent intent3 = new Intent("android.intent.action.VIEW");
                    intent3.setData(Uri.parse("https://drive.google.com/"));
                    final String string = this.context.getString(2131296451);
                    String s;
                    if (Strings.isNullOrEmpty(logSyncStatus.errorMessage)) {
                        s = this.context.getString(2131296452);
                    }
                    else {
                        s = this.context.getString(2131296453, new Object[] { logSyncStatus.errorMessage });
                    }
                    this.showSyncingError(string, s, intent3);
                    break;
                }
            }
        }
    }
    
    public void disconnect() {
        this.mainThreadHandler.post((Runnable)new _$Lambda$WmOOQW2pFqpMpXOyAP45N3kh7mE(this));
    }
    
    public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
        Debug.Printf("LumiyaCloud: service connected", new Object[0]);
        this.toPluginMessenger = new Messenger(binder);
        try {
            this.sendMessage(MessageType.LogSyncStart, new LogSyncStart(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode, this.userManager.getUserID()));
        }
        catch (final PackageManager$NameNotFoundException ex) {
            Debug.Warning((Throwable)ex);
        }
    }
    
    public void onServiceDisconnected(final ComponentName componentName) {
        Debug.Printf("LumiyaCloud: service disconnected", new Object[0]);
    }
    
    public boolean sendMessage(final MessageType messageType, final Bundleable bundleable) {
        return this.toPluginMessenger != null && CloudSyncMessenger.sendMessage(this.toPluginMessenger, messageType, bundleable, this.fromPluginMessenger);
    }
    
    public void showSyncingError(final String contentTitle, final String contentText, final Intent intent) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context);
        builder.setSmallIcon(2130837616).setContentTitle(contentTitle).setContentText(contentText).setDefaults(0).setOngoing(false).setAutoCancel(true).setContentIntent(PendingIntent.getActivity(this.context, 0, intent, 134217728)).setOnlyAlertOnce(true);
        ((NotificationManager)this.context.getSystemService("notification")).notify(2131755018, builder.build());
    }
    
    public void stopSyncing() {
        if (this.syncingStarted.getAndSet(false)) {
            if (this.toPluginMessenger != null) {
                this.userManager.getSyncManager().stopSyncing();
            }
        }
        else {
            this.disconnect();
        }
    }
}
