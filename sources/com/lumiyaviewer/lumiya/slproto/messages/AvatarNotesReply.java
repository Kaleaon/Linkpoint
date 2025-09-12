// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AvatarNotesReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public byte Notes[];
        public UUID TargetID;

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public AvatarNotesReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.Notes.length + 18 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarNotesReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-80);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, Data_Field.TargetID);
        packVariable(bytebuffer, Data_Field.Notes, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        Data_Field.TargetID = unpackUUID(bytebuffer);
        Data_Field.Notes = unpackVariable(bytebuffer, 2);
    }
}
