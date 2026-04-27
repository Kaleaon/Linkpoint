// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentCachedTextureResponse extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int SerialNum;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class WearableData
    {

        public byte HostName[];
        public UUID TextureID;
        public int TextureIndex;

        public WearableData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList WearableData_Fields;

    public AgentCachedTextureResponse()
    {
        WearableData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = WearableData_Fields.iterator();
        int i;
        for (i = 41; iterator.hasNext(); i = ((WearableData)iterator.next()).HostName.length + 18 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentCachedTextureResponse(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-127);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.SerialNum);
        bytebuffer.put((byte)WearableData_Fields.size());
        WearableData wearabledata;
        for (Iterator iterator = WearableData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, wearabledata.HostName, 1))
        {
            wearabledata = (WearableData)iterator.next();
            packUUID(bytebuffer, wearabledata.TextureID);
            packByte(bytebuffer, (byte)wearabledata.TextureIndex);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.SerialNum = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            WearableData wearabledata = new WearableData();
            wearabledata.TextureID = unpackUUID(bytebuffer);
            wearabledata.TextureIndex = unpackByte(bytebuffer) & 0xff;
            wearabledata.HostName = unpackVariable(bytebuffer, 1);
            WearableData_Fields.add(wearabledata);
        }

    }
}
