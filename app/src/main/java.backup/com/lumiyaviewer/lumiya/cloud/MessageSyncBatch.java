/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.cloud;

import com.lumiyaviewer.lumiya.cloud.common.LogChatMessage;
import com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch;
import java.util.HashSet;
import java.util.Set;

public class MessageSyncBatch {
    private final OnMessageBatchSyncedListener listener;
    private final Set<Long> messageIDs;

    public MessageSyncBatch(LogMessageBatch object, OnMessageBatchSyncedListener object22) {
        this.listener = object22;
        this.messageIDs = new HashSet<Long>();
        for (LogChatMessage logChatMessage : ((LogMessageBatch)object).messages) {
            this.messageIDs.add(logChatMessage.messageID);
        }
    }

    public void onMessageSynced(long l) {
        if (this.messageIDs.remove(l) && this.messageIDs.isEmpty()) {
            this.listener.onMessageBatchSynced(this);
        }
    }

    public static interface OnMessageBatchSyncedListener {
        public void onMessageBatchSynced(MessageSyncBatch var1);
    }
}

