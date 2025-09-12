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

public class MapLayerReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int Flags;

        public AgentData()
        {
        }
    }

    public static class LayerData
    {

        public int Bottom;
        public UUID ImageID;
        public int Left;
        public int Right;
        public int Top;

        public LayerData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList LayerData_Fields;

    public MapLayerReply()
    {
        LayerData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return LayerData_Fields.size() * 32 + 25;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMapLayerReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-106);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packInt(bytebuffer, AgentData_Field.Flags);
        bytebuffer.put((byte)LayerData_Fields.size());
        LayerData layerdata;
        for (Iterator iterator = LayerData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, layerdata.ImageID))
        {
            layerdata = (LayerData)iterator.next();
            packInt(bytebuffer, layerdata.Left);
            packInt(bytebuffer, layerdata.Right);
            packInt(bytebuffer, layerdata.Top);
            packInt(bytebuffer, layerdata.Bottom);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.Flags = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            LayerData layerdata = new LayerData();
            layerdata.Left = unpackInt(bytebuffer);
            layerdata.Right = unpackInt(bytebuffer);
            layerdata.Top = unpackInt(bytebuffer);
            layerdata.Bottom = unpackInt(bytebuffer);
            layerdata.ImageID = unpackUUID(bytebuffer);
            LayerData_Fields.add(layerdata);
        }

    }
}
