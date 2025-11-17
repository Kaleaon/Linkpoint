package com.linkpoint.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import java.util.UUID

internal class DriveChatLogFolder(
    private val context: Context,
    private val synchronizer: DriveSynchronizer,
    folderName: String?,
    parentResource: DriveConnectibleResource?,
    private val googleApiClient: GoogleApiClient,
    folderTitle: String,
) {
    private val files: MutableMap<String, DriveTextFile> = HashMap()
    private val folder: DriveConnectibleFolder =
        DriveConnectibleFolder(
            context,
            synchronizer,
            folderName,
            parentResource,
            googleApiClient,
            folderTitle,
        )

    fun getChatLogFile(
        agentSyncConnections: AgentSyncConnections,
        uuid: UUID,
        fileName: String,
        logWriteTracker: LogWriteTracker,
        createIfMissing: Boolean,
    ): DriveTextFile? {
        var file = files[fileName]

        if (file == null && createIfMissing) {
            file =
                DriveTextFile(
                    context,
                    synchronizer,
                    agentSyncConnections,
                    uuid,
                    folder,
                    googleApiClient,
                    fileName,
                    logWriteTracker,
                )
            files[fileName] = file
        }

        return file
    }

    fun resume() {
        files.values.forEach { it.resume() }
    }

    fun suspend() {
        files.values.forEach { it.suspend() }
    }
}
