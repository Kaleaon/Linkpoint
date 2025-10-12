package com.lumiyaviewer.lumiya.ui.common

import android.content.Intent
import androidx.fragment.app.Fragment
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID

open class ConnectionFragment : Fragment() {
    
    companion object {
        const val EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID"

        @JvmStatic
        fun getActiveAgentID(intent: Intent?): UUID? {
            val uuidString = intent?.getStringExtra(EXTRA_ACTIVE_AGENT_UUID) ?: return null
            return UUIDPool.getUUID(uuidString)
        }
    }
}