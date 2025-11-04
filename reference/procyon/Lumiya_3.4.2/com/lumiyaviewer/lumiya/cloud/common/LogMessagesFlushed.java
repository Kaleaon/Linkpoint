// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import java.util.Collection;
import com.google.common.primitives.Longs;
import android.os.Bundle;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import java.util.UUID;

public class LogMessagesFlushed implements Bundleable
{
    @Nonnull
    public final UUID agentUUID;
    @Nonnull
    public final ImmutableList<Long> messageIDs;
    
    public LogMessagesFlushed(final Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        long[] longArray = bundle.getLongArray("messageIDs");
        if (longArray == null) {
            longArray = new long[0];
        }
        this.messageIDs = (ImmutableList<Long>)ImmutableList.copyOf((Collection<?>)Longs.asList(longArray));
    }
    
    public LogMessagesFlushed(@Nonnull final UUID agentUUID, final Collection<Long> collection) {
        this.agentUUID = agentUUID;
        this.messageIDs = ImmutableList.copyOf((Collection<? extends Long>)collection);
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putLongArray("messageIDs", Longs.toArray(this.messageIDs));
        return bundle;
    }
}
