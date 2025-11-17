package com.lumiyaviewer.lumiya.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.Metadata
import com.google.android.gms.drive.MetadataChangeSet

internal class DriveConnectibleFolder(
    context: Context,
    driveSynchronizer: DriveSynchronizer,
    driveId: String?,
    parentResource: DriveConnectibleResource?,
    googleApiClient: GoogleApiClient,
    resourceName: String,
) : DriveConnectibleResource(
        context,
        driveSynchronizer,
        driveId,
        parentResource,
        googleApiClient,
        resourceName,
    ) {
    private val folderCreatedCallback =
        ResultCallback<DriveFolder.DriveFolderResult> { result ->
            val status = result.status

            when {
                status.isSuccess -> {
                    onResourceCreated(result.driveFolder)
                }
                status.hasResolution() -> {
                    ErrorResolutionTracker.getInstance()?.addResolvableError(
                        ErrorResolutionTracker.ResolvableError(
                            resourceName,
                            status,
                            object : ErrorResolutionTracker.RestartableOperation {
                                override fun tryRestartingOperation() {
                                    startCreatingResource()
                                }
                            },
                        ),
                    )
                }
                else -> {
                    onResourceCreationFailed(status.statusMessage)
                }
            }
        }

    override fun createResource(driveFolder: DriveFolder) {
        val metadataChangeSet =
            MetadataChangeSet.Builder()
                .setTitle(resourceName)
                .build()

        driveFolder.createFolder(googleApiClient, metadataChangeSet)
            .setResultCallback(folderCreatedCallback)
    }

    override fun getMimeType(): String {
        return "application/vnd.google-apps.folder"
    }

    override fun isMetadataOk(metadata: Metadata): Boolean {
        return !metadata.isTrashed &&
            !metadata.isExplicitlyTrashed &&
            metadata.isFolder
    }
}
