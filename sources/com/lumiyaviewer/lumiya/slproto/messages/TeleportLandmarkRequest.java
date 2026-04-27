// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TeleportLandmarkRequest extends SLMessage
{
    public static class Info
    {

        public UUID AgentID;
        public UUID LandmarkID;
        public UUID SessionID;

        public Info()
        {
        }
    }


    public Info Info_Field;

    public TeleportLandmarkRequest()
    {
        zeroCoded = true;
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return 52;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportLandmarkRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)65);
        packUUID(bytebuffer, Info_Field.AgentID);
        packUUID(bytebuffer, Info_Field.SessionID);
        packUUID(bytebuffer, Info_Field.LandmarkID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.SessionID = unpackUUID(bytebuffer);
        Info_Field.LandmarkID = unpackUUID(bytebuffer);
    }
}
