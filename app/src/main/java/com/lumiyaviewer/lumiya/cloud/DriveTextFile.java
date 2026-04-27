/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.lumiyaviewer.lumiya.cloud;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.ExecutionOptions;
import com.lumiyaviewer.lumiya.cloud.AgentSyncConnections;
import com.lumiyaviewer.lumiya.cloud.Debug;
import com.lumiyaviewer.lumiya.cloud.DriveConnectibleFile;
import com.lumiyaviewer.lumiya.cloud.DriveConnectibleResource;
import com.lumiyaviewer.lumiya.cloud.DriveLogEntry;
import com.lumiyaviewer.lumiya.cloud.DriveSynchronizer;
import com.lumiyaviewer.lumiya.cloud.ErrorResolutionTracker;
import com.lumiyaviewer.lumiya.cloud.LogWriteTracker;
import com.lumiyaviewer.lumiya.cloud.common.Bundleable;
import com.lumiyaviewer.lumiya.cloud.common.LogMessagesFlushed;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class DriveTextFile {
    private static final int MAX_WRITTEN_STRINGS = 10000;
    @Nonnull
    private final AgentSyncConnections agentSyncConnections;
    @Nonnull
    private final UUID agentUUID;
    private final Queue<DriveLogEntry> appendStrings;
    private boolean attemptedInvalidateParents = false;
    private boolean attemptedRecreate = false;
    private boolean attemptedReopen = false;
    private final Charset charset = Charset.forName("UTF-8");
    @Nonnull
    private final DriveConnectibleFile connectibleFile;
    @Nullable
    private DriveContents driveContents;
    @Nullable
    private DriveFile driveFile = null;
    @Nonnull
    private final String fileName;
    @Nullable
    private FileOutputStream fileOutputStream;
    @Nonnull
    private final GoogleApiClient googleApiClient;
    @Nonnull
    private final LogWriteTracker logWriteTracker;
    private boolean needsFlushing = false;
    private ResultCallback<? super Status> onFileFlushed;
    private final ResultCallback<? super DriveApi.DriveContentsResult> onFileOpened;
    private final DriveConnectibleResource.OnResourceReadyListener onFileReadyListener;
    private long openSinceMillis = 0L;
    @Nonnull
    private State state;
    private final Set<Long> writtenIDs;
    private final Queue<DriveLogEntry> writtenStrings;

    DriveTextFile(@Nonnull Context context, @Nonnull DriveSynchronizer driveSynchronizer, @Nonnull AgentSyncConnections agentSyncConnections, @Nonnull UUID uUID, DriveConnectibleResource driveConnectibleResource, @Nonnull GoogleApiClient googleApiClient, @Nonnull String string2, @Nonnull LogWriteTracker logWriteTracker) {
        this.appendStrings = new LinkedList<DriveLogEntry>();
        this.writtenIDs = new HashSet<Long>();
        this.writtenStrings = new LinkedList<DriveLogEntry>();
        this.state = State.Idle;
        this.onFileFlushed = new ResultCallback<Status>(this){
            final DriveTextFile this$0;
            {
                this.this$0 = driveTextFile;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void onResult(@Nonnull Status object) {
                Debug.Printf("Flushed file: '%s', success %b", this.this$0.fileName, ((Status)object).isSuccess());
                if (((Status)object).isSuccess()) {
                    this.this$0.onSuccessfulFlush();
                    DriveTextFile.access$202(this.this$0, State.Idle);
                    this.this$0.logWriteTracker.markFileClosed(this.this$0);
                    if (!this.this$0.writtenIDs.isEmpty()) {
                        object = new LogMessagesFlushed(this.this$0.agentUUID, this.this$0.writtenIDs);
                        this.this$0.writtenIDs.clear();
                        this.this$0.agentSyncConnections.sendMessage(this.this$0.agentUUID, MessageType.LogMessagesFlushed, (Bundleable)object, null);
                    }
                    this.this$0.writtenStrings.clear();
                    this.this$0.handlePendingStrings();
                    return;
                }
                if (((Status)object).hasResolution()) {
                    Debug.Printf("LumiyaCloud: File flush error, has resolution (%s)", ((Status)object).getStatusMessage());
                    ErrorResolutionTracker errorResolutionTracker = ErrorResolutionTracker.getInstance();
                    if (errorResolutionTracker == null) return;
                    errorResolutionTracker.addResolvableError(new ErrorResolutionTracker.ResolvableError(this.this$0.fileName, (Status)object, new ErrorResolutionTracker.RestartableOperation(this, (Status)object){
                        final 1 this$1;
                        final Status val$status;
                        {
                            this.this$1 = var1_1;
                            this.val$status = status;
                        }

                        @Override
                        public void tryRestartingOperation() {
                            this.this$1.this$0.attemptReopen(this.val$status);
                        }
                    }));
                    return;
                }
                Debug.Printf("LumiyaCloud: File flush error, no resolution (%s)", ((Status)object).getStatusMessage());
                this.this$0.attemptReopen((Status)object);
            }
        };
        this.onFileReadyListener = new DriveConnectibleResource.OnResourceReadyListener(this){
            final DriveTextFile this$0;
            {
                this.this$0 = driveTextFile;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void onResourceReady(@Nullable DriveResource driveResource, @Nullable String string2) {
                if (driveResource != null && driveResource instanceof DriveFile) {
                    Debug.Printf("Connected to file: '%s', opening it", this.this$0.fileName);
                    DriveTextFile.access$1002(this.this$0, (DriveFile)driveResource);
                    this.this$0.driveFile.open(this.this$0.googleApiClient, 0x30000000, null).setResultCallback(this.this$0.onFileOpened);
                    return;
                }
                DriveTextFile.access$202(this.this$0, State.Error);
            }
        };
        this.onFileOpened = new ResultCallback<DriveApi.DriveContentsResult>(this){
            final DriveTextFile this$0;
            {
                this.this$0 = driveTextFile;
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void onResult(@Nonnull DriveApi.DriveContentsResult object) {
                Object object2 = object.getStatus();
                Debug.Printf("Opened file '%s', success %b", this.this$0.fileName, ((Status)object2).isSuccess());
                if (((Status)object2).isSuccess()) {
                    this.this$0.logWriteTracker.markFileOpened(this.this$0);
                    DriveTextFile.access$1302(this.this$0, System.currentTimeMillis());
                    DriveTextFile.access$1402(this.this$0, object.getDriveContents());
                    object = this.this$0.driveContents.getParcelFileDescriptor().getFileDescriptor();
                    object2 = new FileInputStream((FileDescriptor)object);
                    try {
                        ((FileInputStream)object2).skip(((FileInputStream)object2).available());
                    }
                    catch (IOException iOException) {
                        Debug.Warning(iOException);
                    }
                    DriveTextFile.access$1502(this.this$0, new FileOutputStream((FileDescriptor)object));
                    DriveTextFile.access$202(this.this$0, State.Idle);
                    this.this$0.flushWrittenStrings();
                    this.this$0.flushAppendStrings();
                    return;
                }
                if (((Status)object2).hasResolution()) {
                    Debug.Printf("Opening file '%s': has resolution, code %d, message %s", this.this$0.fileName, ((Status)object2).getStatusCode(), ((Status)object2).getStatusMessage());
                    object = ErrorResolutionTracker.getInstance();
                    if (object == null) return;
                    ((ErrorResolutionTracker)object).addResolvableError(new ErrorResolutionTracker.ResolvableError(this.this$0.fileName, (Status)object2, new ErrorResolutionTracker.RestartableOperation(this, (Status)object2){
                        final 3 this$1;
                        final Status val$status;
                        {
                            this.this$1 = var1_1;
                            this.val$status = status;
                        }

                        @Override
                        public void tryRestartingOperation() {
                            this.this$1.this$0.attemptReopen(this.val$status);
                        }
                    }));
                    return;
                }
                Debug.Printf("Opening file '%s': no resolution, code %d, message %s", this.this$0.fileName, ((Status)object2).getStatusCode(), ((Status)object2).getStatusMessage());
                this.this$0.attemptReopen((Status)object2);
            }
        };
        this.agentSyncConnections = agentSyncConnections;
        this.agentUUID = uUID;
        this.googleApiClient = googleApiClient;
        this.fileName = string2;
        this.logWriteTracker = logWriteTracker;
        this.connectibleFile = new DriveConnectibleFile(context, driveSynchronizer, driveConnectibleResource, googleApiClient, string2, "text/plain");
    }

    static /* synthetic */ DriveFile access$1002(DriveTextFile driveTextFile, DriveFile driveFile) {
        driveTextFile.driveFile = driveFile;
        return driveFile;
    }

    static /* synthetic */ long access$1302(DriveTextFile driveTextFile, long l) {
        driveTextFile.openSinceMillis = l;
        return l;
    }

    static /* synthetic */ DriveContents access$1402(DriveTextFile driveTextFile, DriveContents driveContents) {
        driveTextFile.driveContents = driveContents;
        return driveContents;
    }

    static /* synthetic */ FileOutputStream access$1502(DriveTextFile driveTextFile, FileOutputStream fileOutputStream) {
        driveTextFile.fileOutputStream = fileOutputStream;
        return fileOutputStream;
    }

    static /* synthetic */ State access$202(DriveTextFile driveTextFile, State state) {
        driveTextFile.state = state;
        return state;
    }

    /*
     * Enabled aggressive block sorting
     */
    private void attemptReopen(Status status) {
        if (!this.attemptedReopen) {
            this.state = State.Working;
            this.attemptedReopen = true;
            Debug.Printf("File '%s': attempting to re-open", this.fileName);
            this.connectibleFile.requestInvalidate(false, false);
            this.connectibleFile.getResource(this.onFileReadyListener);
            return;
        }
        if (!this.attemptedRecreate) {
            this.state = State.Working;
            this.attemptedRecreate = true;
            Debug.Printf("File '%s': attempting to re-create", this.fileName);
            this.connectibleFile.requestInvalidate(true, false);
            this.connectibleFile.getResource(this.onFileReadyListener);
            return;
        }
        if (!this.attemptedInvalidateParents) {
            this.state = State.Working;
            this.attemptedInvalidateParents = true;
            Debug.Printf("File '%s': attempting to invalidate parents", this.fileName);
            this.connectibleFile.requestInvalidate(false, true);
            this.connectibleFile.getResource(this.onFileReadyListener);
            return;
        }
        Debug.Printf("Re-creation failed", new Object[0]);
        this.state = State.Error;
        ErrorResolutionTracker errorResolutionTracker = ErrorResolutionTracker.getInstance();
        if (errorResolutionTracker == null) return;
        errorResolutionTracker.addResolvableError(new ErrorResolutionTracker.ResolvableError(this.fileName, status, null));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void flushAppendStrings() {
        if (this.fileOutputStream == null) return;
        try {
            DriveLogEntry driveLogEntry;
            while ((driveLogEntry = this.appendStrings.poll()) != null) {
                CharSequence charSequence = new StringBuilder();
                charSequence = ((StringBuilder)charSequence).append(driveLogEntry.text).append("\n").toString();
                Debug.Printf("File '%s': written '%s'", this.fileName, driveLogEntry.text);
                this.fileOutputStream.write(((String)charSequence).getBytes(this.charset));
                this.writtenIDs.add(driveLogEntry.messageID);
                this.writtenStrings.add(driveLogEntry);
                this.logWriteTracker.removePendingLogEntry(driveLogEntry);
                if (driveLogEntry.syncBatch == null) continue;
                driveLogEntry.syncBatch.onMessageSynced(driveLogEntry.messageID);
            }
            if (this.writtenStrings.size() >= 10000) {
                this.needsFlushing = true;
            }
            if (!this.needsFlushing) return;
            this.needsFlushing = false;
            this.startClosingFile();
            return;
        }
        catch (IOException iOException) {
            Debug.Warning(iOException);
            this.state = State.Error;
        }
    }

    private void flushWrittenStrings() {
        if (this.fileOutputStream != null) {
            try {
                for (DriveLogEntry driveLogEntry : this.writtenStrings) {
                    CharSequence charSequence = new StringBuilder();
                    charSequence = ((StringBuilder)charSequence).append(driveLogEntry.text).append("\n").toString();
                    Debug.Printf("File '%s': (old) written '%s'", this.fileName, driveLogEntry.text);
                    this.fileOutputStream.write(((String)charSequence).getBytes(this.charset));
                }
            }
            catch (IOException iOException) {
                Debug.Warning(iOException);
                this.state = State.Error;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void handlePendingStrings() {
        if (this.fileOutputStream != null) {
            this.flushAppendStrings();
            return;
        }
        if (this.appendStrings.isEmpty()) {
            if (this.writtenStrings.isEmpty()) return;
        }
        if (this.driveFile != null) {
            Debug.Printf("Opening file: '%s'", this.fileName);
            this.state = State.Working;
            this.driveFile.open(this.googleApiClient, 0x30000000, null).setResultCallback(this.onFileOpened);
            return;
        }
        Debug.Printf("Connecting to file: '%s'", this.fileName);
        this.state = State.Working;
        this.connectibleFile.getResource(this.onFileReadyListener);
    }

    private void onSuccessfulFlush() {
        this.attemptedReopen = false;
        this.attemptedRecreate = false;
        this.attemptedInvalidateParents = false;
    }

    private void startClosingFile() {
        if (this.driveContents != null && this.state != State.Working) {
            Debug.Printf("Flushing file: '%s'", this.fileName);
            this.needsFlushing = false;
            this.state = State.Working;
            this.fileOutputStream = null;
            ExecutionOptions executionOptions = new ExecutionOptions.Builder().setConflictStrategy(0).build();
            this.driveContents.commit(this.googleApiClient, null, executionOptions).setResultCallback(this.onFileFlushed);
            this.driveContents = null;
        }
    }

    void appendString(DriveLogEntry driveLogEntry) {
        if (driveLogEntry != null) {
            this.logWriteTracker.addPendingLogEntry(driveLogEntry);
            this.appendStrings.add(driveLogEntry);
            Debug.Printf("File '%s': requested append '%s'", this.fileName, driveLogEntry.text);
        }
        if (this.state == State.Idle && !this.logWriteTracker.isLoggingSuspended()) {
            this.handlePendingStrings();
        }
    }

    void flush() {
        this.needsFlushing = true;
        if (this.driveContents != null && this.state != State.Working && !this.logWriteTracker.isLoggingSuspended()) {
            this.flushAppendStrings();
        }
    }

    long getOpenedTimeMillis(long l) {
        return l - this.openSinceMillis;
    }

    void resume() {
        this.handlePendingStrings();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void suspend() {
        if (this.fileOutputStream != null) {
            try {
                this.fileOutputStream.close();
            }
            catch (IOException iOException) {
                Debug.Warning(iOException);
            }
        }
        this.fileOutputStream = null;
        this.driveFile = null;
        this.driveContents = null;
        this.state = State.Idle;
    }

    private static enum State {
        Idle,
        Working,
        Error;

    }
}

