// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import com.google.common.base.Strings;
import android.os.Bundle;
import java.util.UUID;
import javax.annotation.Nullable;

public class LogFlushMessages implements Bundleable
{
    @Nullable
    public final String agentName;
    public final UUID agentUUID;
    @Nullable
    public final String chatterName;
    
    public LogFlushMessages(final Bundle bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.agentName = Strings.nullToEmpty(bundle.getString("agentName"));
        this.chatterName = bundle.getString("chatterName");
    }
    
    public LogFlushMessages(final UUID agentUUID, @Nullable final String agentName, @Nullable final String chatterName) {
        this.agentUUID = agentUUID;
        this.agentName = agentName;
        this.chatterName = chatterName;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putString("agentName", this.agentName);
        bundle.putString("chatterName", this.chatterName);
        return bundle;
    }
}
