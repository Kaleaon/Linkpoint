package com.lumiyaviewer.lumiya.ui.common

import android.content.Intent
import android.os.Bundle
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID

object ActivityUtils {
    const val EXTRA_ACTIVE_AGENT_UUID: String = "activeAgentUUID"
    const val FRAGMENT_SELECTION_KEY: String = "fragmentSelection"

    fun getActiveAgentID(intent: Intent?): UUID? {
        val stringExtra = intent?.getStringExtra(EXTRA_ACTIVE_AGENT_UUID)
        return if (stringExtra != null) UUIDPool.getUUID(stringExtra) else null
    }

    fun getActiveAgentID(bundle: Bundle?): UUID? {
        val string = bundle?.getString(EXTRA_ACTIVE_AGENT_UUID)
        return if (string != null) UUIDPool.getUUID(string) else null
    }

    fun getFragmentSelection(bundle: Bundle?): Bundle? {
        return bundle?.getBundle(FRAGMENT_SELECTION_KEY)
    }

    fun getUserManager(intent: Intent?): UserManager? {
        val activeAgentID = getActiveAgentID(intent)
        return if (activeAgentID != null) UserManager.getUserManager(activeAgentID) else null
    }

    fun getUserManager(bundle: Bundle?): UserManager? {
        val activeAgentID = getActiveAgentID(bundle)
        return if (activeAgentID != null) UserManager.getUserManager(activeAgentID) else null
    }

    fun makeFragmentArguments(uuid: UUID?, bundle: Bundle?): Bundle {
        val bundle2 = Bundle()
        if (uuid != null) {
            bundle2.putString(EXTRA_ACTIVE_AGENT_UUID, uuid.toString())
        }
        if (bundle != null) {
            bundle2.putBundle(FRAGMENT_SELECTION_KEY, bundle)
        }
        return bundle2
    }

    fun setActiveAgentID(intent: Intent, uuid: UUID?) {
        if (uuid != null) {
            intent.putExtra(EXTRA_ACTIVE_AGENT_UUID, uuid.toString())
        }
    }

    fun setActiveAgentID(bundle: Bundle, uuid: UUID?) {
        if (uuid != null) {
            bundle.putString(EXTRA_ACTIVE_AGENT_UUID, uuid.toString())
        }
    }

    fun setFragmentSelection(bundle: Bundle?, bundle2: Bundle?) {
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
