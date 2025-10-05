package com.linkpoint.cloud

import com.google.common.collect.ImmutableList
import com.linkpoint.cloud.DriveLogEntry
import com.linkpoint.cloud.DriveTextFile
import java.util.HashSet
import java.util.Set

class LogWriteTracker {
    private Boolean isLoggingSuspended
    private val OnLoggingDoneListener onLoggingDone
    private val Set<DriveTextFile> openedFiles
    private val Set<DriveLogEntry> pendingLogEntries = HashSet<DriveLogEntry>()

    LogWriteTracker(OnLoggingDoneListener onLoggingDoneListener) {
        this.openedFiles = HashSet<DriveTextFile>()
        this.onLoggingDone = onLoggingDoneListener
        this.isLoggingSuspended = false
    }

    Unit addPendingLogEntry(DriveLogEntry driveLogEntry) {
        this.pendingLogEntries.add(driveLogEntry)
        if (this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingNeeded()
        }
    }

    ImmutableList<DriveTextFile> getOpenedFiles() {
        return ImmutableList.copyOf(this.openedFiles)
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Boolean hasOpenedFiles() {
        if (this.openedFiles.isEmpty()) return false
        return true
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    Boolean hasPendingLogEntries() {
        if (this.pendingLogEntries.isEmpty()) return false
        return true
    }

    Boolean isLoggingSuspended() {
        return this.isLoggingSuspended
    }

    Unit markFileClosed(DriveTextFile driveTextFile) {
        this.openedFiles.remove(driveTextFile)
        if (this.openedFiles.isEmpty() && this.pendingLogEntries.isEmpty() && this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingDone()
        }
    }

    Unit markFileOpened(DriveTextFile driveTextFile) {
        this.openedFiles.add(driveTextFile)
        if (this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingNeeded()
        }
    }

    Unit markLoggingSuspended(Boolean bl) {
        this.isLoggingSuspended = bl
    }

    Unit removePendingLogEntry(DriveLogEntry driveLogEntry) {
        this.pendingLogEntries.remove(driveLogEntry)
        if (this.openedFiles.isEmpty() && this.pendingLogEntries.isEmpty() && this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingDone()
        }
    }

    static interface OnLoggingDoneListener {
        fun onLoggingDone()

        fun onLoggingNeeded()
    }
}

