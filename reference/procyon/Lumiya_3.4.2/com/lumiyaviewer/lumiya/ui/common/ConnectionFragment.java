// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ConnectionFragment extends Fragment
{
    public static final String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID";
    
    @Nullable
    public static UUID getActiveAgentID(@Nullable final Intent intent) {
        if (intent != null) {
            final String stringExtra = intent.getStringExtra("activeAgentUUID");
            if (stringExtra != null) {
                return UUIDPool.getUUID(stringExtra);
            }
        }
        return null;
    }
}
