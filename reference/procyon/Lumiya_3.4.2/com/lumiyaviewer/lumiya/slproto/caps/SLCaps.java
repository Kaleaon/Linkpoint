// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.caps;

import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class SLCaps
{
    private final Map<SLCapability, String> caps;
    
    public SLCaps() {
        this.caps = new EnumMap<SLCapability, String>(SLCapability.class);
    }
    
    private void GetCapabilitesOnce(final String spec, String repairCapabilityURL) throws LLSDException, IOException {
        int n;
        boolean b;
        LLSDXMLRequest llsdxmlRequest;
        LLSDArray llsdArray;
        while (true) {
            n = 0;
            while (true) {
                Label_0267: {
                    try {
                        if (!new URL(spec).getHost().equals("login.agni.lindenlab.com")) {
                            break Label_0267;
                        }
                        b = true;
                        repairCapabilityURL = repairCapabilityURL(b, repairCapabilityURL);
                        llsdxmlRequest = new LLSDXMLRequest();
                        llsdArray = new LLSDArray();
                        final SLCapability[] values = SLCapability.values();
                        for (int length = values.length, i = 0; i < length; ++i) {
                            llsdArray.add(new LLSDString(values[i].name()));
                        }
                    }
                    catch (final Exception ex) {
                        Debug.Warning(ex);
                        b = false;
                        continue;
                    }
                    break;
                }
                b = false;
                continue;
            }
        }
        final LLSDNode performRequest = llsdxmlRequest.PerformRequest(repairCapabilityURL, llsdArray);
        final SLCapability[] values2 = SLCapability.values();
        for (int length2 = values2.length, j = n; j < length2; ++j) {
            final SLCapability slCapability = values2[j];
            if (performRequest.keyExists(slCapability.name())) {
                final String repairCapabilityURL2 = repairCapabilityURL(b, performRequest.byKey(slCapability.name()).asString());
                this.caps.put(slCapability, repairCapabilityURL2);
                Debug.Log("GetCapabilities: " + slCapability.name() + " = " + repairCapabilityURL2);
            }
            else {
                Debug.Log("GetCapabilities: " + slCapability.name() + " not supported");
            }
        }
    }
    
    private static String repairCapabilityURL(final boolean b, final String spec) {
        String s = spec;
        if (!b) {
            return s;
        }
        String replace = spec;
        try {
            replace = spec;
            final URL url = new URL(spec);
            replace = spec;
            final String host = url.getHost();
            s = spec;
            replace = spec;
            if (!host.contains(".")) {
                s = spec;
                replace = spec;
                if (host.startsWith("sim")) {
                    replace = spec;
                    replace = spec;
                    final StringBuilder sb = new StringBuilder();
                    replace = spec;
                    s = (replace = spec.replace(host, sb.append(host).append(".agni.lindenlab.com").toString()));
                    Debug.Printf("Repaired capability URL to %s", s);
                }
            }
            return s;
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
            s = replace;
            return s;
        }
    }
    
    public static String repairURL(String repairCapabilityURL, final String s) {
        try {
            final URL url = new URL(repairCapabilityURL);
            repairCapabilityURL = s;
            if (url.getHost().endsWith(".lindenlab.com")) {
                repairCapabilityURL = repairCapabilityURL(true, s);
            }
            return repairCapabilityURL;
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
            repairCapabilityURL = s;
            return repairCapabilityURL;
        }
    }
    
    public void GetCapabilites(final String s, final String s2) {
        int n = 0;
        while (true) {
            if (n >= 1) {
                return;
            }
            try {
                this.GetCapabilitesOnce(s, s2);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                ++n;
            }
        }
    }
    
    public String getCapability(final SLCapability slCapability) {
        return this.caps.get(slCapability);
    }
    
    public String getCapabilityOrThrow(final SLCapability slCapability) throws NoSuchCapabilityException {
        final String s = this.caps.get(slCapability);
        if (s == null) {
            throw new NoSuchCapabilityException(slCapability);
        }
        return s;
    }
    
    public static class NoSuchCapabilityException extends Exception
    {
        private static final long serialVersionUID = 1L;
        
        public NoSuchCapabilityException(final SLCapability slCapability) {
            super("No such capability: " + slCapability.name());
        }
    }
    
    public enum SLCapability
    {
        ChatSessionRequest("ChatSessionRequest", 17), 
        CopyInventoryFromNotecard("CopyInventoryFromNotecard", 7), 
        EventQueueGet("EventQueueGet", 0), 
        FetchInventoryDescendents2("FetchInventoryDescendents2", 3), 
        GetDisplayNames("GetDisplayNames", 4), 
        GetMesh("GetMesh", 9), 
        GetTexture("GetTexture", 1), 
        GroupMemberData("GroupMemberData", 13), 
        HomeLocation("HomeLocation", 14), 
        NewFileAgentInventory("NewFileAgentInventory", 6), 
        ParcelVoiceInfoRequest("ParcelVoiceInfoRequest", 16), 
        ProvisionVoiceAccountRequest("ProvisionVoiceAccountRequest", 15), 
        UpdateAvatarAppearance("UpdateAvatarAppearance", 8), 
        UpdateNotecardAgentInventory("UpdateNotecardAgentInventory", 5), 
        UpdateNotecardTaskInventory("UpdateNotecardTaskInventory", 10), 
        UpdateScriptAgent("UpdateScriptAgent", 12), 
        UpdateScriptTask("UpdateScriptTask", 11), 
        UploadBakedTexture("UploadBakedTexture", 2);
        
        private SLCapability(final String name, final int ordinal) {
        }
    }
}
