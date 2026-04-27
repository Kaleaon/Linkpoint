package com.lumiyaviewer.lumiya.slproto.caps

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
import java.io.IOException
import java.net.URL
import java.util.EnumMap
import java.util.Map

class SLCaps {
    private Map<SLCapability, String> caps = EnumMap(SLCapability.class)

    class NoSuchCapabilityException : Exception {
        private Long serialVersionUID = 1

        NoSuchCapabilityException(SLCapability sLCapability) {
            super("No such capability: " + sLCapability.name())
        }
    }

    enum SLCapability {
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

    private Unit GetCapabilitesOnce(String str, String str2) throws LLSDException, IOException {
        try {
            z = URL(str).getHost().equals("login.agni.lindenlab.com")
        } catch (Exception e) {
            Debug.Warning(e)
            z = false
        }
        String repairCapabilityURL = repairCapabilityURL(z, str2)
        LLSDXMLRequest lLSDXMLRequest = LLSDXMLRequest()
        LLSDArray lLSDArray = LLSDArray()
        for (SLCapability name : SLCapability.values()) {
            lLSDArray.add(LLSDString(name.name()))
        }
        LLSDNode PerformRequest = lLSDXMLRequest.PerformRequest(repairCapabilityURL, lLSDArray)
        for (SLCapability sLCapability : SLCapability.values()) {
            if (PerformRequest.keyExists(sLCapability.name())) {
                String repairCapabilityURL2 = repairCapabilityURL(z, PerformRequest.byKey(sLCapability.name()).asString())
                this.caps.put(sLCapability, repairCapabilityURL2)
                Debug.Log("GetCapabilities: " + sLCapability.name() + " = " + repairCapabilityURL2)
            } else {
                Debug.Log("GetCapabilities: " + sLCapability.name() + " not supported")
            }
        }
    }

    private String repairCapabilityURL(Boolean z, String str) {
        if (!z) {
            return str
        }
        try {
            String host = URL(str).getHost()
            if (host.contains(".") || !host.startsWith("sim")) {
                return str
            }
            String replace = str.replace(host, host + ".agni.lindenlab.com")
            Debug.Printf("Repaired capability URL to %s", replace)
            return replace
        } catch (Exception e) {
            Debug.Warning(e)
            return str
        }
    }

    String repairURL(String str, String str2) {
        try {
            return URL(str).getHost().endsWith(".lindenlab.com") ? repairCapabilityURL(true, str2) : str2
        } catch (Exception e) {
            Debug.Warning(e)
            return str2
        }
    }

    Unit GetCapabilites(String str, String str2) {
        Int i = 0
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

    String getCapability(SLCapability sLCapability) {
        return this.caps.get(sLCapability)
    }

    String getCapabilityOrThrow(SLCapability sLCapability) throws NoSuchCapabilityException {
        String str = this.caps.get(sLCapability)
        if (str != null) {
            return str
        }
        throw NoSuchCapabilityException(sLCapability)
    }
}
