package com.linkpoint.ui.common

import android.content.Intent
import android.os.Bundle
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ActivityUtils {
    const val String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID"
    const val String FRAGMENT_SELECTION_KEY = "fragmentSelection"

    @JvmStatic
    UUID getActiveAgentID(Intent intent) {
        String stringExtra
        if (intent == null || (stringExtra = intent.getStringExtra("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(stringExtra)
    }

    @JvmStatic
    UUID getActiveAgentID(Bundle bundle) {
        String string
        if (bundle == null || (string = bundle.getString("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(string)
    }

    @JvmStatic
    Bundle getFragmentSelection(Bundle bundle) {
        if (bundle != null) {
            return bundle.getBundle(FRAGMENT_SELECTION_KEY)
        }
        return null
    }

    @JvmStatic
    UserManager getUserManager(Intent intent) {
        UUID activeAgentID = getActiveAgentID(intent)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @JvmStatic
    UserManager getUserManager(Bundle bundle) {
        UUID activeAgentID = getActiveAgentID(bundle)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @JvmStatic
    Bundle makeFragmentArguments(UUID uuid, Bundle bundle) {
        Bundle bundle2 = Bundle()
        if (uuid != null) {
            bundle2.putString("activeAgentUUID", uuid.toString())
        }
        if (bundle != null) {
            bundle2.putBundle(FRAGMENT_SELECTION_KEY, bundle)
        }
        return bundle2
    }

    @JvmStatic
    Unit setActiveAgentID(Intent intent, UUID uuid) {
        if (uuid != null) {
            intent.putExtra("activeAgentUUID", uuid.toString())
        }
    }

    @JvmStatic
    Unit setActiveAgentID(Bundle bundle, UUID uuid) {
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString())
        }
    }

    @JvmStatic
    Unit setFragmentSelection(Bundle bundle, Bundle bundle2) {
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
