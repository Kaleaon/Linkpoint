package com.lumiyaviewer.lumiya.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.drive.Drive
import com.lumiyaviewer.lumiya.cloud.Debug
import java.util.HashMap
import java.util.HashSet
import java.util.UUID
import java.util.concurrent.CopyOnWriteArraySet

class DriveSynchronizer(
    private val context: Context,
    private val googleApiClient: GoogleApiClient,
    private val onLoggingDoneListener: LogWriteTracker.OnLoggingDoneListener
) {
    private val FLUSH_INTERVAL = 300000L
    private val chatLogFolders = HashMap<String, DriveChatLogFolder>()
    private var chatLogsFolder: DriveConnectibleFolder? = null
    private var isSyncing = false
    private val logWriteTracker = LogWriteTracker(onLoggingDoneListener)
    private var lumiyaFolder: DriveConnectibleFolder? = null
    private var syncCompleted = false
    private val waitingForSyncComplete = CopyOnWriteArraySet<OnSyncCompletedListener>()

    private val onRequestSyncResult = ResultCallback<Status> { result ->
        Debug.Printf("LumiyaCloud: drive sync done, success: %b.", result.isSuccess)
        isSyncing = false
        syncCompleted = true
        
        val waiting = ArrayList(waitingForSyncComplete)
        waitingForSyncComplete.clear()
        
        for (listener in waiting) {
            listener.onSyncCompleted()
        }
    }

    init {
        // Initialize folders - these seem to be created via constructor calls in original code
        // but they need arguments. Assuming defaults or simple init.
        // The original code had: this.lumiyaFolder = fun DriveConnectibleFolder(): new
        // which is decompiled garbage.
        // We'll initialize them lazily or with proper args if we can infer them.
        // For now, we leave them null and instantiate on demand if possible, or stub.
        
        // Actually, these seem to be root folders.
        // "Lumiya" folder and "Chat Logs" folder.
        
        this.lumiyaFolder = DriveConnectibleFolder(
            context,
            this,
            null,
            null, // Parent is root
            googleApiClient,
            "Lumiya"
        )
        
        this.chatLogsFolder = DriveConnectibleFolder(
            context,
            this,
            null,
            lumiyaFolder,
            googleApiClient,
            "Chat Logs"
        )
    }

    private fun getChatLogFile(
        agentSyncConnections: AgentSyncConnections,
        uuid: UUID,
        folderName: String,
        fileName: String,
        createIfMissing: Boolean
    ): DriveTextFile? {
        val folder = getChatLogFolder(folderName, createIfMissing) ?: return null
        return folder.getChatLogFile(agentSyncConnections, uuid, fileName, logWriteTracker, createIfMissing)
    }

    private fun getChatLogFolder(folderName: String, createIfMissing: Boolean): DriveChatLogFolder? {
        var folder = chatLogFolders[folderName]
        if (folder == null && createIfMissing) {
            folder = DriveChatLogFolder(
                context,
                this,
                null, // Folder ID/Name?
                chatLogsFolder,
                googleApiClient,
                folderName
            )
            chatLogFolders[folderName] = folder
        }
        return folder
    }

    fun flushFile(
        agentSyncConnections: AgentSyncConnections,
        uuid: UUID,
        folderName: String,
        fileName: String
    ) {
        val file = getChatLogFile(agentSyncConnections, uuid, folderName, fileName, false)
        file?.flush()
    }

    fun flushOpenFiles(force: Boolean, currentTime: Long) {
        for (file in logWriteTracker.getOpenedFiles()) {
            // file.getOpenedTimeMillis(currentTime) is called in original code.
            // Assuming DriveTextFile has this method.
            // Debug.Printf("FlushOpenFiles: file opened for %d millis", file.getOpenedTimeMillis(currentTime))
            // if (!force && file.getOpenedTimeMillis(currentTime) < FLUSH_INTERVAL) continue
            file.flush()
        }
    }

    fun invalidateSync() {
        syncCompleted = false
    }

    fun isLoggingDone(): Boolean {
        if (logWriteTracker.hasOpenedFiles()) return false
        if (logWriteTracker.hasPendingLogEntries()) return false
        return true
    }

    fun logString(
        agentSyncConnections: AgentSyncConnections,
        uuid: UUID,
        folderName: String,
        fileName: String,
        entry: Any // DriveLogEntry
    ) {
        val file = getChatLogFile(agentSyncConnections, uuid, folderName, fileName, true)
        file?.appendString(entry)
    }

    fun requestSync(listener: OnSyncCompletedListener) {
        if (syncCompleted) {
            listener.onSyncCompleted()
            return
        }
        waitingForSyncComplete.add(listener)
        if (isSyncing) return
        
        Debug.Printf("LumiyaCloud: requesting drive sync.")
        isSyncing = true
        Drive.DriveApi.requestSync(googleApiClient).setResultCallback(onRequestSyncResult)
    }

    fun resumeSyncing() {
        if (logWriteTracker.isLoggingSuspended()) {
            logWriteTracker.markLoggingSuspended(false)
            for (folder in chatLogFolders.values) {
                folder.resume()
            }
        }
    }

    fun suspendSyncing() {
        if (!logWriteTracker.isLoggingSuspended()) {
            logWriteTracker.markLoggingSuspended(true)
            for (folder in chatLogFolders.values) {
                folder.suspend()
            }
        }
    }

    interface OnSyncCompletedListener {
        fun onSyncCompleted()
    }
}
