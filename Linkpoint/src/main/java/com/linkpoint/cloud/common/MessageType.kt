package com.linkpoint.cloud.common

enum class MessageType {
    LogSyncStart,
    LogSyncStatus,
    LogMessageBatch,
    LogMessagesCompleted,
    LogFlushMessages,
    LogMessagesFlushed;

    companion object {
        const val CLOUD_PLUGIN_MESSAGE = 100
        const val CLOUD_PLUGIN_NO_USER_RESOLUTION = 102
        const val CLOUD_PLUGIN_RETRY_CONNECT = 101
    }
}