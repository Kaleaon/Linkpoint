// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

public enum MessageType
{
    public static final int CLOUD_PLUGIN_MESSAGE = 100;
    public static final int CLOUD_PLUGIN_NO_USER_RESOLUTION = 102;
    public static final int CLOUD_PLUGIN_RETRY_CONNECT = 101;
    
    LogFlushMessages, 
    LogMessageBatch, 
    LogMessagesCompleted, 
    LogMessagesFlushed, 
    LogSyncStart, 
    LogSyncStatus;
}
