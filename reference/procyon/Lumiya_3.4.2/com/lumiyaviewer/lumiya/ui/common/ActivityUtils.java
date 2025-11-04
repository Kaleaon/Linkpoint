// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;
import android.content.Intent;

public class ActivityUtils
{
    public static final String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID";
    public static final String FRAGMENT_SELECTION_KEY = "fragmentSelection";
    
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
    
    @Nullable
    public static UUID getActiveAgentID(@Nullable final Bundle bundle) {
        if (bundle != null) {
            final String string = bundle.getString("activeAgentUUID");
            if (string != null) {
                return UUIDPool.getUUID(string);
            }
        }
        return null;
    }
    
    @Nullable
    public static Bundle getFragmentSelection(@Nullable final Bundle bundle) {
        if (bundle != null) {
            return bundle.getBundle("fragmentSelection");
        }
        return null;
    }
    
    @Nullable
    public static UserManager getUserManager(@Nullable final Intent intent) {
        final UUID activeAgentID = getActiveAgentID(intent);
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID);
        }
        return null;
    }
    
    @Nullable
    public static UserManager getUserManager(@Nullable final Bundle bundle) {
        final UUID activeAgentID = getActiveAgentID(bundle);
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID);
        }
        return null;
    }
    
    @Nonnull
    public static Bundle makeFragmentArguments(@Nullable final UUID uuid, @Nullable final Bundle bundle) {
        final Bundle bundle2 = new Bundle();
        if (uuid != null) {
            bundle2.putString("activeAgentUUID", uuid.toString());
        }
        if (bundle != null) {
            bundle2.putBundle("fragmentSelection", bundle);
        }
        return bundle2;
    }
    
    public static void setActiveAgentID(final Intent intent, final UUID uuid) {
        if (uuid != null) {
            intent.putExtra("activeAgentUUID", uuid.toString());
        }
    }
    
    public static void setActiveAgentID(final Bundle bundle, final UUID uuid) {
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString());
        }
    }
    
    public static void setFragmentSelection(@Nullable final Bundle bundle, @Nullable final Bundle bundle2) {
        if (bundle != null) {
            if (bundle2 != null) {
                bundle.putBundle("fragmentSelection", bundle2);
            }
            else {
                bundle.remove("fragmentSelection");
            }
        }
    }
}
