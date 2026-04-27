package com.lumiyaviewer.lumiya.slproto.caps;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class SLCaps {
    private final Map<SLCapability, String> caps = new EnumMap(SLCapability.class);

    public static class NoSuchCapabilityException extends Exception {
        private static final long serialVersionUID = 1;

        public NoSuchCapabilityException(SLCapability sLCapability) {
            super("No such capability: " + sLCapability.name());
        }
    }

    public enum SLCapability {
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

    private void GetCapabilitesOnce(String str, String str2) throws LLSDException, IOException {
        boolean z;
        try {
            z = new URL(str).getHost().equals("login.agni.lindenlab.com");
        } catch (Exception e) {
            Debug.Warning(e);
            z = false;
        }
        String repairCapabilityURL = repairCapabilityURL(z, str2);
        LLSDXMLRequest lLSDXMLRequest = new LLSDXMLRequest();
        LLSDArray lLSDArray = new LLSDArray();
        for (SLCapability name : SLCapability.values()) {
            lLSDArray.add(new LLSDString(name.name()));
        }
        LLSDNode PerformRequest = lLSDXMLRequest.PerformRequest(repairCapabilityURL, lLSDArray);
        for (SLCapability sLCapability : SLCapability.values()) {
            if (PerformRequest.keyExists(sLCapability.name())) {
                String repairCapabilityURL2 = repairCapabilityURL(z, PerformRequest.byKey(sLCapability.name()).asString());
                this.caps.put(sLCapability, repairCapabilityURL2);
                Debug.Log("GetCapabilities: " + sLCapability.name() + " = " + repairCapabilityURL2);
            } else {
                Debug.Log("GetCapabilities: " + sLCapability.name() + " not supported");
            }
        }
    }

    private static String repairCapabilityURL(boolean z, String str) {
        if (!z) {
            return str;
        }
        try {
            String host = new URL(str).getHost();
            if (host.contains(".") || !host.startsWith("sim")) {
                return str;
            }
            String replace = str.replace(host, host + ".agni.lindenlab.com");
            Debug.Printf("Repaired capability URL to %s", replace);
            return replace;
        } catch (Exception e) {
            Debug.Warning(e);
            return str;
        }
    }

    public static String repairURL(String str, String str2) {
        try {
            return new URL(str).getHost().endsWith(".lindenlab.com") ? repairCapabilityURL(true, str2) : str2;
        } catch (Exception e) {
            Debug.Warning(e);
            return str2;
        }
    }

    public void GetCapabilites(String str, String str2) {
        int i = 0;
        while (i < 1) {
            try {
                GetCapabilitesOnce(str, str2);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                i++;
            }
        }
    }

    public String getCapability(SLCapability sLCapability) {
        return this.caps.get(sLCapability);
    }

    public String getCapabilityOrThrow(SLCapability sLCapability) throws NoSuchCapabilityException {
        String str = this.caps.get(sLCapability);
        if (str != null) {
            return str;
        }
        throw new NoSuchCapabilityException(sLCapability);
    }
}
