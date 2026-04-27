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

public class ChatFromSimulator extends SLMessage
{
    public static class ChatData
    {

        public int Audible;
        public int ChatType;
        public byte FromName[];
        public byte Message[];
        public UUID OwnerID;
        public LLVector3 Position;
        public UUID SourceID;
        public int SourceType;

        public ChatData()
        {
        }
    }


    public ChatData ChatData_Field;

    public ChatFromSimulator()
    {
        zeroCoded = false;
        ChatData_Field = new ChatData();
    }

    public int CalcPayloadSize()
    {
        return ChatData_Field.FromName.length + 1 + 16 + 16 + 1 + 1 + 1 + 12 + 2 + ChatData_Field.Message.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleChatFromSimulator(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-117);
        packVariable(bytebuffer, ChatData_Field.FromName, 1);
        packUUID(bytebuffer, ChatData_Field.SourceID);
        packUUID(bytebuffer, ChatData_Field.OwnerID);
        packByte(bytebuffer, (byte)ChatData_Field.SourceType);
        packByte(bytebuffer, (byte)ChatData_Field.ChatType);
        packByte(bytebuffer, (byte)ChatData_Field.Audible);
        packLLVector3(bytebuffer, ChatData_Field.Position);
        packVariable(bytebuffer, ChatData_Field.Message, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ChatData_Field.FromName = unpackVariable(bytebuffer, 1);
        ChatData_Field.SourceID = unpackUUID(bytebuffer);
        ChatData_Field.OwnerID = unpackUUID(bytebuffer);
        ChatData_Field.SourceType = unpackByte(bytebuffer) & 0xff;
        ChatData_Field.ChatType = unpackByte(bytebuffer) & 0xff;
        ChatData_Field.Audible = unpackByte(bytebuffer) & 0xff;
        ChatData_Field.Position = unpackLLVector3(bytebuffer);
        ChatData_Field.Message = unpackVariable(bytebuffer, 2);
    }
}
