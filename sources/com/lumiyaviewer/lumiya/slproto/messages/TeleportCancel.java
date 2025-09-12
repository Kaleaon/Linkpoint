// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TeleportCancel extends SLMessage
{
    public static class Info
    {

        public UUID AgentID;
        public UUID SessionID;

        public Info()
        {
        }
    }


    public Info Info_Field;

    public TeleportCancel()
    {
        zeroCoded = false;
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportCancel(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)72);
        packUUID(bytebuffer, Info_Field.AgentID);
        packUUID(bytebuffer, Info_Field.SessionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.SessionID = unpackUUID(bytebuffer);
    }
}
