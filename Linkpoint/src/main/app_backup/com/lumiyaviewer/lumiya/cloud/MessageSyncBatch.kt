package com.lumiyaviewer.lumiya.cloud

class MessageSyncBatch(
    val sourceMessage: Any?,
    val onCompleted: (Any?) -> Unit
)
