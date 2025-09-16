/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.Service
 *  android.content.Context
 *  android.content.Intent
 *  android.content.pm.PackageInfo
 *  android.content.pm.PackageManager$NameNotFoundException
 *  android.os.Bundle
 *  android.os.Handler
 *  android.os.IBinder
 *  android.os.Message
 *  android.os.Messenger
 */
package com.lumiyaviewer.lumiya.cloud;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.cloud.AgentSyncConnections;
import com.lumiyaviewer.lumiya.cloud.ConnectionResolutionActivity;
import com.lumiyaviewer.lumiya.cloud.Debug;
import com.lumiyaviewer.lumiya.cloud.DriveLogEntry;
import com.lumiyaviewer.lumiya.cloud.DriveSynchronizer;
import com.lumiyaviewer.lumiya.cloud.ErrorResolutionTracker;
import com.lumiyaviewer.lumiya.cloud.LogWriteTracker;
import com.lumiyaviewer.lumiya.cloud.MessageSyncBatch;
import com.lumiyaviewer.lumiya.cloud.common.CloudSyncMessenger;
import com.lumiyaviewer.lumiya.cloud.common.LogChatMessage;
import com.lumiyaviewer.lumiya.cloud.common.LogFlushMessages;
import com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesCompleted;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStart;
import com.lumiyaviewer.lumiya.cloud.common.LogSyncStatus;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DriveSyncService
extends Service
implements LogWriteTracker.OnLoggingDoneListener {
    private static final long PERIODIC_SYNC_INTERVAL = 30000L;
    private static final int REQUIRED_APP_VERSION = 58;
    private final AgentSyncConnections agentSyncConnections;
    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private final ErrorResolutionTracker errorResolutionTracker;
    private GoogleApiState googleApiState = GoogleApiState.Idle;
    private boolean isServiceBound = false;
    private GoogleApiClient mGoogleApiClient = null;
    private final Messenger mMessenger;
    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;
    private final Runnable periodicSync;
    private boolean periodicSyncEnabled = false;
    private final Handler periodicSyncHandler;
    private final Set<Messenger> syncRequestSources = new HashSet<Messenger>();
    private DriveSynchronizer synchronizer = null;

    public DriveSyncService() {
        this.agentSyncConnections = new AgentSyncConnections();
        this.errorResolutionTracker = new ErrorResolutionTracker((Context)this);
        this.periodicSyncHandler = new Handler();
        this.mMessenger = new Messenger((Handler)new IncomingHandler(this));
        this.connectionCallbacks = new GoogleApiClient.ConnectionCallbacks(this){
            final DriveSyncService this$0;
            {
                this.this$0 = driveSyncService;
            }

            /*
             * Enabled aggressive block sorting
             */
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Debug.Printf("LumiyaCloud: connected.", new Object[0]);
                if (this.this$0.synchronizer == null) {
                    DriveSyncService.access$702(this.this$0, GoogleApiState.Connected);
                    DriveSyncService.access$602(this.this$0, new DriveSynchronizer((Context)this.this$0, this.this$0.mGoogleApiClient, this.this$0));
                } else {
                    Debug.Printf("LumiyaCloud: resuming sync.", new Object[0]);
                    this.this$0.synchronizer.resumeSyncing();
                }
                this.this$0.periodicSyncHandler.removeCallbacks(this.this$0.periodicSync);
                this.this$0.periodicSyncHandler.postDelayed(this.this$0.periodicSync, 30000L);
                DriveSyncService.access$1002(this.this$0, true);
                this.this$0.processSyncReady();
            }

            @Override
            public void onConnectionSuspended(int n) {
                Debug.Printf("LumiyaCloud: connection suspended (%d)", n);
                if (this.this$0.synchronizer != null) {
                    this.this$0.synchronizer.suspendSyncing();
                }
                this.this$0.periodicSyncHandler.removeCallbacks(this.this$0.periodicSync);
                DriveSyncService.access$1002(this.this$0, false);
            }
        };
        this.periodicSync = new Runnable(this){
            final DriveSyncService this$0;
            {
                this.this$0 = driveSyncService;
            }

            @Override
            public void run() {
                if (this.this$0.synchronizer != null) {
                    Debug.Printf("FlushFiles: checking for files to flush", new Object[0]);
                    long l = System.currentTimeMillis();
                    this.this$0.synchronizer.flushOpenFiles(false, l);
                    this.this$0.periodicSyncHandler.removeCallbacks(this.this$0.periodicSync);
                    DriveSyncService.access$1002(this.this$0, false);
                    if (!this.this$0.synchronizer.isLoggingDone()) {
                        this.this$0.periodicSyncHandler.postDelayed(this.this$0.periodicSync, 30000L);
                        DriveSyncService.access$1002(this.this$0, true);
                    }
                }
            }
        };
        this.onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener(this){
            final DriveSyncService this$0;
            {
                this.this$0 = driveSyncService;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void onConnectionFailed(@Nonnull ConnectionResult connectionResult) {
                String string2;
                Debug.Printf("LumiyaCloud: connection failed, has resolution: %b", connectionResult.hasResolution());
                if (connectionResult.hasResolution()) {
                    ConnectionResolutionActivity.startForConnectionResolution((Context)this.this$0, connectionResult);
                    return;
                }
                Debug.Printf("LumiyaCloud: no resolution at all (%d), error message %s", connectionResult.getErrorCode(), connectionResult.getErrorMessage());
                String string3 = string2 = connectionResult.getErrorMessage();
                if (Strings.isNullOrEmpty(string2)) {
                    string3 = this.this$0.getString(2131099702, new Object[]{connectionResult.getErrorCode()});
                }
                this.this$0.notifyError(string3);
            }
        };
    }

    static /* synthetic */ void access$000(DriveSyncService driveSyncService, LogSyncStart logSyncStart, Messenger messenger) {
        driveSyncService.onLogSyncStart(logSyncStart, messenger);
    }

    static /* synthetic */ void access$100(DriveSyncService driveSyncService, LogMessageBatch logMessageBatch, Messenger messenger) {
        driveSyncService.onLogMessageBatch(logMessageBatch, messenger);
    }

    static /* synthetic */ boolean access$1002(DriveSyncService driveSyncService, boolean bl) {
        driveSyncService.periodicSyncEnabled = bl;
        return bl;
    }

    static /* synthetic */ void access$200(DriveSyncService driveSyncService, LogFlushMessages logFlushMessages) {
        driveSyncService.onFlushMessages(logFlushMessages);
    }

    static /* synthetic */ DriveSynchronizer access$602(DriveSyncService driveSyncService, DriveSynchronizer driveSynchronizer) {
        driveSyncService.synchronizer = driveSynchronizer;
        return driveSynchronizer;
    }

    static /* synthetic */ GoogleApiState access$702(DriveSyncService driveSyncService, GoogleApiState googleApiState) {
        driveSyncService.googleApiState = googleApiState;
        return googleApiState;
    }

    private String logFileNameForChatter(String string2) {
        return string2.replaceAll("[/.:*\\\\]", "_").trim() + ".txt";
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void notifyError(@Nullable String string2) {
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            for (Messenger messenger : this.syncRequestSources) {
                MessageType messageType = MessageType.LogSyncStatus;
                LogSyncStatus logSyncStatus = new LogSyncStatus(packageInfo.versionCode, LogSyncStatus.Status.GoogleDriveError, string2);
                CloudSyncMessenger.sendMessage(messenger, messageType, logSyncStatus, null);
            }
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException);
            return;
        }
        {
            this.syncRequestSources.clear();
            return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void onFlushMessages(LogFlushMessages logFlushMessages) {
        if (logFlushMessages.agentName != null && logFlushMessages.chatterName != null) {
            if (this.synchronizer != null) {
                this.synchronizer.flushFile(this.agentSyncConnections, logFlushMessages.agentUUID, logFlushMessages.agentName, this.logFileNameForChatter(logFlushMessages.chatterName));
            }
            return;
        }
        this.synchronizer.flushOpenFiles(true, System.currentTimeMillis());
    }

    private void onLogMessageBatch(LogMessageBatch logMessageBatch, Messenger object) {
        if (this.synchronizer != null && logMessageBatch != null && logMessageBatch.agentName != null) {
            MessageSyncBatch messageSyncBatch = new MessageSyncBatch(logMessageBatch, new MessageSyncBatch.OnMessageBatchSyncedListener(this, (Messenger)object, logMessageBatch){
                final DriveSyncService this$0;
                final LogMessageBatch val$message;
                final Messenger val$replyTo;
                {
                    this.this$0 = driveSyncService;
                    this.val$replyTo = messenger;
                    this.val$message = logMessageBatch;
                }

                @Override
                public void onMessageBatchSynced(MessageSyncBatch messageSyncBatch) {
                    CloudSyncMessenger.sendMessage(this.val$replyTo, MessageType.LogMessagesCompleted, new LogMessagesCompleted(this.val$message.agentUUID, this.val$message.lastMessageID), null);
                }
            for (LogChatMessage logChatMessage : logMessageBatch.messages) {
                if (logChatMessage == null || logChatMessage.chatterName == null || logChatMessage.messageText == null) continue;
                this.synchronizer.logString(this.agentSyncConnections, logMessageBatch.agentUUID, logMessageBatch.agentName, this.logFileNameForChatter(logChatMessage.chatterName), new DriveLogEntry(logChatMessage.messageText, messageSyncBatch, logChatMessage.messageID));
            }
        }
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void onLogSyncStart(LogSyncStart bundleable, Messenger messenger) {
        try {
            void var2_4;
            if (bundleable.appVersionCode < 58) {
                PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
                MessageType messageType = MessageType.LogSyncStatus;
                LogSyncStatus logSyncStatus = new LogSyncStatus(packageInfo.versionCode, LogSyncStatus.Status.AppVersionRejected, null);
                CloudSyncMessenger.sendMessage((Messenger)var2_4, messageType, logSyncStatus, null);
                return;
            }
            this.syncRequestSources.add((Messenger)var2_4);
            this.agentSyncConnections.addSyncConnection(bundleable.agentUUID, (Messenger)var2_4);
            this.updateGoogleApiConnection();
            this.processSyncReady();
            return;
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException);
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void processSyncReady() {
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            if (this.synchronizer == null) return;
            for (Messenger messenger : this.syncRequestSources) {
                MessageType messageType = MessageType.LogSyncStatus;
                LogSyncStatus logSyncStatus = new LogSyncStatus(packageInfo.versionCode, LogSyncStatus.Status.Ready, null);
                CloudSyncMessenger.sendMessage(messenger, messageType, logSyncStatus, null);
            }
            this.syncRequestSources.clear();
            return;
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void updateGoogleApiConnection() {
        switch (5.$SwitchMap$com$lumiyaviewer$lumiya$cloud$DriveSyncService$GoogleApiState[this.googleApiState.ordinal()]) {
            case 1: {
                if (this.syncRequestSources.isEmpty()) return;
                this.googleApiState = GoogleApiState.Connecting;
                Debug.Printf("Starting service.", new Object[0]);
                this.startService(new Intent((Context)this, DriveSyncService.class));
                if (this.mGoogleApiClient != null) return;
                this.mGoogleApiClient = new GoogleApiClient.Builder((Context)this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this.connectionCallbacks).addOnConnectionFailedListener(this.onConnectionFailedListener).build();
                this.mGoogleApiClient.connect();
            }
            default: {
                return;
            }
            case 2: 
            case 3: 
        }
        if (!this.syncRequestSources.isEmpty()) return;
        if (this.isServiceBound) return;
        if (this.synchronizer != null) {
            if (!this.synchronizer.isLoggingDone()) return;
        }
        this.googleApiState = GoogleApiState.Idle;
        this.periodicSyncHandler.removeCallbacks(this.periodicSync);
        this.periodicSyncEnabled = false;
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.disconnect();
            this.mGoogleApiClient = null;
            this.synchronizer = null;
        }
        this.stopSelf();
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        Debug.Printf("DriveSyncService is bound", new Object[0]);
        this.isServiceBound = true;
        return this.mMessenger.getBinder();
    }

    public void onDestroy() {
        Debug.Printf("Service destroyed", new Object[0]);
        super.onDestroy();
    }

    @Override
    public void onLoggingDone() {
        this.updateGoogleApiConnection();
    }

    @Override
    public void onLoggingNeeded() {
        if (!this.periodicSyncEnabled) {
            this.periodicSyncEnabled = true;
            this.periodicSyncHandler.postDelayed(this.periodicSync, 30000L);
        }
    }

    public int onStartCommand(Intent object, int n, int n2) {
        Debug.Printf("Service started.", new Object[0]);
        if (object.hasExtra("deleteResolvableError")) {
            object = UUID.fromString(object.getStringExtra("deleteResolvableError"));
            this.errorResolutionTracker.clearError((UUID)object, false);
            this.errorResolutionTracker.clearNotification();
        }
        return 2;
    }

    public boolean onUnbind(Intent intent) {
        Debug.Printf("DriveSyncService is unbound", new Object[0]);
        this.isServiceBound = false;
        if (this.synchronizer != null) {
            this.synchronizer.flushOpenFiles(true, System.currentTimeMillis());
        }
        this.updateGoogleApiConnection();
        return super.onUnbind(intent);
    }

    private static enum GoogleApiState {
        Idle,
        Connecting,
        Connected;

    }

    @SuppressLint(value={"HandlerLeak"})
    private class IncomingHandler
    extends Handler {
        final DriveSyncService this$0;

        private IncomingHandler(DriveSyncService driveSyncService) {
            this.this$0 = driveSyncService;
        }

        /*
         * Exception decompiling
         */
        public void handleMessage(Message var1_1) {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Back jump on a try block [egrp 1[TRYBLOCK] [2 : 130->190)] java.lang.Exception
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.insertExceptionBlocks(Op02WithProcessedDataAndRefs.java:2283)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:415)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }
    }
}

