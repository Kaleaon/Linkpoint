package com.lumiyaviewer.lumiya.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.Metadata
import com.google.android.gms.drive.MetadataChangeSet

internal open class DriveConnectibleFile(
    context: Context,
    driveSynchronizer: DriveSynchronizer,
    parentResource: DriveConnectibleResource?,
    googleApiClient: GoogleApiClient,
    resourceName: String,
    private val mimeType: String
) : DriveConnectibleResource(
    context,
    driveSynchronizer,
    null,
    parentResource,
    googleApiClient,
    resourceName
) {

    private val fileCreatedCallback = ResultCallback<DriveFolder.DriveFileResult> { result ->
        val status = result.status
        
        when {
            status.isSuccess -> {
                onResourceCreated(result.driveFile)
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
                        }
                    )
                )
            }
            else -> {
                onResourceCreationFailed(status.statusMessage)
            }
        }
    }

    override fun createResource(driveFolder: DriveFolder) {
        val metadataChangeSet = MetadataChangeSet.Builder()
            .setTitle(resourceName)
            .setMimeType(mimeType)
            .build()
        
        driveFolder.createFile(googleApiClient, metadataChangeSet, null)
            .setResultCallback(fileCreatedCallback)
    }

    override fun getMimeType(): String {
        return mimeType
    }

    override fun isMetadataOk(metadata: Metadata): Boolean {
        return !metadata.isTrashed && 
               !metadata.isExplicitlyTrashed && 
               !metadata.isFolder
    }
}