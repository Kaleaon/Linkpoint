package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import java.util.UUID;

public class LogMessagesCompleted implements Bundleable {
    public final UUID agentUUID;
    public final long lastWrittenMessageID;

    public LogMessagesCompleted(Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.lastWrittenMessageID = bundle.getLong("lastWrittenMessageID");
    }

    public LogMessagesCompleted(UUID uuid, long j) {
        this.agentUUID = uuid;
        this.lastWrittenMessageID = j;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putLong("lastWrittenMessageID", this.lastWrittenMessageID);
        return bundle;
    }
}
