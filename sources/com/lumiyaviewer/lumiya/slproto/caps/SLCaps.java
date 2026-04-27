// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

public class SLCaps
{
    public static class NoSuchCapabilityException extends Exception
    {

        private static final long serialVersionUID = 1L;

        public NoSuchCapabilityException(SLCapability slcapability)
        {
            super((new StringBuilder()).append("No such capability: ").append(slcapability.name()).toString());
        }
    }

    public static final class SLCapability extends Enum
    {

        private static final SLCapability $VALUES[];
        public static final SLCapability ChatSessionRequest;
        public static final SLCapability CopyInventoryFromNotecard;
        public static final SLCapability EventQueueGet;
        public static final SLCapability FetchInventoryDescendents2;
        public static final SLCapability GetDisplayNames;
        public static final SLCapability GetMesh;
        public static final SLCapability GetTexture;
        public static final SLCapability GroupMemberData;
        public static final SLCapability HomeLocation;
        public static final SLCapability NewFileAgentInventory;
        public static final SLCapability ParcelVoiceInfoRequest;
        public static final SLCapability ProvisionVoiceAccountRequest;
        public static final SLCapability UpdateAvatarAppearance;
        public static final SLCapability UpdateNotecardAgentInventory;
        public static final SLCapability UpdateNotecardTaskInventory;
        public static final SLCapability UpdateScriptAgent;
        public static final SLCapability UpdateScriptTask;
        public static final SLCapability UploadBakedTexture;

        public static SLCapability valueOf(String s)
        {
            return (SLCapability)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability, s);
        }

        public static SLCapability[] values()
        {
            return $VALUES;
        }

        static 
        {
            EventQueueGet = new SLCapability("EventQueueGet", 0);
            GetTexture = new SLCapability("GetTexture", 1);
            UploadBakedTexture = new SLCapability("UploadBakedTexture", 2);
            FetchInventoryDescendents2 = new SLCapability("FetchInventoryDescendents2", 3);
            GetDisplayNames = new SLCapability("GetDisplayNames", 4);
            UpdateNotecardAgentInventory = new SLCapability("UpdateNotecardAgentInventory", 5);
            NewFileAgentInventory = new SLCapability("NewFileAgentInventory", 6);
            CopyInventoryFromNotecard = new SLCapability("CopyInventoryFromNotecard", 7);
            UpdateAvatarAppearance = new SLCapability("UpdateAvatarAppearance", 8);
            GetMesh = new SLCapability("GetMesh", 9);
            UpdateNotecardTaskInventory = new SLCapability("UpdateNotecardTaskInventory", 10);
            UpdateScriptTask = new SLCapability("UpdateScriptTask", 11);
            UpdateScriptAgent = new SLCapability("UpdateScriptAgent", 12);
            GroupMemberData = new SLCapability("GroupMemberData", 13);
            HomeLocation = new SLCapability("HomeLocation", 14);
            ProvisionVoiceAccountRequest = new SLCapability("ProvisionVoiceAccountRequest", 15);
            ParcelVoiceInfoRequest = new SLCapability("ParcelVoiceInfoRequest", 16);
            ChatSessionRequest = new SLCapability("ChatSessionRequest", 17);
            $VALUES = (new SLCapability[] {
                EventQueueGet, GetTexture, UploadBakedTexture, FetchInventoryDescendents2, GetDisplayNames, UpdateNotecardAgentInventory, NewFileAgentInventory, CopyInventoryFromNotecard, UpdateAvatarAppearance, GetMesh, 
                UpdateNotecardTaskInventory, UpdateScriptTask, UpdateScriptAgent, GroupMemberData, HomeLocation, ProvisionVoiceAccountRequest, ParcelVoiceInfoRequest, ChatSessionRequest
            });
        }

        private SLCapability(String s, int i)
        {
            super(s, i);
        }
    }


    private final Map caps = new EnumMap(com/lumiyaviewer/lumiya/slproto/caps/SLCaps$SLCapability);

    public SLCaps()
    {
    }

    private void GetCapabilitesOnce(String s, String s1)
        throws LLSDException, IOException
    {
        boolean flag = false;
        boolean flag1 = (new URL(s)).getHost().equals("login.agni.lindenlab.com");
        LLSDArray llsdarray;
        SLCapability aslcapability[];
        int k;
        if (flag1)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        s = repairCapabilityURL(flag1, s1);
        s1 = new LLSDXMLRequest();
        llsdarray = new LLSDArray();
        aslcapability = SLCapability.values();
        k = aslcapability.length;
        for (int i = 0; i < k; i++)
        {
            llsdarray.add(new LLSDString(aslcapability[i].name()));
        }

        break; /* Loop/switch isn't completed */
        s;
        Debug.Warning(s);
        flag1 = false;
        if (true) goto _L2; else goto _L1
_L2:
        break MISSING_BLOCK_LABEL_29;
_L1:
        s = s1.PerformRequest(s, llsdarray);
        s1 = SLCapability.values();
        int l = s1.length;
        int j = ((flag) ? 1 : 0);
        while (j < l) 
        {
            SLCapability slcapability = s1[j];
            if (s.keyExists(slcapability.name()))
            {
                String s2 = repairCapabilityURL(flag1, s.byKey(slcapability.name()).asString());
                caps.put(slcapability, s2);
                Debug.Log((new StringBuilder()).append("GetCapabilities: ").append(slcapability.name()).append(" = ").append(s2).toString());
            } else
            {
                Debug.Log((new StringBuilder()).append("GetCapabilities: ").append(slcapability.name()).append(" not supported").toString());
            }
            j++;
        }
        return;
    }

    private static String repairCapabilityURL(boolean flag, String s)
    {
        String s2 = s;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_93;
        }
        String s1 = s;
        String s3;
        try
        {
            s3 = (new URL(s)).getHost();
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            Debug.Warning(s);
            return s1;
        }
        s2 = s;
        s1 = s;
        if (s3.contains("."))
        {
            break MISSING_BLOCK_LABEL_93;
        }
        s2 = s;
        s1 = s;
        if (!s3.startsWith("sim"))
        {
            break MISSING_BLOCK_LABEL_93;
        }
        s1 = s;
        s2 = s.replace(s3, (new StringBuilder()).append(s3).append(".agni.lindenlab.com").toString());
        s1 = s2;
        Debug.Printf("Repaired capability URL to %s", new Object[] {
            s2
        });
        return s2;
    }

    public static String repairURL(String s, String s1)
    {
        String s2 = s1;
        try
        {
            if ((new URL(s)).getHost().endsWith(".lindenlab.com"))
            {
                s2 = repairCapabilityURL(true, s1);
            }
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            Debug.Warning(s);
            return s1;
        }
        return s2;
    }

    public void GetCapabilites(String s, String s1)
    {
        int i = 0;
_L2:
        if (i >= 1)
        {
            break MISSING_BLOCK_LABEL_15;
        }
        GetCapabilitesOnce(s, s1);
        return;
        Exception exception;
        exception;
        exception.printStackTrace();
        i++;
        if (true) goto _L2; else goto _L1
_L1:
    }

    public String getCapability(SLCapability slcapability)
    {
        return (String)caps.get(slcapability);
    }

    public String getCapabilityOrThrow(SLCapability slcapability)
        throws NoSuchCapabilityException
    {
        String s = (String)caps.get(slcapability);
        if (s == null)
        {
            throw new NoSuchCapabilityException(slcapability);
        } else
        {
            return s;
        }
    }
}
