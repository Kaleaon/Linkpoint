// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class Error extends SLMessage
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

        public int Code;
        public byte Data[];
        public UUID ID;
        public byte Message[];
        public byte System[];
        public byte Token[];

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public Error()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.Token.length + 5 + 16 + 1 + Data_Field.System.length + 2 + Data_Field.Message.length + 2 + Data_Field.Data.length + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleError(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-89);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packInt(bytebuffer, Data_Field.Code);
        packVariable(bytebuffer, Data_Field.Token, 1);
        packUUID(bytebuffer, Data_Field.ID);
        packVariable(bytebuffer, Data_Field.System, 1);
        packVariable(bytebuffer, Data_Field.Message, 2);
        packVariable(bytebuffer, Data_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        Data_Field.Code = unpackInt(bytebuffer);
        Data_Field.Token = unpackVariable(bytebuffer, 1);
        Data_Field.ID = unpackUUID(bytebuffer);
        Data_Field.System = unpackVariable(bytebuffer, 1);
        Data_Field.Message = unpackVariable(bytebuffer, 2);
        Data_Field.Data = unpackVariable(bytebuffer, 2);
    }
}
