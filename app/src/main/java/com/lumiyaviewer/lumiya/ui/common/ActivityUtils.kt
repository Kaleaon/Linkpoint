package com.lumiyaviewer.lumiya.ui.common

import android.content.Intent
import android.os.Bundle
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ActivityUtils {
    String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID"
    String FRAGMENT_SELECTION_KEY = "fragmentSelection"

    @Nullable
    UUID getActiveAgentID(@Nullable Intent intent) {
        String stringExtra
        if (intent == null || (stringExtra = intent.getStringExtra("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(stringExtra)
    }

    @Nullable
    UUID getActiveAgentID(@Nullable Bundle bundle) {
        String string
        if (bundle == null || (string = bundle.getString("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(string)
    }

    @Nullable
    Bundle getFragmentSelection(@Nullable Bundle bundle) {
        if (bundle != null) {
            return bundle.getBundle(FRAGMENT_SELECTION_KEY)
        }
        return null
    }

    @Nullable
    UserManager getUserManager(@Nullable Intent intent) {
        UUID activeAgentID = getActiveAgentID(intent)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @Nullable
    UserManager getUserManager(@Nullable Bundle bundle) {
        UUID activeAgentID = getActiveAgentID(bundle)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @Nonnull
    Bundle makeFragmentArguments(@Nullable UUID uuid, @Nullable Bundle bundle) {
        Bundle bundle2 = Bundle()
        if (uuid != null) {
            bundle2.putString("activeAgentUUID", uuid.toString())
        }
        if (bundle != null) {
            bundle2.putBundle(FRAGMENT_SELECTION_KEY, bundle)
        }
        return bundle2
    }

    Unit setActiveAgentID(Intent intent, UUID uuid) {
        if (uuid != null) {
            intent.putExtra("activeAgentUUID", uuid.toString())
        }
    }

    Unit setActiveAgentID(Bundle bundle, UUID uuid) {
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString())
        }
    }

    Unit setFragmentSelection(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        if (bundle == null) {
            return
        }
        if (bundle2 != null) {
            bundle.putBundle(FRAGMENT_SELECTION_KEY, bundle2)
        } else {
            bundle.remove(FRAGMENT_SELECTION_KEY)
        }
    }
}
