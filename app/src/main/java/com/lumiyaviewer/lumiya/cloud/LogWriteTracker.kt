package com.lumiyaviewer.lumiya.cloud

import com.google.common.collect.ImmutableList

internal class LogWriteTracker(
    private val onLoggingDone: OnLoggingDoneListener?
) {
    private var isLoggingSuspended: Boolean = false
    private val openedFiles: MutableSet<DriveTextFile> = HashSet()
    private val pendingLogEntries: MutableSet<DriveLogEntry> = HashSet()

    fun addPendingLogEntry(driveLogEntry: DriveLogEntry) {
        pendingLogEntries.add(driveLogEntry)
        onLoggingDone?.onLoggingNeeded()
    }

    fun getOpenedFiles(): ImmutableList<DriveTextFile> {
        return ImmutableList.copyOf(openedFiles)
    }

    fun hasOpenedFiles(): Boolean {
        return openedFiles.isNotEmpty()
    }

    fun hasPendingLogEntries(): Boolean {
        return pendingLogEntries.isNotEmpty()
    }

    fun isLoggingSuspended(): Boolean {
        return isLoggingSuspended
    }

    fun markFileClosed(driveTextFile: DriveTextFile) {
        openedFiles.remove(driveTextFile)
        if (openedFiles.isEmpty() && pendingLogEntries.isEmpty()) {
            onLoggingDone?.onLoggingDone()
        }
    }

    fun markFileOpened(driveTextFile: DriveTextFile) {
        openedFiles.add(driveTextFile)
        onLoggingDone?.onLoggingNeeded()
    }

    fun markLoggingSuspended(suspended: Boolean) {
        isLoggingSuspended = suspended
    }

    fun removePendingLogEntry(driveLogEntry: DriveLogEntry) {
        pendingLogEntries.remove(driveLogEntry)
        if (openedFiles.isEmpty() && pendingLogEntries.isEmpty()) {
            onLoggingDone?.onLoggingDone()
        }
    }

    interface OnLoggingDoneListener {
        fun onLoggingDone()
        fun onLoggingNeeded()
    }
}