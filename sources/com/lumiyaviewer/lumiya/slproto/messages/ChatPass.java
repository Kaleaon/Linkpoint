// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ChatPass extends SLMessage
{
    public static class ChatData
    {

        public int Channel;
        public UUID ID;
        public byte Message[];
        public byte Name[];
        public UUID OwnerID;
        public LLVector3 Position;
        public float Radius;
        public int SimAccess;
        public int SourceType;
        public int Type;

        public ChatData()
        {
        }
    }


    public ChatData ChatData_Field;

    public ChatPass()
    {
        zeroCoded = true;
        ChatData_Field = new ChatData();
    }

    public int CalcPayloadSize()
    {
        return ChatData_Field.Name.length + 49 + 1 + 1 + 4 + 1 + 2 + ChatData_Field.Message.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleChatPass(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-17);
        packInt(bytebuffer, ChatData_Field.Channel);
        packLLVector3(bytebuffer, ChatData_Field.Position);
        packUUID(bytebuffer, ChatData_Field.ID);
        packUUID(bytebuffer, ChatData_Field.OwnerID);
        packVariable(bytebuffer, ChatData_Field.Name, 1);
        packByte(bytebuffer, (byte)ChatData_Field.SourceType);
        packByte(bytebuffer, (byte)ChatData_Field.Type);
        packFloat(bytebuffer, ChatData_Field.Radius);
        packByte(bytebuffer, (byte)ChatData_Field.SimAccess);
        packVariable(bytebuffer, ChatData_Field.Message, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ChatData_Field.Channel = unpackInt(bytebuffer);
        ChatData_Field.Position = unpackLLVector3(bytebuffer);
        ChatData_Field.ID = unpackUUID(bytebuffer);
        ChatData_Field.OwnerID = unpackUUID(bytebuffer);
        ChatData_Field.Name = unpackVariable(bytebuffer, 1);
        ChatData_Field.SourceType = unpackByte(bytebuffer) & 0xff;
        ChatData_Field.Type = unpackByte(bytebuffer) & 0xff;
        ChatData_Field.Radius = unpackFloat(bytebuffer);
        ChatData_Field.SimAccess = unpackByte(bytebuffer) & 0xff;
        ChatData_Field.Message = unpackVariable(bytebuffer, 2);
    }
}
