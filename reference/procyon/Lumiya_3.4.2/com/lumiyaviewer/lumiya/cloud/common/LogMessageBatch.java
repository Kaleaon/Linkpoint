// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import java.util.Collection;
import java.util.List;
import android.os.Parcelable;
import android.os.Bundle;
import com.google.common.collect.ImmutableList;
import java.util.UUID;

public class LogMessageBatch implements Bundleable
{
    public final String agentName;
    public final UUID agentUUID;
    public final long lastMessageID;
    public final ImmutableList<LogChatMessage> messages;
    
    public LogMessageBatch(final Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.agentName = bundle.getString("agentName");
        this.lastMessageID = bundle.getLong("lastMessageID");
        final Parcelable[] parcelableArray = bundle.getParcelableArray("messages");
        final ImmutableList.Builder<LogChatMessage> builder = ImmutableList.builder();
        if (parcelableArray != null) {
            for (final Parcelable parcelable : parcelableArray) {
                if (parcelable instanceof Bundle) {
                    builder.add(new LogChatMessage((Bundle)parcelable));
                }
            }
        }
        this.messages = builder.build();
    }
    
    public LogMessageBatch(final UUID agentUUID, final String agentName, final List<LogChatMessage> list, final long lastMessageID) {
        this.agentUUID = agentUUID;
        this.agentName = agentName;
        this.messages = ImmutableList.copyOf((Collection<? extends LogChatMessage>)list);
        this.lastMessageID = lastMessageID;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putString("agentName", this.agentName);
        bundle.putLong("lastMessageID", this.lastMessageID);
        final Bundle[] array = new Bundle[this.messages.size()];
        for (int i = 0; i < this.messages.size(); ++i) {
            array[i] = ((LogChatMessage)this.messages.get(i)).toBundle();
        }
        bundle.putParcelableArray("messages", (Parcelable[])array);
        return bundle;
    }
}
