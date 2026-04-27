// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TeleportLureRequest extends SLMessage
{
    public static class Info
    {

        public UUID AgentID;
        public UUID LureID;
        public UUID SessionID;
        public int TeleportFlags;

        public Info()
        {
        }
    }


    public Info Info_Field;

    public TeleportLureRequest()
    {
        zeroCoded = false;
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return 56;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportLureRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)71);
        packUUID(bytebuffer, Info_Field.AgentID);
        packUUID(bytebuffer, Info_Field.SessionID);
        packUUID(bytebuffer, Info_Field.LureID);
        packInt(bytebuffer, Info_Field.TeleportFlags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.SessionID = unpackUUID(bytebuffer);
        Info_Field.LureID = unpackUUID(bytebuffer);
        Info_Field.TeleportFlags = unpackInt(bytebuffer);
    }
}
