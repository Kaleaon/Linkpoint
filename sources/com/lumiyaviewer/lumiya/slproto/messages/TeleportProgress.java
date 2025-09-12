// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TeleportProgress extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class Info
    {

        public byte Message[];
        public int TeleportFlags;

        public Info()
        {
        }
    }


    public AgentData AgentData_Field;
    public Info Info_Field;

    public TeleportProgress()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return Info_Field.Message.length + 5 + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportProgress(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)66);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packInt(bytebuffer, Info_Field.TeleportFlags);
        packVariable(bytebuffer, Info_Field.Message, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.TeleportFlags = unpackInt(bytebuffer);
        Info_Field.Message = unpackVariable(bytebuffer, 1);
    }
}
