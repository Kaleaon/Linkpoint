package com.lumiyaviewer.lumiya.cloud.common;

public enum MessageType {
    LogSyncStart,
    LogSyncStatus,
    LogMessageBatch,
    LogMessagesCompleted,
    LogFlushMessages,
    LogMessagesFlushed;
    
    public static final int CLOUD_PLUGIN_MESSAGE = 100;
    public static final int CLOUD_PLUGIN_NO_USER_RESOLUTION = 102;
    public static final int CLOUD_PLUGIN_RETRY_CONNECT = 101;
}
