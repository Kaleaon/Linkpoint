// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LogSyncStatus implements Bundleable
{
    @Nullable
    public final String errorMessage;
    public final int pluginVersionCode;
    @Nonnull
    public final Status status;
    
    public LogSyncStatus(final int pluginVersionCode, @Nonnull final Status status, @Nullable final String errorMessage) {
        this.pluginVersionCode = pluginVersionCode;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public LogSyncStatus(final Bundle bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode");
        this.status = Status.valueOf(bundle.getString("status"));
        this.errorMessage = bundle.getString("errorMessage");
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("pluginVersionCode", this.pluginVersionCode);
        bundle.putString("status", this.status.toString());
        bundle.putString("errorMessage", this.errorMessage);
        return bundle;
    }
    
    public enum Status
    {
        AppVersionRejected, 
        GoogleDriveError, 
        Ready;
    }
}
