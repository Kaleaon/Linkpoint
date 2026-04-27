/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.lumiyaviewer.lumiya.cloud;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.lumiyaviewer.lumiya.cloud.DriveConnectibleResource;
import com.lumiyaviewer.lumiya.cloud.DriveSynchronizer;
import com.lumiyaviewer.lumiya.cloud.ErrorResolutionTracker;

class DriveConnectibleFolder
extends DriveConnectibleResource {
    private final ResultCallback<? super DriveFolder.DriveFolderResult> folderCreatedCallback = new ResultCallback<DriveFolder.DriveFolderResult>(this){
        final DriveConnectibleFolder this$0;
        {
            this.this$0 = driveConnectibleFolder;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public void onResult(@NonNull DriveFolder.DriveFolderResult object) {
            Status status = object.getStatus();
            if (status.isSuccess()) {
                this.this$0.onResourceCreated(object.getDriveFolder());
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

    DriveConnectibleFolder(@NonNull Context context, @NonNull DriveSynchronizer driveSynchronizer, @Nullable String string2, DriveConnectibleResource driveConnectibleResource, @NonNull GoogleApiClient googleApiClient, @NonNull String string3) {
        super(context, driveSynchronizer, string2, driveConnectibleResource, googleApiClient, string3);
    }

    @Override
    protected void createResource(@NonNull DriveFolder driveFolder) {
        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setTitle(this.resourceName).build();
        driveFolder.createFolder(this.googleApiClient, metadataChangeSet).setResultCallback(this.folderCreatedCallback);
    }

    @Override
    @NonNull
    protected String getMimeType() {
        return "application/vnd.google-apps.folder";
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected boolean isMetadataOk(@NonNull Metadata metadata) {
        if (metadata.isTrashed()) return false;
        if (metadata.isExplicitlyTrashed()) return false;
        if (!metadata.isFolder()) return false;
        return true;
    }
}

