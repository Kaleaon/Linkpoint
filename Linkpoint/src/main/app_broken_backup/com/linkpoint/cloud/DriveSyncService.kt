package com.linkpoint.cloud

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive
import com.google.common.base.Strings
import com.linkpoint.cloud.common.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for syncing chat logs to Google Drive
 * Manages Google Drive API connection and file synchronization
 */
class DriveSyncService : Service(), LogWriteTracker.OnLoggingDoneListener {
    
    private val PERIODIC_SYNC_INTERVAL = 30000L
    private val REQUIRED_APP_VERSION = 58
    
    private val agentSyncConnections = AgentSyncConnections()
    private val errorResolutionTracker = ErrorResolutionTracker(this)
    private val periodicSyncHandler = Handler(Looper.getMainLooper())
    private val mMessenger = Messenger(IncomingHandler(this))
    private val syncRequestSources = ConcurrentHashMap.newKeySet<Messenger>()
    
    private var googleApiState = GoogleApiState.Idle
    private var isServiceBound = false
    private var mGoogleApiClient: GoogleApiClient? = null
    private var synchronizer: DriveSynchronizer? = null
    private var periodicSyncEnabled = false
    
    /**
     * Google API connection states
     */
    private enum class GoogleApiState {
        Idle,
        Connecting,
        Connected
    }

    /**
     * Connection callbacks for Google API
     */
    private val connectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(bundle: Bundle?) {
            Debug.Printf("LumiyaCloud: connected.")
            
            if (synchronizer == null) {
                googleApiState = GoogleApiState.Connected
                synchronizer = DriveSynchronizer(
                    this@DriveSyncService,
                    mGoogleApiClient!!,
                    this@DriveSyncService
                )
            } else {
                Debug.Printf("LumiyaCloud: resuming sync.")
                synchronizer?.resumeSyncing()
            }
            
            periodicSyncHandler.removeCallbacks(periodicSync)
            periodicSyncHandler.postDelayed(periodicSync, PERIODIC_SYNC_INTERVAL)
            periodicSyncEnabled = true
            processSyncReady()
        }

