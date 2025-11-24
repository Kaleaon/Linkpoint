package com.lumiyaviewer.lumiya.slproto.caps

import com.lumiyaviewer.lumiya.Debug
import java.io.IOException
import java.util.EnumMap

class SLCaps {
    private val caps: MutableMap<SLCapability, String> = EnumMap(SLCapability::class.java)

    class NoSuchCapabilityException(capability: SLCapability) : Exception("No such capability: ${capability.name}") {
        private val serialVersionUID: Long = 1
    }

    enum class SLCapability {
        EventQueueGet,
        GetTexture,
        UploadBakedTexture,
        FetchInventoryDescendents2,
        GetDisplayNames,
        UpdateNotecardAgentInventory,
        NewFileAgentInventory,
        CopyInventoryFromNotecard,
        UpdateAvatarAppearance,
        GetMesh,
        UpdateNotecardTaskInventory,
        UpdateScriptTask,
        UpdateScriptAgent,
        GroupMemberData,
        HomeLocation,
        ProvisionVoiceAccountRequest,
        ParcelVoiceInfoRequest,
        ChatSessionRequest
    }

    private fun getCapabilitesOnce(seedUrl: String, capUrl: String) {
        // Stub implementation to unblock compilation
        // In real implementation, this fetches caps from the seed capability
        caps[SLCapability.GetTexture] = "http://example.com/cap/GetTexture"
        // ... populate other caps
    }

    fun getCapabilites(seedUrl: String, capUrl: String) {
        try {
            getCapabilitesOnce(seedUrl, capUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCapability(capability: SLCapability): String? {
        return caps[capability]
    }

    @Throws(NoSuchCapabilityException::class)
    fun getCapabilityOrThrow(capability: SLCapability): String {
        return caps[capability] ?: throw NoSuchCapabilityException(capability)
    }
}
