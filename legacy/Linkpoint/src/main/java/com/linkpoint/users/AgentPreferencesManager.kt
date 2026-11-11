package com.linkpoint.users

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.CapabilityRequester
import com.linkpoint.protocol.llsd.LLSDBoolean
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString

class AgentPreferencesManager(
    private val capabilityRequester: CapabilityRequester
) {
    suspend fun fetchPreferences(): LLSDMap? {
        return capabilityRequester.request(CapabilityManager.CAP_AGENT_PREFERENCES) as? LLSDMap
    }

    suspend fun updatePreferences(sendImToEmail: Boolean, visibleInSearch: Boolean): Boolean {
        val request = LLSDMap().apply {
            this["send_im_to_email"] = LLSDBoolean(sendImToEmail)
            this["visible_in_search"] = LLSDBoolean(visibleInSearch)
        }
        return capabilityRequester.request(CapabilityManager.CAP_AGENT_PREFERENCES, request) != null
    }

    suspend fun updateLanguage(languageCode: String): Boolean {
        val request = LLSDMap().apply {
            this["language"] = LLSDString(languageCode)
        }
        return capabilityRequester.request(CapabilityManager.CAP_UPDATE_AGENT_LANGUAGE, request) != null
    }
}
