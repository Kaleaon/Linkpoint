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

     fun addPendingLogEntry(driveLogEntry: DriveLogEntry) {
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
     fun hasOpenedFiles(): Boolean {
        if (this.openedFiles.isEmpty()) return false
        return true
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
     fun hasPendingLogEntries(): Boolean {
        if (this.pendingLogEntries.isEmpty()) return false
        return true
    }

     fun isLoggingSuspended(): Boolean {
        return this.isLoggingSuspended
    }

     fun markFileClosed(driveTextFile: DriveTextFile) {
        this.openedFiles.remove(driveTextFile)
        if (this.openedFiles.isEmpty() && this.pendingLogEntries.isEmpty() && this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingDone()
        }
    }

     fun markFileOpened(driveTextFile: DriveTextFile) {
        this.openedFiles.add(driveTextFile)
        if (this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingNeeded()
        }
    }

     fun markLoggingSuspended(bl: Boolean) {
        this.isLoggingSuspended = bl
    }

     fun removePendingLogEntry(driveLogEntry: DriveLogEntry) {
        this.pendingLogEntries.remove(driveLogEntry)
        if (this.openedFiles.isEmpty() && this.pendingLogEntries.isEmpty() && this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingDone()
        }
    }

    interface OnLoggingDoneListener {
        fun onLoggingDone()

        fun onLoggingNeeded()
    }
}

