package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActivityUtils {
    public static final String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID";
    public static final String FRAGMENT_SELECTION_KEY = "fragmentSelection";

    @Nullable
    public static UUID getActiveAgentID(@Nullable Intent intent) {
        String stringExtra;
        if (intent == null || (stringExtra = intent.getStringExtra("activeAgentUUID")) == null) {
            return null;
        }
        return UUIDPool.getUUID(stringExtra);
    }

    @Nullable
    public static UUID getActiveAgentID(@Nullable Bundle bundle) {
        String string;
        if (bundle == null || (string = bundle.getString("activeAgentUUID")) == null) {
            return null;
        }
        return UUIDPool.getUUID(string);
    }

    @Nullable
    public static Bundle getFragmentSelection(@Nullable Bundle bundle) {
        if (bundle != null) {
            return bundle.getBundle(FRAGMENT_SELECTION_KEY);
        }
        return null;
    }

    @Nullable
    public static UserManager getUserManager(@Nullable Intent intent) {
        UUID activeAgentID = getActiveAgentID(intent);
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID);
        }
        return null;
    }

    @Nullable
    public static UserManager getUserManager(@Nullable Bundle bundle) {
        UUID activeAgentID = getActiveAgentID(bundle);
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID);
        }
        return null;
    }

    @Nonnull
    public static Bundle makeFragmentArguments(@Nullable UUID uuid, @Nullable Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (uuid != null) {
            bundle2.putString("activeAgentUUID", uuid.toString());
        }
        if (bundle != null) {
            bundle2.putBundle(FRAGMENT_SELECTION_KEY, bundle);
        }
        return bundle2;
    }

    public static void setActiveAgentID(Intent intent, UUID uuid) {
        if (uuid != null) {
            intent.putExtra("activeAgentUUID", uuid.toString());
        }
    }

    public static void setActiveAgentID(Bundle bundle, UUID uuid) {
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString());
        }
    }

    public static void setFragmentSelection(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        if (bundle == null) {
            return;
        }
        if (bundle2 != null) {
            bundle.putBundle(FRAGMENT_SELECTION_KEY, bundle2);
        } else {
            bundle.remove(FRAGMENT_SELECTION_KEY);
        }
    }
}
