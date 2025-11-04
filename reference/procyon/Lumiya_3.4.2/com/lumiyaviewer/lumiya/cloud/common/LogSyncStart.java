// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import java.util.UUID;

public class LogSyncStart implements Bundleable
{
    public final UUID agentUUID;
    public final int appVersionCode;
    
    public LogSyncStart(final int appVersionCode, final UUID agentUUID) {
        this.appVersionCode = appVersionCode;
        this.agentUUID = agentUUID;
    }
    
    public LogSyncStart(final Bundle bundle) {
        this.appVersionCode = bundle.getInt("appVersionCode");
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("appVersionCode", this.appVersionCode);
        bundle.putString("agentUUID", this.agentUUID.toString());
        return bundle;
    }
}
