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

/* loaded from: classes.dex */
public class SLCaps {
    private final Map<SLCapability, String> caps = new EnumMap(SLCapability.class);

    /* loaded from: classes.dex */
    public static class NoSuchCapabilityException extends Exception {
        private static final long serialVersionUID = 1;

        public NoSuchCapabilityException(SLCapability sLCapability) {
            super("No such capability: " + sLCapability.name());
        }
    }

    /* loaded from: classes.dex */
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
        ChatSessionRequest;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static SLCapability[] valuesCustom() {
            return values();
        }
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
        for (SLCapability sLCapability : SLCapability.valuesCustom()) {
            lLSDArray.add(new LLSDString(sLCapability.name()));
        }
        LLSDNode PerformRequest = lLSDXMLRequest.PerformRequest(repairCapabilityURL, lLSDArray);
        for (SLCapability sLCapability2 : SLCapability.valuesCustom()) {
            if (PerformRequest.keyExists(sLCapability2.name())) {
                String repairCapabilityURL2 = repairCapabilityURL(z, PerformRequest.byKey(sLCapability2.name()).asString());
                this.caps.put(sLCapability2, repairCapabilityURL2);
                Debug.Log("GetCapabilities: " + sLCapability2.name() + " = " + repairCapabilityURL2);
            } else {
                Debug.Log("GetCapabilities: " + sLCapability2.name() + " not supported");
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
            str = str.replace(host, host + ".agni.lindenlab.com");
            Debug.Printf("Repaired capability URL to %s", str);
            return str;
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
        for (int i = 0; i < 1; i++) {
            try {
                GetCapabilitesOnce(str, str2);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getCapability(SLCapability sLCapability) {
        return this.caps.get(sLCapability);
    }

    public String getCapabilityOrThrow(SLCapability sLCapability) throws NoSuchCapabilityException {
        String str = this.caps.get(sLCapability);
        if (str == null) {
            throw new NoSuchCapabilityException(sLCapability);
        }
        return str;
    }
}
