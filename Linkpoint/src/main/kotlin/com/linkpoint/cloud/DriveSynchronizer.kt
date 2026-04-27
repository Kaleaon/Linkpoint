package com.linkpoint.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.drive.Drive
import com.google.common.collect.ImmutableList
import com.linkpoint.cloud.AgentSyncConnections
import com.linkpoint.cloud.Debug
import com.linkpoint.cloud.DriveChatLogFolder
import com.linkpoint.cloud.DriveConnectibleFolder
import com.linkpoint.cloud.DriveLogEntry
import com.linkpoint.cloud.DriveTextFile
import com.linkpoint.cloud.LogWriteTracker
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.Map
import java.util.Set
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class DriveSynchronizer {
    private const val FLUSH_INTERVAL: Long = 300000L
    private val Map<String, DriveChatLogFolder> chatLogFolders = HashMap<String, DriveChatLogFolder>()
    private val DriveConnectibleFolder chatLogsFolder
    private val Context context
    private val GoogleApiClient googleApiClient
    private Boolean isSyncing = false
    private val LogWriteTracker logWriteTracker
    private val DriveConnectibleFolder lumiyaFolder
    private val ResultCallback<? super Status> onRequestSyncResult
    private Boolean syncCompleted = false
    private val Set<OnSyncCompletedListener> waitingForSyncComplete = HashSet<OnSyncCompletedListener>()

    DriveSynchronizer(Context context, GoogleApiClient googleApiClient, LogWriteTracker.OnLoggingDoneListener onLoggingDoneListener) {
        this.onRequestSyncResult = ResultCallback<Status>(this){
            final DriveSynchronizer this$0
            {
                this.this$0 = driveSynchronizer
            }

            override Unit onResult(Status object) {
                Debug.Printf("LinkpointCloud: drive sync done, success: %b.", ((Status)object).isSuccess())
                DriveSynchronizer.access$002(this.this$0, false)
                DriveSynchronizer.access$102(this.this$0, true)
                object = ImmutableList.copyOf(this.this$0.waitingForSyncComplete)
                this.this$0.waitingForSyncComplete.clear()
                object = ((ImmutableList)object).iterator()
                while (object.hasNext()) {
                    ((OnSyncCompletedListener)object.next()).onSyncCompleted()
                }
            }
        }
        this.context = context
        this.googleApiClient = googleApiClient
        this.logWriteTracker = LogWriteTracker(onLoggingDoneListener)
        this.lumiyaFolder = DriveConnectibleFolder(context, this, "LinkpointFolderId", null, googleApiClient, "Linkpoint")
        this.chatLogsFolder = DriveConnectibleFolder(context, this, "ChatLogsFolderId", this.lumiyaFolder, googleApiClient, "Chat Logs")
    }

    // TODO: Review synthetic accessor - static /* synthetic */ Boolean access$002(DriveSynchronizer driveSynchronizer, Boolean bl) {
        driveSynchronizer.isSyncing = bl
        return bl
    }

    // TODO: Review synthetic accessor - static /* synthetic */ Boolean access$102(DriveSynchronizer driveSynchronizer, Boolean bl) {
        driveSynchronizer.syncCompleted = bl
        return bl
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
     private fun getChatLogFile(object: AgentSyncConnections, uUID: UUID, object2: String, string2: String, bl: Boolean): DriveTextFile {
        if ((object2 = this.getChatLogFolder((String)object2, bl)) == null) return null
        return ((DriveChatLogFolder)object2).getChatLogFile((AgentSyncConnections)object, uUID, string2, this.logWriteTracker, bl)
    }

     private fun getChatLogFolder(string2: String, bl: Boolean): DriveChatLogFolder {
        DriveChatLogFolder driveChatLogFolder
        val driveChatLogFolder2: DriveChatLogFolder = driveChatLogFolder = this.chatLogFolders.get(string2)
        if (driveChatLogFolder == null) {
            driveChatLogFolder2 = driveChatLogFolder
            if (bl) {
                driveChatLogFolder2 = DriveChatLogFolder(this.context, this, null, this.chatLogsFolder, this.googleApiClient, string2)
                this.chatLogFolders.put(string2, driveChatLogFolder2)
            }
        }
        return driveChatLogFolder2
    }

     fun flushFile(object: AgentSyncConnections, uUID: UUID, string2: String, string3: String) {
        if ((object = this.getChatLogFile((AgentSyncConnections)object, uUID, string2, string3, false)) != null) {
            ((DriveTextFile)object).flush()
        }
    }

     fun flushOpenFiles(bl: Boolean, l: Long) {
        for (DriveTextFile driveTextFile : this.logWriteTracker.getOpenedFiles()) {
            Debug.Printf("FlushOpenFiles: file opened for %d millis", driveTextFile.getOpenedTimeMillis(l))
            if (!bl && driveTextFile.getOpenedTimeMillis(l) < 300000L) continue
            driveTextFile.flush()
        }
    }

     fun invalidateSync() {
        this.syncCompleted = false
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
     fun isLoggingDone(): Boolean {
        if (this.logWriteTracker.hasOpenedFiles()) return false
        if (this.logWriteTracker.hasPendingLogEntries()) return false
        return true
    }

     fun logString(object: AgentSyncConnections, uUID: UUID, string2: String, string3: String, driveLogEntry: DriveLogEntry) {
        if ((object = this.getChatLogFile((AgentSyncConnections)object, uUID, string2, string3, true)) != null) {
            ((DriveTextFile)object).appendString(driveLogEntry)
        }
    }

    /*
     * Enabled aggressive block sorting
     */
     fun requestSync(onSyncCompletedListener: OnSyncCompletedListener) {
        if (this.syncCompleted) {
            onSyncCompletedListener.onSyncCompleted()
            return
        }
        this.waitingForSyncComplete.add(onSyncCompletedListener)
        if (this.isSyncing) return
        Debug.Printf("LinkpointCloud: requesting drive sync.", Object[0])
        this.isSyncing = true
        Drive.DriveApi.requestSync(this.googleApiClient).setResultCallback(this.onRequestSyncResult)
    }

     fun resumeSyncing() {
        if (this.logWriteTracker.isLoggingSuspended()) {
            this.logWriteTracker.markLoggingSuspended(false)
            Iterator<Map.Entry<String, DriveChatLogFolder>> iterator = this.chatLogFolders.entrySet().iterator()
            while (iterator.hasNext()) {
                iterator.next().getValue().resume()
            }
        }
    }

     fun suspendSyncing() {
        if (!this.logWriteTracker.isLoggingSuspended()) {
            this.logWriteTracker.markLoggingSuspended(true)
            Iterator<Map.Entry<String, DriveChatLogFolder>> iterator = this.chatLogFolders.entrySet().iterator()
            while (iterator.hasNext()) {
                iterator.next().getValue().suspend()
            }
        }
    }

    interface OnSyncCompletedListener {
        fun onSyncCompleted()
    }
}

