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

public class ActivateGestures extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int Flags;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public UUID AssetID;
        public int GestureFlags;
        public UUID ItemID;

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList Data_Fields;

    public ActivateGestures()
    {
        Data_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return Data_Fields.size() * 36 + 41;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleActivateGestures(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)60);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.Flags);
        bytebuffer.put((byte)Data_Fields.size());
        Data data;
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, data.GestureFlags))
        {
            data = (Data)iterator.next();
            packUUID(bytebuffer, data.ItemID);
            packUUID(bytebuffer, data.AssetID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.Flags = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.ItemID = unpackUUID(bytebuffer);
            data.AssetID = unpackUUID(bytebuffer);
            data.GestureFlags = unpackInt(bytebuffer);
            Data_Fields.add(data);
        }

    }
}
