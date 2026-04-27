/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.cloud;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.cloud.DriveLogEntry;
import com.lumiyaviewer.lumiya.cloud.DriveTextFile;
import java.util.HashSet;
import java.util.Set;

class LogWriteTracker {
    private boolean isLoggingSuspended;
    private final OnLoggingDoneListener onLoggingDone;
    private final Set<DriveTextFile> openedFiles;
    private final Set<DriveLogEntry> pendingLogEntries = new HashSet<DriveLogEntry>();

    LogWriteTracker(OnLoggingDoneListener onLoggingDoneListener) {
        this.openedFiles = new HashSet<DriveTextFile>();
        this.onLoggingDone = onLoggingDoneListener;
        this.isLoggingSuspended = false;
    }

    void addPendingLogEntry(DriveLogEntry driveLogEntry) {
        this.pendingLogEntries.add(driveLogEntry);
        if (this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingNeeded();
        }
    }

    ImmutableList<DriveTextFile> getOpenedFiles() {
        return ImmutableList.copyOf(this.openedFiles);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    boolean hasOpenedFiles() {
        if (this.openedFiles.isEmpty()) return false;
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    boolean hasPendingLogEntries() {
        if (this.pendingLogEntries.isEmpty()) return false;
        return true;
    }

    boolean isLoggingSuspended() {
        return this.isLoggingSuspended;
    }

    void markFileClosed(DriveTextFile driveTextFile) {
        this.openedFiles.remove(driveTextFile);
        if (this.openedFiles.isEmpty() && this.pendingLogEntries.isEmpty() && this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingDone();
        }
    }

    void markFileOpened(DriveTextFile driveTextFile) {
        this.openedFiles.add(driveTextFile);
        if (this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingNeeded();
        }
    }

    void markLoggingSuspended(boolean bl) {
        this.isLoggingSuspended = bl;
    }

    void removePendingLogEntry(DriveLogEntry driveLogEntry) {
        this.pendingLogEntries.remove(driveLogEntry);
        if (this.openedFiles.isEmpty() && this.pendingLogEntries.isEmpty() && this.onLoggingDone != null) {
            this.onLoggingDone.onLoggingDone();
        }
    }

    static interface OnLoggingDoneListener {
        public void onLoggingDone();

        public void onLoggingNeeded();
    }
}

