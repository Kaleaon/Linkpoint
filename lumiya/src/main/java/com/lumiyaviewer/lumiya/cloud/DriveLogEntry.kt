package com.lumiyaviewer.lumiya.cloud

data class DriveLogEntry(
    val text: String,
    val syncBatch: MessageSyncBatch?,
    val messageID: Long,
)