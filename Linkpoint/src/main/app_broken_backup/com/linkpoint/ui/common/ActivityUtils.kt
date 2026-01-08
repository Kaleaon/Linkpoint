package com.linkpoint.ui.common

import android.content.Intent
import android.os.Bundle
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ActivityUtils {
    val EXTRA_ACTIVE_AGENT_UUID: String = "activeAgentUUID"
    val FRAGMENT_SELECTION_KEY: String = "fragmentSelection"

    @Nullable
    fun getActiveAgentID(@Nullable Intent intent): UUID {
        String stringExtra
        if (intent == null || (stringExtra = intent.getStringExtra("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(stringExtra)
    }

    @Nullable
    fun getActiveAgentID(@Nullable Bundle bundle): UUID {
        String string
        if (bundle == null || (string = bundle.getString("activeAgentUUID")) == null) {
            return null
        }
        return UUIDPool.getUUID(string)
    }

    @Nullable
    fun getFragmentSelection(@Nullable Bundle bundle): Bundle {
        if (bundle != null) {
            return bundle.getBundle(FRAGMENT_SELECTION_KEY)
        }
        return null
    }

    @Nullable
    fun getUserManager(@Nullable Intent intent): UserManager {
        UUID activeAgentID = getActiveAgentID(intent)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @Nullable
    fun getUserManager(@Nullable Bundle bundle): UserManager {
        UUID activeAgentID = getActiveAgentID(bundle)
        if (activeAgentID != null) {
            return UserManager.getUserManager(activeAgentID)
        }
        return null
    }

    @NonNull
    fun makeFragmentArguments(@Nullable UUID uuid, @Nullable Bundle bundle): Bundle {
        Bundle bundle2 = Bundle()
        if (uuid != null) {
            bundle2.putString("activeAgentUUID", uuid.toString())
        }
        if (bundle != null) {
            bundle2.putBundle(FRAGMENT_SELECTION_KEY, bundle)
        }
        return bundle2
    }

    fun setActiveAgentID(Intent intent, UUID uuid): Unit {
        if (uuid != null) {
            intent.putExtra("activeAgentUUID", uuid.toString())
        }
    }

    fun setActiveAgentID(Bundle bundle, UUID uuid): Unit {
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString())
        }
    }

    fun setFragmentSelection(@Nullable Bundle bundle, @Nullable Bundle bundle2): Unit {
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
