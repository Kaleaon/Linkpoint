// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class FormFriendship extends SLMessage
{
    public static class AgentBlock
    {

        public UUID DestID;
        public UUID SourceID;

        public AgentBlock()
        {
        }
    }


    public AgentBlock AgentBlock_Field;

    public FormFriendship()
    {
        zeroCoded = false;
        AgentBlock_Field = new AgentBlock();
    }

    public int CalcPayloadSize()
    {
        return 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleFormFriendship(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)43);
        packUUID(bytebuffer, AgentBlock_Field.SourceID);
        packUUID(bytebuffer, AgentBlock_Field.DestID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentBlock_Field.SourceID = unpackUUID(bytebuffer);
        AgentBlock_Field.DestID = unpackUUID(bytebuffer);
    }
}
