/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.lumiyaviewer.lumiya.cloud;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.lumiyaviewer.lumiya.cloud.DriveConnectibleResource;
import com.lumiyaviewer.lumiya.cloud.DriveSynchronizer;
import com.lumiyaviewer.lumiya.cloud.ErrorResolutionTracker;

class DriveConnectibleFile
extends DriveConnectibleResource {
    private final ResultCallback<? super DriveFolder.DriveFileResult> fileCreatedCallback = new ResultCallback<DriveFolder.DriveFileResult>(this){
        final DriveConnectibleFile this$0;
        {
            this.this$0 = driveConnectibleFile;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public void onResult(@NonNull DriveFolder.DriveFileResult object) {
            Status status = object.getStatus();
            if (status.isSuccess()) {
                this.this$0.onResourceCreated(object.getDriveFile());
                return;
            }
            if (status.hasResolution()) {
                object = ErrorResolutionTracker.getInstance();
                if (object == null) return;
                ((ErrorResolutionTracker)object).addResolvableError(new ErrorResolutionTracker.ResolvableError(this.this$0.resourceName, status, new ErrorResolutionTracker.RestartableOperation(this){
                    final 1 this$1;
                    {
                        this.this$1 = var1_1;
                    }

                    @Override
                    public void tryRestartingOperation() {
                        this.this$1.this$0.startCreatingResource();
                    }
                }));
                return;
            }
            this.this$0.onResourceCreationFailed(status.getStatusMessage());
        }
    };
    @NonNull
    private final String mimeType;

    DriveConnectibleFile(@NonNull Context context, @NonNull DriveSynchronizer driveSynchronizer, DriveConnectibleResource driveConnectibleResource, @NonNull GoogleApiClient googleApiClient, @NonNull String string2, @NonNull String string3) {
        super(context, driveSynchronizer, null, driveConnectibleResource, googleApiClient, string2);
        this.mimeType = string3;
    }

    @Override
    protected void createResource(@NonNull DriveFolder driveFolder) {
        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setTitle(this.resourceName).setMimeType(this.mimeType).build();
        driveFolder.createFile(this.googleApiClient, metadataChangeSet, null).setResultCallback(this.fileCreatedCallback);
    }

    @Override
    @NonNull
    protected String getMimeType() {
        return this.mimeType;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected boolean isMetadataOk(@NonNull Metadata metadata) {
        if (metadata.isTrashed()) return false;
        if (metadata.isExplicitlyTrashed()) return false;
        if (metadata.isFolder()) return false;
        return true;
    }
}

