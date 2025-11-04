// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import java.util.UUID;

public class LogMessagesCompleted implements Bundleable
{
    public final UUID agentUUID;
    public final long lastWrittenMessageID;
    
    public LogMessagesCompleted(final Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.lastWrittenMessageID = bundle.getLong("lastWrittenMessageID");
    }
    
    public LogMessagesCompleted(final UUID agentUUID, final long lastWrittenMessageID) {
        this.agentUUID = agentUUID;
        this.lastWrittenMessageID = lastWrittenMessageID;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putLong("lastWrittenMessageID", this.lastWrittenMessageID);
        return bundle;
    }
}
