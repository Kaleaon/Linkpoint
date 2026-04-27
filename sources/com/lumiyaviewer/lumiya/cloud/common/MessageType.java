// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;


public final class MessageType extends Enum
{

    private static final MessageType $VALUES[];
    public static final int CLOUD_PLUGIN_MESSAGE = 100;
    public static final int CLOUD_PLUGIN_NO_USER_RESOLUTION = 102;
    public static final int CLOUD_PLUGIN_RETRY_CONNECT = 101;
    public static final MessageType LogFlushMessages;
    public static final MessageType LogMessageBatch;
    public static final MessageType LogMessagesCompleted;
    public static final MessageType LogMessagesFlushed;
    public static final MessageType LogSyncStart;
    public static final MessageType LogSyncStatus;

    private MessageType(String s, int i)
    {
        super(s, i);
    }

    public static MessageType valueOf(String s)
    {
        return (MessageType)Enum.valueOf(com/lumiyaviewer/lumiya/cloud/common/MessageType, s);
    }

    public static MessageType[] values()
    {
        return (MessageType[])$VALUES.clone();
    }

    static 
    {
        LogSyncStart = new MessageType("LogSyncStart", 0);
        LogSyncStatus = new MessageType("LogSyncStatus", 1);
        LogMessageBatch = new MessageType("LogMessageBatch", 2);
        LogMessagesCompleted = new MessageType("LogMessagesCompleted", 3);
        LogFlushMessages = new MessageType("LogFlushMessages", 4);
        LogMessagesFlushed = new MessageType("LogMessagesFlushed", 5);
        $VALUES = (new MessageType[] {
            LogSyncStart, LogSyncStatus, LogMessageBatch, LogMessagesCompleted, LogFlushMessages, LogMessagesFlushed
        });
    }
}
