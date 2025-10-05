package com.linkpoint.cloud

import com.linkpoint.cloud.MessageSyncBatch

data class DriveLogEntry(
    val text: String,
    val syncBatch: MessageSyncBatch?,
    val messageID: Long
)