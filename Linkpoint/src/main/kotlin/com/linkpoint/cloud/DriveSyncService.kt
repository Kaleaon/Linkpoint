package com.linkpoint.cloud

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive
import com.google.common.base.Strings
import com.linkpoint.cloud.AgentSyncConnections
import com.linkpoint.cloud.ConnectionResolutionActivity
import com.linkpoint.cloud.Debug
import com.linkpoint.cloud.DriveLogEntry
import com.linkpoint.cloud.DriveSynchronizer
import com.linkpoint.cloud.ErrorResolutionTracker
import com.linkpoint.cloud.LogWriteTracker
import com.linkpoint.cloud.MessageSyncBatch
import com.linkpoint.cloud.common.CloudSyncMessenger
import com.linkpoint.cloud.common.LogChatMessage
import com.linkpoint.cloud.common.LogFlushMessages
import com.linkpoint.cloud.common.LogMessageBatch
import com.linkpoint.cloud.common.LogMessagesCompleted
import com.linkpoint.cloud.common.LogSyncStart
import com.linkpoint.cloud.common.LogSyncStatus
import com.linkpoint.cloud.common.MessageType
import java.util.HashSet
import java.util.Set
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class DriveSyncService : Service()
: LogWriteTracker.OnLoggingDoneListener {
    private const val PERIODIC_SYNC_INTERVAL: Long = 30000L
    private const val REQUIRED_APP_VERSION: Int = 58
    private val AgentSyncConnections agentSyncConnections
    private val GoogleApiClient.ConnectionCallbacks connectionCallbacks
    private val ErrorResolutionTracker errorResolutionTracker
    private GoogleApiState googleApiState = GoogleApiState.Idle
    private Boolean isServiceBound = false
    private GoogleApiClient mGoogleApiClient = null
    private val Messenger mMessenger
    private val GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener
    private val Runnable periodicSync
    private Boolean periodicSyncEnabled = false
    private val Handler periodicSyncHandler
    private val Set<Messenger> syncRequestSources = HashSet<Messenger>()
    private DriveSynchronizer synchronizer = null

    public DriveSyncService() {
        this.agentSyncConnections = AgentSyncConnections()
        this.errorResolutionTracker = ErrorResolutionTracker((Context)this)
        this.periodicSyncHandler = Handler()
        this.mMessenger = Messenger((Handler)IncomingHandler(this))
        this.connectionCallbacks = GoogleApiClient.ConnectionCallbacks(this){
            final DriveSyncService this$0
            {
                this.this$0 = driveSyncService
            }

            /*
             * Enabled aggressive block sorting
             */
            override Unit onConnected(Bundle bundle) {
                Debug.Printf("LinkpointCloud: connected.", Object[0])
                if (this.this$0.synchronizer == null) {
                    DriveSyncService.access$702(this.this$0, GoogleApiState.Connected)
                    DriveSyncService.access$602(this.this$0, DriveSynchronizer((Context)this.this$0, this.this$0.mGoogleApiClient, this.this$0))
                } else {
                    Debug.Printf("LinkpointCloud: resuming sync.", Object[0])
                    this.this$0.synchronizer.resumeSyncing()
                }
                this.this$0.periodicSyncHandler.removeCallbacks(this.this$0.periodicSync)
                this.this$0.periodicSyncHandler.postDelayed(this.this$0.periodicSync, 30000L)
                DriveSyncService.access$1002(this.this$0, true)
                this.this$0.processSyncReady()
            }

            override Unit onConnectionSuspended(Int n) {
                Debug.Printf("LinkpointCloud: connection suspended (%d)", n)
                if (this.this$0.synchronizer != null) {
                    this.this$0.synchronizer.suspendSyncing()
                }
                this.this$0.periodicSyncHandler.removeCallbacks(this.this$0.periodicSync)
                DriveSyncService.access$1002(this.this$0, false)
            }
        }
        this.periodicSync = Runnable(this){
            final DriveSyncService this$0
            {
                this.this$0 = driveSyncService
            }

            override Unit run() {
                if (this.this$0.synchronizer != null) {
                    Debug.Printf("FlushFiles: checking for files to flush", Object[0])
                    val l: Long = System.currentTimeMillis()
                    this.this$0.synchronizer.flushOpenFiles(false, l)
                    this.this$0.periodicSyncHandler.removeCallbacks(this.this$0.periodicSync)
                    DriveSyncService.access$1002(this.this$0, false)
                    if (!this.this$0.synchronizer.isLoggingDone()) {
                        this.this$0.periodicSyncHandler.postDelayed(this.this$0.periodicSync, 30000L)
                        DriveSyncService.access$1002(this.this$0, true)
                    }
                }
            }
        }
        this.onConnectionFailedListener = GoogleApiClient.OnConnectionFailedListener(this){
            final DriveSyncService this$0
            {
                this.this$0 = driveSyncService
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            override Unit onConnectionFailed(ConnectionResult connectionResult) {
                String string2
                Debug.Printf("LinkpointCloud: connection failed, has resolution: %b", connectionResult.hasResolution())
                if (connectionResult.hasResolution()) {
                    ConnectionResolutionActivity.startForConnectionResolution((Context)this.this$0, connectionResult)
                    return
                }
                Debug.Printf("LinkpointCloud: no resolution at all (%d), error message %s", connectionResult.getErrorCode(), connectionResult.getErrorMessage())
                val string3: String = string2 = connectionResult.getErrorMessage()
                if (Strings.isNullOrEmpty(string2)) {
                    string3 = this.this$0.getString(2131099702, Array<Any>{connectionResult.getErrorCode()})
                }
                this.this$0.notifyError(string3)
            }
        }
    }

    static /* synthetic */ Unit access$000(DriveSyncService driveSyncService, LogSyncStart logSyncStart, Messenger messenger) {
        driveSyncService.onLogSyncStart(logSyncStart, messenger)
    }

    static /* synthetic */ Unit access$100(DriveSyncService driveSyncService, LogMessageBatch logMessageBatch, Messenger messenger) {
        driveSyncService.onLogMessageBatch(logMessageBatch, messenger)
    }

    static /* synthetic */ Boolean access$1002(DriveSyncService driveSyncService, Boolean bl) {
        driveSyncService.periodicSyncEnabled = bl
        return bl
    }

    static /* synthetic */ Unit access$200(DriveSyncService driveSyncService, LogFlushMessages logFlushMessages) {
        driveSyncService.onFlushMessages(logFlushMessages)
    }

    static /* synthetic */ DriveSynchronizer access$602(DriveSyncService driveSyncService, DriveSynchronizer driveSynchronizer) {
        driveSyncService.synchronizer = driveSynchronizer
        return driveSynchronizer
    }

    static /* synthetic */ GoogleApiState access$702(DriveSyncService driveSyncService, GoogleApiState googleApiState) {
        driveSyncService.googleApiState = googleApiState
        return googleApiState
    }

     private fun logFileNameForChatter(string2: String): String {
        return string2.replaceAll("[/.:*\\\\]", "_").trim() + ".txt"
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
     private fun notifyError(string2: String) {
        try {
            val packageInfo: PackageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
            for (Messenger messenger : this.syncRequestSources) {
                val messageType: MessageType = MessageType.LogSyncStatus
                val logSyncStatus: LogSyncStatus = LogSyncStatus(packageInfo.versionCode, LogSyncStatus.Status.GoogleDriveError, string2)
                CloudSyncMessenger.sendMessage(messenger, messageType, logSyncStatus, null)
            }
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException)
            return
        }
        {
            this.syncRequestSources.clear()
            return
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
     private fun onFlushMessages(logFlushMessages: LogFlushMessages) {
        if (logFlushMessages.agentName != null && logFlushMessages.chatterName != null) {
            if (this.synchronizer != null) {
                this.synchronizer.flushFile(this.agentSyncConnections, logFlushMessages.agentUUID, logFlushMessages.agentName, this.logFileNameForChatter(logFlushMessages.chatterName))
            }
            return
        }
        this.synchronizer.flushOpenFiles(true, System.currentTimeMillis())
    }

     private fun onLogMessageBatch(logMessageBatch: LogMessageBatch, object: Messenger) {
        if (this.synchronizer != null && logMessageBatch != null && logMessageBatch.agentName != null) {
            val messageSyncBatch: MessageSyncBatch = MessageSyncBatch(logMessageBatch, MessageSyncBatch.OnMessageBatchSyncedListener(this, (Messenger)object, logMessageBatch){
                final DriveSyncService this$0
                final LogMessageBatch val$message
                final Messenger val$replyTo
                {
                    this.this$0 = driveSyncService
                    this.val$replyTo = messenger
                    this.val$message = logMessageBatch
                }

                override Unit onMessageBatchSynced(MessageSyncBatch messageSyncBatch) {
                    CloudSyncMessenger.sendMessage(this.val$replyTo, MessageType.LogMessagesCompleted, LogMessagesCompleted(this.val$message.agentUUID, this.val$message.lastMessageID), null)
                }
            for (LogChatMessage logChatMessage : logMessageBatch.messages) {
                if (logChatMessage == null || logChatMessage.chatterName == null || logChatMessage.messageText == null) continue
                this.synchronizer.logString(this.agentSyncConnections, logMessageBatch.agentUUID, logMessageBatch.agentName, this.logFileNameForChatter(logChatMessage.chatterName), DriveLogEntry(logChatMessage.messageText, messageSyncBatch, logChatMessage.messageID))
            }
        }
    }

    /*
     * WARNING - Unit declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
     private fun onLogSyncStart(bundleable: LogSyncStart, messenger: Messenger) {
        try {
            Unit var2_4
            if (bundleable.appVersionCode < 58) {
                val packageInfo: PackageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
                val messageType: MessageType = MessageType.LogSyncStatus
                val logSyncStatus: LogSyncStatus = LogSyncStatus(packageInfo.versionCode, LogSyncStatus.Status.AppVersionRejected, null)
                CloudSyncMessenger.sendMessage((Messenger)var2_4, messageType, logSyncStatus, null)
                return
            }
            this.syncRequestSources.add((Messenger)var2_4)
            this.agentSyncConnections.addSyncConnection(bundleable.agentUUID, (Messenger)var2_4)
            this.updateGoogleApiConnection()
            this.processSyncReady()
            return
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException)
            return
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
     private fun processSyncReady() {
        try {
            val packageInfo: PackageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
            if (this.synchronizer == null) return
            for (Messenger messenger : this.syncRequestSources) {
                val messageType: MessageType = MessageType.LogSyncStatus
                val logSyncStatus: LogSyncStatus = LogSyncStatus(packageInfo.versionCode, LogSyncStatus.Status.Ready, null)
                CloudSyncMessenger.sendMessage(messenger, messageType, logSyncStatus, null)
            }
            this.syncRequestSources.clear()
            return
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Debug.Warning(nameNotFoundException)
        }
    }

    /*
     * Enabled aggressive block sorting
     */
     private fun updateGoogleApiConnection() {
        switch (5.$SwitchMap$com$lumiyaviewer$lumiya$cloud$DriveSyncService$GoogleApiState[this.googleApiState.ordinal()]) {
            case 1: {
                if (this.syncRequestSources.isEmpty()) return
                this.googleApiState = GoogleApiState.Connecting
                Debug.Printf("Starting service.", Object[0])
                this.startService(Intent((Context)this, DriveSyncService.class))
                if (this.mGoogleApiClient != null) return
                this.mGoogleApiClient = GoogleApiClient.Builder((Context)this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this.connectionCallbacks).addOnConnectionFailedListener(this.onConnectionFailedListener).build()
                this.mGoogleApiClient.connect()
            }
            default: {
                return
            }
            case 2: 
            case 3: 
        }
        if (!this.syncRequestSources.isEmpty()) return
        if (this.isServiceBound) return
        if (this.synchronizer != null) {
            if (!this.synchronizer.isLoggingDone()) return
        }
        this.googleApiState = GoogleApiState.Idle
        this.periodicSyncHandler.removeCallbacks(this.periodicSync)
        this.periodicSyncEnabled = false
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.disconnect()
            this.mGoogleApiClient = null
            this.synchronizer = null
        }
        this.stopSelf()
    }

     public fun onBind(intent: Intent): IBinder {
        Debug.Printf("DriveSyncService is bound", Object[0])
        this.isServiceBound = true
        return this.mMessenger.getBinder()
    }

    fun onDestroy() {
        Debug.Printf("Service destroyed", Object[0])
        super.onDestroy()
    }

    override Unit onLoggingDone() {
        this.updateGoogleApiConnection()
    }

    override Unit onLoggingNeeded() {
        if (!this.periodicSyncEnabled) {
            this.periodicSyncEnabled = true
            this.periodicSyncHandler.postDelayed(this.periodicSync, 30000L)
        }
    }

     public fun onStartCommand(object: Intent, n: Int, n2: Int): Int {
        Debug.Printf("Service started.", Object[0])
        if (object.hasExtra("deleteResolvableError")) {
            object = UUID.fromString(object.getStringExtra("deleteResolvableError"))
            this.errorResolutionTracker.clearError((UUID)object, false)
            this.errorResolutionTracker.clearNotification()
        }
        return 2
    }

     public fun onUnbind(intent: Intent): Boolean {
        Debug.Printf("DriveSyncService is unbound", Object[0])
        this.isServiceBound = false
        if (this.synchronizer != null) {
            this.synchronizer.flushOpenFiles(true, System.currentTimeMillis())
        }
        this.updateGoogleApiConnection()
        return super.onUnbind(intent)
    }

    @JvmStatic
private enum GoogleApiState {
        Idle,
        Connecting,
        Connected

    }

    @SuppressLint(value={"HandlerLeak"})
    private class IncomingHandler : Handler() {
        final DriveSyncService this$0

        private IncomingHandler(DriveSyncService driveSyncService) {
            this.this$0 = driveSyncService
        }

        /*
         * Exception decompiling
         */
        fun handleMessage(var1_1: Message) {
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
            throw IllegalStateException("Decompilation failed")
        }
    }
}