        override fun onConnectionSuspended(cause: Int) {
            Debug.Printf("LumiyaCloud: connection suspended ($cause)")
            synchronizer?.suspendSyncing()
            periodicSyncHandler.removeCallbacks(periodicSync)
            periodicSyncEnabled = false
        }
    }
    
    /**
     * Periodic sync runnable
     */
    private val periodicSync = Runnable {
        synchronizer?.let { sync ->
            Debug.Printf("FlushFiles: checking for files to flush")
            val currentTime = System.currentTimeMillis()
            sync.flushOpenFiles(forceFlush = false, currentTime)
            periodicSyncHandler.removeCallbacks(periodicSync)
            periodicSyncEnabled = false
            
            if (!sync.isLoggingDone()) {
                periodicSyncHandler.postDelayed(periodicSync, PERIODIC_SYNC_INTERVAL)
                periodicSyncEnabled = true
            }
        }
    }
    
    /**
     * Connection failed listener
     */
    private val onConnectionFailedListener = GoogleApiClient.OnConnectionFailedListener { result ->
        Debug.Printf(
            "LumiyaCloud: connection failed, has resolution: %b",
            result.hasResolution()
        )
        
        if (result.hasResolution()) {
            ConnectionResolutionActivity.startForConnectionResolution(this, result)
        } else {
            Debug.Printf(
                "LumiyaCloud: no resolution at all (%d), error message %s",
                result.errorCode,
                result.errorMessage
            )
            
            val errorMsg = if (Strings.isNullOrEmpty(result.errorMessage)) {
                getString(R.string.cloud_sync_error_format, result.errorCode)
            } else {
                result.errorMessage
            }
            
            notifyError(errorMsg)
        }
    }

    /**
     * Get log filename for a chatter
     */
    private fun logFileNameForChatter(chatterName: String): String {
        return chatterName.replace(Regex("[/.:*\\\\]"), "_").trim() + ".txt"
    }

    /**
     * Notify error to sync requestors
     */
    private fun notifyError(errorMessage: String?) {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            
            syncRequestSources.forEach { messenger ->
                val status = LogSyncStatus(
                    packageInfo.versionCode,
                    LogSyncStatus.Status.GoogleDriveError,
                    errorMessage
                )
                CloudSyncMessenger.sendMessage(messenger, MessageType.LogSyncStatus, status, null)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Debug.Warning(e)
        } finally {
            syncRequestSources.clear()
        }
    }

    /**
     * Handle flush messages request
     */
    private fun onFlushMessages(message: LogFlushMessages) {
        val sync = synchronizer ?: return
        
        if (message.agentName != null && message.chatterName != null) {
            sync.flushFile(
                agentSyncConnections,
                message.agentUUID,
                message.agentName!!,
                logFileNameForChatter(message.chatterName!!)
            )
        } else {
            sync.flushOpenFiles(forceFlush = true, System.currentTimeMillis())
        }
    }

    /**
     * Handle log message batch
     */
    private fun onLogMessageBatch(message: LogMessageBatch, replyTo: Messenger) {
        val sync = synchronizer ?: return
        if (message.agentName == null) return
        
        val batch = MessageSyncBatch(message) { _ ->
            CloudSyncMessenger.sendMessage(
                replyTo,
                MessageType.LogMessagesCompleted,
                LogMessagesCompleted(message.agentUUID, message.lastMessageID),
                null
            )
        }
        
        for (chatMessage in message.messages) {
            if (chatMessage?.chatterName != null && chatMessage.messageText != null) {
                sync.logString(
                    agentSyncConnections,
                    message.agentUUID,
                    message.agentName!!,
                    logFileNameForChatter(chatMessage.chatterName!!),
                    DriveLogEntry(chatMessage.messageText!!, batch, chatMessage.messageID)
                )
            }
        }
    }

    /**
     * Handle log sync start request
     */
    private fun onLogSyncStart(message: LogSyncStart, messenger: Messenger) {
        try {
            if (message.appVersionCode < REQUIRED_APP_VERSION) {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val status = LogSyncStatus(
                    packageInfo.versionCode,
                    LogSyncStatus.Status.AppVersionRejected,
                    null
                )
                CloudSyncMessenger.sendMessage(messenger, MessageType.LogSyncStatus, status, null)
                return
            }
            
            syncRequestSources.add(messenger)
            agentSyncConnections.addSyncConnection(message.agentUUID, messenger)
            updateGoogleApiConnection()
            processSyncReady()
        } catch (e: PackageManager.NameNotFoundException) {
            Debug.Warning(e)
        }
    }

    /**
     * Notify all requestors that sync is ready
     */
    private fun processSyncReady() {
        if (synchronizer == null) return
        
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            
            syncRequestSources.forEach { messenger ->
                val status = LogSyncStatus(
                    packageInfo.versionCode,
                    LogSyncStatus.Status.Ready,
                    null
                )
                CloudSyncMessenger.sendMessage(messenger, MessageType.LogSyncStatus, status, null)
            }
            
            syncRequestSources.clear()
        } catch (e: PackageManager.NameNotFoundException) {
            Debug.Warning(e)
        }
    }

    /**
     * Update Google API connection state
     */
    private fun updateGoogleApiConnection() {
        when (googleApiState) {
            GoogleApiState.Idle -> {
                if (syncRequestSources.isNotEmpty()) {
                    googleApiState = GoogleApiState.Connecting
                    Debug.Printf("Starting service.")
                    startService(Intent(this, DriveSyncService::class.java))
                    
                    if (mGoogleApiClient == null) {
                        mGoogleApiClient = GoogleApiClient.Builder(this)
                            .addApi(Drive.API)
                            .addScope(Drive.SCOPE_FILE)
                            .addConnectionCallbacks(connectionCallbacks)
                            .addOnConnectionFailedListener(onConnectionFailedListener)
                            .build()
                        mGoogleApiClient?.connect()
                    }
                }
            }
            
            GoogleApiState.Connecting, GoogleApiState.Connected -> {
                if (syncRequestSources.isEmpty() && !isServiceBound) {
                    if (synchronizer?.isLoggingDone() != false) {
                        googleApiState = GoogleApiState.Idle
                        periodicSyncHandler.removeCallbacks(periodicSync)
                        periodicSyncEnabled = false
                        
                        mGoogleApiClient?.disconnect()
                        mGoogleApiClient = null
                        synchronizer = null
                        
                        stopSelf()
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Debug.Printf("DriveSyncService is bound")
        isServiceBound = true
        return mMessenger.binder
    }

    override fun onDestroy() {
        Debug.Printf("Service destroyed")
        super.onDestroy()
    }

    override fun onLoggingDone() {
        updateGoogleApiConnection()
    }

    override fun onLoggingNeeded() {
        if (!periodicSyncEnabled) {
            periodicSyncEnabled = true
            periodicSyncHandler.postDelayed(periodicSync, PERIODIC_SYNC_INTERVAL)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Debug.Printf("Service started.")
        
        intent?.let {
            if (it.hasExtra("deleteResolvableError")) {
                val errorUUID = UUID.fromString(it.getStringExtra("deleteResolvableError"))
                errorResolutionTracker.clearError(errorUUID, false)
                errorResolutionTracker.clearNotification()
            }
        }
        
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Debug.Printf("DriveSyncService is unbound")
        isServiceBound = false
        synchronizer?.flushOpenFiles(forceFlush = true, System.currentTimeMillis())
        updateGoogleApiConnection()
        return super.onUnbind(intent)
    }

    /**
     * Handler for incoming messages from clients
     */
    @SuppressLint("HandlerLeak")
    private class IncomingHandler(
        service: DriveSyncService
    ) : Handler(Looper.getMainLooper()) {
        
        private val serviceRef = WeakReference(service)

        override fun handleMessage(msg: Message) {
            val service = serviceRef.get() ?: return
            
            try {
                val messageType = MessageType.values()[msg.what]
                val replyTo = msg.replyTo
                
                when (messageType) {
                    MessageType.LogSyncStart -> {
                        val data = CloudSyncMessenger.extractMessage<LogSyncStart>(msg)
                        data?.let { service.onLogSyncStart(it, replyTo) }
                    }
                    
                    MessageType.LogMessageBatch -> {
                        val data = CloudSyncMessenger.extractMessage<LogMessageBatch>(msg)
                        data?.let { service.onLogMessageBatch(it, replyTo) }
                    }
                    
                    MessageType.LogFlushMessages -> {
                        val data = CloudSyncMessenger.extractMessage<LogFlushMessages>(msg)
                        data?.let { service.onFlushMessages(it) }
                    }
                    
                    else -> {
                        Debug.Printf("DriveSyncService: Unknown message type: $messageType")
                    }
                }
            } catch (e: Exception) {
                Debug.Warning(e)
            }
        }
    }
}
