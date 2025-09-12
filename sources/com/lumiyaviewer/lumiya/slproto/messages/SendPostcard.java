// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SendPostcard extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public boolean AllowPublish;
        public UUID AssetID;
        public byte From[];
        public boolean MaturePublish;
        public byte Msg[];
        public byte Name[];
        public LLVector3d PosGlobal;
        public UUID SessionID;
        public byte Subject[];
        public byte To[];

        public AgentData()
        {
        }
    }


    public AgentData AgentData_Field;

    public SendPostcard()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return AgentData_Field.To.length + 73 + 1 + AgentData_Field.From.length + 1 + AgentData_Field.Name.length + 1 + AgentData_Field.Subject.length + 2 + AgentData_Field.Msg.length + 1 + 1 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSendPostcard(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-100);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.AssetID);
        packLLVector3d(bytebuffer, AgentData_Field.PosGlobal);
        packVariable(bytebuffer, AgentData_Field.To, 1);
        packVariable(bytebuffer, AgentData_Field.From, 1);
        packVariable(bytebuffer, AgentData_Field.Name, 1);
        packVariable(bytebuffer, AgentData_Field.Subject, 1);
        packVariable(bytebuffer, AgentData_Field.Msg, 2);
        packBoolean(bytebuffer, AgentData_Field.AllowPublish);
        packBoolean(bytebuffer, AgentData_Field.MaturePublish);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.AssetID = unpackUUID(bytebuffer);
        AgentData_Field.PosGlobal = unpackLLVector3d(bytebuffer);
        AgentData_Field.To = unpackVariable(bytebuffer, 1);
        AgentData_Field.From = unpackVariable(bytebuffer, 1);
        AgentData_Field.Name = unpackVariable(bytebuffer, 1);
        AgentData_Field.Subject = unpackVariable(bytebuffer, 1);
        AgentData_Field.Msg = unpackVariable(bytebuffer, 2);
        AgentData_Field.AllowPublish = unpackBoolean(bytebuffer);
        AgentData_Field.MaturePublish = unpackBoolean(bytebuffer);
    }
}
