// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ChatFromViewer extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ChatData
    {

        public int Channel;
        public byte Message[];
        public int Type;

        public ChatData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ChatData ChatData_Field;

    public ChatFromViewer()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ChatData_Field = new ChatData();
    }

    public int CalcPayloadSize()
    {
        return ChatData_Field.Message.length + 2 + 1 + 4 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleChatFromViewer(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)80);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packVariable(bytebuffer, ChatData_Field.Message, 2);
        packByte(bytebuffer, (byte)ChatData_Field.Type);
        packInt(bytebuffer, ChatData_Field.Channel);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ChatData_Field.Message = unpackVariable(bytebuffer, 2);
        ChatData_Field.Type = unpackByte(bytebuffer) & 0xff;
        ChatData_Field.Channel = unpackInt(bytebuffer);
    }
}
