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

public class MapItemReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int Flags;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public int Extra;
        public int Extra2;
        public UUID ID;
        public byte Name[];
        public int X;
        public int Y;

        public Data()
        {
        }
    }

    public static class RequestData
    {

        public int ItemType;

        public RequestData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList Data_Fields;
    public RequestData RequestData_Field;

    public MapItemReply()
    {
        Data_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
        RequestData_Field = new RequestData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = Data_Fields.iterator();
        int i;
        for (i = 29; iterator.hasNext(); i = ((Data)iterator.next()).Name.length + 33 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMapItemReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-101);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packInt(bytebuffer, AgentData_Field.Flags);
        packInt(bytebuffer, RequestData_Field.ItemType);
        bytebuffer.put((byte)Data_Fields.size());
        Data data;
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, data.Name, 1))
        {
            data = (Data)iterator.next();
            packInt(bytebuffer, data.X);
            packInt(bytebuffer, data.Y);
            packUUID(bytebuffer, data.ID);
            packInt(bytebuffer, data.Extra);
            packInt(bytebuffer, data.Extra2);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.Flags = unpackInt(bytebuffer);
        RequestData_Field.ItemType = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.X = unpackInt(bytebuffer);
            data.Y = unpackInt(bytebuffer);
            data.ID = unpackUUID(bytebuffer);
            data.Extra = unpackInt(bytebuffer);
            data.Extra2 = unpackInt(bytebuffer);
            data.Name = unpackVariable(bytebuffer, 1);
            Data_Fields.add(data);
        }

    }
}
