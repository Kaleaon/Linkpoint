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

public class GroupNoticesListReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public int AssetType;
        public byte FromName[];
        public boolean HasAttachment;
        public UUID NoticeID;
        public byte Subject[];
        public int Timestamp;

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList Data_Fields;

    public GroupNoticesListReply()
    {
        Data_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = Data_Fields.iterator();
        Data data;
        int i;
        int j;
        for (i = 37; iterator.hasNext(); i = data.Subject.length + (j + 22 + 2) + 1 + 1 + i)
        {
            data = (Data)iterator.next();
            j = data.FromName.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGroupNoticesListReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)59);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        bytebuffer.put((byte)Data_Fields.size());
        Data data;
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)data.AssetType))
        {
            data = (Data)iterator.next();
            packUUID(bytebuffer, data.NoticeID);
            packInt(bytebuffer, data.Timestamp);
            packVariable(bytebuffer, data.FromName, 2);
            packVariable(bytebuffer, data.Subject, 2);
            packBoolean(bytebuffer, data.HasAttachment);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.NoticeID = unpackUUID(bytebuffer);
            data.Timestamp = unpackInt(bytebuffer);
            data.FromName = unpackVariable(bytebuffer, 2);
            data.Subject = unpackVariable(bytebuffer, 2);
            data.HasAttachment = unpackBoolean(bytebuffer);
            data.AssetType = unpackByte(bytebuffer) & 0xff;
            Data_Fields.add(data);
        }

    }
}
