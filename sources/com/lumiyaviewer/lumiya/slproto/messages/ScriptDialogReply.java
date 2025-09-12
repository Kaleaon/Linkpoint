// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptDialogReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public int ButtonIndex;
        public byte ButtonLabel[];
        public int ChatChannel;
        public UUID ObjectID;

        public Data()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;

    public ScriptDialogReply()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.ButtonLabel.length + 25 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptDialogReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-65);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, Data_Field.ObjectID);
        packInt(bytebuffer, Data_Field.ChatChannel);
        packInt(bytebuffer, Data_Field.ButtonIndex);
        packVariable(bytebuffer, Data_Field.ButtonLabel, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        Data_Field.ObjectID = unpackUUID(bytebuffer);
        Data_Field.ChatChannel = unpackInt(bytebuffer);
        Data_Field.ButtonIndex = unpackInt(bytebuffer);
        Data_Field.ButtonLabel = unpackVariable(bytebuffer, 1);
    }
}
