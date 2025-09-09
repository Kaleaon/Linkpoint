package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import android.os.Parcelable;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;

public class LogMessageBatch implements Bundleable {
    public final String agentName;
    public final UUID agentUUID;
    public final long lastMessageID;
    public final ImmutableList<LogChatMessage> messages;

    public LogMessageBatch(Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.agentName = bundle.getString("agentName");
        this.lastMessageID = bundle.getLong("lastMessageID");
        Parcelable[] parcelableArray = bundle.getParcelableArray("messages");
        ImmutableList.Builder builder = ImmutableList.builder();
        if (parcelableArray != null) {
            for (Parcelable parcelable : parcelableArray) {
                if (parcelable instanceof Bundle) {
                    builder.add((Object) new LogChatMessage((Bundle) parcelable));
                }
            }
        }
        this.messages = builder.build();
    }

    public LogMessageBatch(UUID uuid, String str, List<LogChatMessage> list, long j) {
        this.agentUUID = uuid;
        this.agentName = str;
        this.messages = ImmutableList.copyOf(list);
        this.lastMessageID = j;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putString("agentName", this.agentName);
        bundle.putLong("lastMessageID", this.lastMessageID);
        Bundle[] bundleArr = new Bundle[this.messages.size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.messages.size()) {
                bundle.putParcelableArray("messages", bundleArr);
                return bundle;
            }
            bundleArr[i2] = ((LogChatMessage) this.messages.get(i2)).toBundle();
            i = i2 + 1;
        }
    }
}
