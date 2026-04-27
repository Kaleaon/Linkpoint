// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentSetAppearance extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int SerialNum;
        public UUID SessionID;
        public LLVector3 Size;

        public AgentData()
        {
        }
    }

    public static class ObjectData
    {

        public byte TextureEntry[];

        public ObjectData()
        {
        }
    }

    public static class VisualParam
    {

        public int ParamValue;

        public VisualParam()
        {
        }
    }

    public static class WearableData
    {

        public UUID CacheID;
        public int TextureIndex;

        public WearableData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    public ArrayList VisualParam_Fields;
    public ArrayList WearableData_Fields;

    public AgentSetAppearance()
    {
        WearableData_Fields = new ArrayList();
        VisualParam_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return WearableData_Fields.size() * 17 + 53 + (ObjectData_Field.TextureEntry.length + 2) + 1 + VisualParam_Fields.size() * 1;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentSetAppearance(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)84);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.SerialNum);
        packLLVector3(bytebuffer, AgentData_Field.Size);
        bytebuffer.put((byte)WearableData_Fields.size());
        WearableData wearabledata;
        for (Iterator iterator = WearableData_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)wearabledata.TextureIndex))
        {
            wearabledata = (WearableData)iterator.next();
            packUUID(bytebuffer, wearabledata.CacheID);
        }

        packVariable(bytebuffer, ObjectData_Field.TextureEntry, 2);
        bytebuffer.put((byte)VisualParam_Fields.size());
        for (Iterator iterator1 = VisualParam_Fields.iterator(); iterator1.hasNext(); packByte(bytebuffer, (byte)((VisualParam)iterator1.next()).ParamValue)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.SerialNum = unpackInt(bytebuffer);
        AgentData_Field.Size = unpackLLVector3(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            WearableData wearabledata = new WearableData();
            wearabledata.CacheID = unpackUUID(bytebuffer);
            wearabledata.TextureIndex = unpackByte(bytebuffer) & 0xff;
            WearableData_Fields.add(wearabledata);
        }

        ObjectData_Field.TextureEntry = unpackVariable(bytebuffer, 2);
        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            VisualParam visualparam = new VisualParam();
            visualparam.ParamValue = unpackByte(bytebuffer) & 0xff;
            VisualParam_Fields.add(visualparam);
        }

    }
}
