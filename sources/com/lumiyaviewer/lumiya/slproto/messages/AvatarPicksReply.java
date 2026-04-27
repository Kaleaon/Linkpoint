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

public class AvatarPicksReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID TargetID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public UUID PickID;
        public byte PickName[];

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList Data_Fields;

    public AvatarPicksReply()
    {
        Data_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = Data_Fields.iterator();
        int i;
        for (i = 37; iterator.hasNext(); i = ((Data)iterator.next()).PickName.length + 17 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarPicksReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-78);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.TargetID);
        bytebuffer.put((byte)Data_Fields.size());
        Data data;
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, data.PickName, 1))
        {
            data = (Data)iterator.next();
            packUUID(bytebuffer, data.PickID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.TargetID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.PickID = unpackUUID(bytebuffer);
            data.PickName = unpackVariable(bytebuffer, 1);
            Data_Fields.add(data);
        }

    }
}
