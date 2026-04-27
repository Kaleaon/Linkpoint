package com.linkpoint.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import java.util.HashMap
import java.util.UUID

internal class DriveChatLogFolder(
    private val context: Context,
    private val synchronizer: DriveSynchronizer,
    folderKey: String?,
    driveConnectibleResource: DriveConnectibleResource,
    private val googleApiClient: GoogleApiClient,
    folderName: String
) {
    private val files: MutableMap<String, DriveTextFile> = HashMap()
    private val folder: DriveConnectibleFolder = DriveConnectibleFolder(
        context,
        synchronizer,
        folderKey,
        driveConnectibleResource,
        googleApiClient,
        folderName
    )

    fun getChatLogFile(
        agentSyncConnections: AgentSyncConnections,
        uuid: UUID,
        fileName: String,
        logWriteTracker: LogWriteTracker,
        createIfNeeded: Boolean
    ): DriveTextFile? {
        var driveTextFile = files[fileName]
        
        if (driveTextFile == null && createIfNeeded) {
            driveTextFile = DriveTextFile(
                context,
                synchronizer,
                agentSyncConnections,
                uuid,
                folder,
                googleApiClient,
                fileName,
                logWriteTracker
            )
            files[fileName] = driveTextFile
        }
        
        return driveTextFile
    }

    fun resume() {
        for (file in files.values) {
            file.resume()
        }
    }

    fun suspend() {
        for (file in files.values) {
            file.suspend()
        }
    }
}