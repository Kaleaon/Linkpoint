package com.linkpoint.cloud

data class DriveLogEntry(
    val text: String,
    val syncBatch: MessageSyncBatch?,
    val messageID: Long,
)