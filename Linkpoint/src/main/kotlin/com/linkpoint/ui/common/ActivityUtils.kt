package com.linkpoint.ui.common

import android.content.Intent
import android.os.Bundle
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ActivityUtils {
    const val EXTRA_ACTIVE_AGENT_UUID: String = "activeAgentUUID"
    const val FRAGMENT_SELECTION_KEY: String = "fragmentSelection"

    @JvmStatic
     fun getActiveAgentID(intent: Intent): UUID {
        String stringExtra
        if (intent == null || (stringExtra = intent.getStringExtra("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(stringExtra)
    }

    @JvmStatic
     fun getActiveAgentID(bundle: Bundle): UUID {
        String string
        if (bundle == null || (string = bundle.getString("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(string)
    }

    @JvmStatic
     fun getFragmentSelection(bundle: Bundle): Bundle {
        if (bundle != null) {
            return bundle.getBundle(FRAGMENT_SELECTION_KEY)
        }
        return null
    }

    @JvmStatic
     fun getUserManager(intent: Intent): UserManager {
        val activeAgentID: UUID = getActiveAgentID(intent)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @JvmStatic
     fun getUserManager(bundle: Bundle): UserManager {
        val activeAgentID: UUID = getActiveAgentID(bundle)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @JvmStatic
     fun makeFragmentArguments(uuid: UUID, bundle: Bundle): Bundle {
        val bundle2: Bundle = Bundle()
        if (uuid != null) {
            bundle2.putString("activeAgentUUID", uuid.toString())
        }
        if (bundle != null) {
            bundle2.putBundle(FRAGMENT_SELECTION_KEY, bundle)
        }
        return bundle2
    }

    @JvmStatic
     fun setActiveAgentID(intent: Intent, uuid: UUID) {
        if (uuid != null) {
            intent.putExtra("activeAgentUUID", uuid.toString())
        }
    }

    @JvmStatic
     fun setActiveAgentID(bundle: Bundle, uuid: UUID) {
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString())
        }
    }

    @JvmStatic
     fun setFragmentSelection(bundle: Bundle, bundle2: Bundle) {
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
