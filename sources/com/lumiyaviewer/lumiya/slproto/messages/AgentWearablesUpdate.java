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

public class AgentWearablesUpdate extends SLMessage
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

        public UUID AssetID;
        public UUID ItemID;
        public int WearableType;

        public WearableData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList WearableData_Fields;

    public AgentWearablesUpdate()
    {
        WearableData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return WearableData_Fields.size() * 33 + 41;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentWearablesUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)126);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.SerialNum);
        bytebuffer.put((byte)WearableData_Fields.size());
        WearableData wearabledata;
        for (Iterator iterator = WearableData_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)wearabledata.WearableType))
        {
            wearabledata = (WearableData)iterator.next();
            packUUID(bytebuffer, wearabledata.ItemID);
            packUUID(bytebuffer, wearabledata.AssetID);
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
            wearabledata.ItemID = unpackUUID(bytebuffer);
            wearabledata.AssetID = unpackUUID(bytebuffer);
            wearabledata.WearableType = unpackByte(bytebuffer) & 0xff;
            WearableData_Fields.add(wearabledata);
        }

    }
}
