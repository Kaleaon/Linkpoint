package com.lumiyaviewer.lumiya.cloud

import android.content.Context
import com.google.android.gms.common.api.GoogleApiClient
import java.util.UUID

class DriveTextFile(
    context: Context,
    synchronizer: DriveSynchronizer,
    agentSyncConnections: AgentSyncConnections,
    uuid: UUID,
    parentResource: DriveConnectibleResource?,
    googleApiClient: GoogleApiClient,
    resourceName: String,
    logWriteTracker: LogWriteTracker
) : DriveConnectibleFile(
    context,
    synchronizer,
    parentResource,
    googleApiClient,
    resourceName,
    "text/plain"
) {
    
    fun resume() {
        // TODO: Implement resume logic
    }

    fun suspend() {
        // TODO: Implement suspend logic
    }

    fun flush() {
        // TODO: Implement flush logic
    }

    fun appendString(driveLogEntry: Any) { // Type of driveLogEntry needs to be checked
        // TODO: Implement append logic
    }
}
