/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.lumiyaviewer.lumiya.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.drive.Drive
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.cloud.AgentSyncConnections
import com.lumiyaviewer.lumiya.cloud.Debug
import com.lumiyaviewer.lumiya.cloud.DriveChatLogFolder
import com.lumiyaviewer.lumiya.cloud.DriveConnectibleFolder
import com.lumiyaviewer.lumiya.cloud.DriveLogEntry
import com.lumiyaviewer.lumiya.cloud.DriveTextFile
import com.lumiyaviewer.lumiya.cloud.LogWriteTracker
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.Map
import java.util.Set
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class DriveSynchronizer {
    private long FLUSH_INTERVAL = 300000L
    private Map<String, DriveChatLogFolder> chatLogFolders = new HashMap<String, DriveChatLogFolder>()
    private DriveConnectibleFolder chatLogsFolder
    @NonNull
    private Context context
    @NonNull
    private GoogleApiClient googleApiClient
    private boolean isSyncing = false
    private LogWriteTracker logWriteTracker
    private DriveConnectibleFolder lumiyaFolder
    private ResultCallback<? super Status> onRequestSyncResult
    private boolean syncCompleted = false
    private Set<OnSyncCompletedListener> waitingForSyncComplete = new HashSet<OnSyncCompletedListener>()

    DriveSynchronizer(@NonNull Context context, @NonNull GoogleApiClient googleApiClient, LogWriteTracker.OnLoggingDoneListener onLoggingDoneListener) {
        this.onRequestSyncResult = new ResultCallback<Status>(this){
            DriveSynchronizer this$0
            {
                this.this$0 = driveSynchronizer
            }

            @Override
            void onResult(@NonNull Status object) {
                Debug.Printf("LumiyaCloud: drive sync done, success: %b.", ((Status)object).isSuccess())
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
        this.logWriteTracker = fun LogWriteTracker(): new
        this.lumiyaFolder = fun DriveConnectibleFolder(): new
        this.chatLogsFolder = fun DriveConnectibleFolder(Logs": "Chat): new
    }

    /* synthetic */ boolean access$002(DriveSynchronizer driveSynchronizer, boolean bl) {
        driveSynchronizer.isSyncing = bl
        return bl
    }

    /* synthetic */ boolean access$102(DriveSynchronizer driveSynchronizer, boolean bl) {
        driveSynchronizer.syncCompleted = bl
        return bl
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Nullable
    private DriveTextFile getChatLogFile(AgentSyncConnections object, UUID uUID, @NonNull String object2, @NonNull String string2, boolean bl) {
        if ((object2 = this.getChatLogFolder((String)object2, bl)) == null) return null
        return ((DriveChatLogFolder)object2).getChatLogFile((AgentSyncConnections)object, uUID, string2, this.logWriteTracker, bl)
    }

    @Nullable
    private DriveChatLogFolder getChatLogFolder(@NonNull String string2, boolean bl) {
        DriveChatLogFolder driveChatLogFolder
        DriveChatLogFolder driveChatLogFolder2 = driveChatLogFolder = this.chatLogFolders.get(string2)
        if (driveChatLogFolder == null) {
            driveChatLogFolder2 = driveChatLogFolder
            if (bl) {
                driveChatLogFolder2 = fun DriveChatLogFolder(): new
                this.chatLogFolders.put(string2, driveChatLogFolder2)
            }
        }
        return driveChatLogFolder2
    }

    void flushFile(AgentSyncConnections object, UUID uUID, @NonNull String string2, @NonNull String string3) {
        if ((object = this.getChatLogFile((AgentSyncConnections)object, uUID, string2, string3, false)) != null) {
            ((DriveTextFile)object).flush()
        }
    }

    void flushOpenFiles(boolean bl, long l) {
        for (DriveTextFile driveTextFile : this.logWriteTracker.getOpenedFiles()) {
            Debug.Printf("FlushOpenFiles: file opened for %d millis", driveTextFile.getOpenedTimeMillis(l))
            if (!bl && driveTextFile.getOpenedTimeMillis(l) < 300000L) continue
            driveTextFile.flush()
        }
    }

    void invalidateSync() {
        this.syncCompleted = false
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    boolean isLoggingDone() {
        if (this.logWriteTracker.hasOpenedFiles()) return false
        if (this.logWriteTracker.hasPendingLogEntries()) return false
        return true
    }

    void logString(AgentSyncConnections object, UUID uUID, @NonNull String string2, @NonNull String string3, @NonNull DriveLogEntry driveLogEntry) {
        if ((object = this.getChatLogFile((AgentSyncConnections)object, uUID, string2, string3, true)) != null) {
            ((DriveTextFile)object).appendString(driveLogEntry)
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    void requestSync(OnSyncCompletedListener onSyncCompletedListener) {
        if (this.syncCompleted) {
            onSyncCompletedListener.onSyncCompleted()
            return
        }
        this.waitingForSyncComplete.add(onSyncCompletedListener)
        if (this.isSyncing) return
        Debug.Printf("LumiyaCloud: requesting drive sync.", Array<Object>(0))
        this.isSyncing = true
        Drive.DriveApi.requestSync(this.googleApiClient).setResultCallback(this.onRequestSyncResult)
    }

    void resumeSyncing() {
        if (this.logWriteTracker.isLoggingSuspended()) {
            this.logWriteTracker.markLoggingSuspended(false)
            Iterator<Map.Entry<String, DriveChatLogFolder>> iterator = this.chatLogFolders.entrySet().iterator()
            while (iterator.hasNext()) {
                iterator.next().getValue().resume()
            }
        }
    }

    void suspendSyncing() {
        if (!this.logWriteTracker.isLoggingSuspended()) {
            this.logWriteTracker.markLoggingSuspended(true)
            Iterator<Map.Entry<String, DriveChatLogFolder>> iterator = this.chatLogFolders.entrySet().iterator()
            while (iterator.hasNext()) {
                iterator.next().getValue().suspend()
            }
        }
    }

    interface OnSyncCompletedListener {
        fun onSyncCompleted(): Unit
    }
}

