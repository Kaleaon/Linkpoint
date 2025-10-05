package com.linkpoint.cloud

import com.linkpoint.cloud.common.LogMessageBatch

class MessageSyncBatch(
    batch: LogMessageBatch,
    private val listener: OnMessageBatchSyncedListener
) {
    private val messageIDs = mutableSetOf<Long>()

    init {
        for (message in batch.messages) {
            messageIDs.add(message.messageID)
        }
    }

    fun onMessageSynced(messageID: Long) {
        if (messageIDs.remove(messageID) && messageIDs.isEmpty()) {
            listener.onMessageBatchSynced(this)
        }
    }

    interface OnMessageBatchSyncedListener {
        fun onMessageBatchSynced(batch: MessageSyncBatch)
    }
}