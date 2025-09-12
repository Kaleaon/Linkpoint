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

public class TeleportLocal extends SLMessage
{
    public static class Info
    {

        public UUID AgentID;
        public int LocationID;
        public LLVector3 LookAt;
        public LLVector3 Position;
        public int TeleportFlags;

        public Info()
        {
        }
    }


    public Info Info_Field;

    public TeleportLocal()
    {
        zeroCoded = false;
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return 52;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportLocal(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)64);
        packUUID(bytebuffer, Info_Field.AgentID);
        packInt(bytebuffer, Info_Field.LocationID);
        packLLVector3(bytebuffer, Info_Field.Position);
        packLLVector3(bytebuffer, Info_Field.LookAt);
        packInt(bytebuffer, Info_Field.TeleportFlags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.LocationID = unpackInt(bytebuffer);
        Info_Field.Position = unpackLLVector3(bytebuffer);
        Info_Field.LookAt = unpackLLVector3(bytebuffer);
        Info_Field.TeleportFlags = unpackInt(bytebuffer);
    }
}
