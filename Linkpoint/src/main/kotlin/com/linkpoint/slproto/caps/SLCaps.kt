package com.linkpoint.slproto.caps

import com.linkpoint.Debug
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDArray
import com.linkpoint.slproto.llsd.types.LLSDString
import java.io.IOException
import java.net.URL
import java.util.EnumMap
import java.util.Map

class SLCaps {
    private val Map<SLCapability, String> caps = EnumMap(SLCapability.class)

    @JvmStatic
    class NoSuchCapabilityException : Exception() {
        private const val Long serialVersionUID = 1

        public NoSuchCapabilityException(SLCapability sLCapability) {
            super("No such capability: " + sLCapability.name())
        }
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

    private fun GetCapabilitesOnce(str: String, str2: String) throws LLSDException, IOException {
        try {
            z = URL(str).getHost().equals("login.agni.lindenlab.com")
        } catch (Exception e) {
            Debug.Warning(e)
            z = false
        }
        val repairCapabilityURL: String = repairCapabilityURL(z, str2)
        val lLSDXMLRequest: LLSDXMLRequest = LLSDXMLRequest()
        val lLSDArray: LLSDArray = LLSDArray()
        for (SLCapability name : SLCapability.values()) {
            lLSDArray.add(LLSDString(name.name()))
        }
        val PerformRequest: LLSDNode = lLSDXMLRequest.PerformRequest(repairCapabilityURL, lLSDArray)
        for (SLCapability sLCapability : SLCapability.values()) {
            if (PerformRequest.keyExists(sLCapability.name())) {
                val repairCapabilityURL2: String = repairCapabilityURL(z, PerformRequest.byKey(sLCapability.name()).asString())
                this.caps.put(sLCapability, repairCapabilityURL2)
                Debug.Log("GetCapabilities: " + sLCapability.name() + " = " + repairCapabilityURL2)
            } else {
                Debug.Log("GetCapabilities: " + sLCapability.name() + " not supported")
            }
        }
    }

    @JvmStatic
 private fun repairCapabilityURL(z: Boolean, str: String): String {
        if (!z) {
            return str
        }
        try {
            val host: String = URL(str).getHost()
            if (host.contains(".") || !host.startsWith("sim")) {
                return str
            }
            val replace: String = str.replace(host, host + ".agni.lindenlab.com")
            Debug.Printf("Repaired capability URL to %s", replace)
            return replace
        } catch (Exception e) {
            Debug.Warning(e)
            return str
        }
    }

    @JvmStatic
     fun repairURL(str: String, str2: String): String {
        try {
            return URL(str).getHost().endsWith(".lindenlab.com") ? repairCapabilityURL(true, str2) : str2
        } catch (Exception e) {
            Debug.Warning(e)
            return str2
        }
    }

    fun GetCapabilites(str: String, str2: String) {
        val i: Int = 0
        while (i < 1) {
            try {
                GetCapabilitesOnce(str, str2)
                return
            } catch (Exception e) {
                e.printStackTrace()
                i++
            }
        }
    }

     public fun getCapability(sLCapability: SLCapability): String {
        return this.caps.get(sLCapability)
    }

     public fun getCapabilityOrThrow(sLCapability: SLCapability) throws NoSuchCapabilityException {
        val str: String = this.caps.get(sLCapability)
        if (str != null) {
            return str
        }
        throw NoSuchCapabilityException(sLCapability)
    }
}
