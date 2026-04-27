package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;

public class LogMessagesFlushed implements Bundleable {
    @Nonnull
    public final UUID agentUUID;
    @Nonnull
    public final ImmutableList<Long> messageIDs;

    public LogMessagesFlushed(Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        long[] longArray = bundle.getLongArray("messageIDs");
        if (longArray == null) {
            longArray = new long[0];
        }
        this.messageIDs = ImmutableList.copyOf(Longs.asList(longArray));
    }

    public LogMessagesFlushed(@Nonnull UUID uuid, Collection<Long> collection) {
        this.agentUUID = uuid;
        this.messageIDs = ImmutableList.copyOf((Collection) collection);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putLongArray("messageIDs", Longs.toArray(this.messageIDs));
        return bundle;
    }
}
