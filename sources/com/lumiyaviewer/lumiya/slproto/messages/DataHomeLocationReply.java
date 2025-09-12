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

public class DataHomeLocationReply extends SLMessage
{
    public static class Info
    {

        public UUID AgentID;
        public LLVector3 LookAt;
        public LLVector3 Position;
        public long RegionHandle;

        public Info()
        {
        }
    }


    public Info Info_Field;

    public DataHomeLocationReply()
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
        slmessagehandler.HandleDataHomeLocationReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)68);
        packUUID(bytebuffer, Info_Field.AgentID);
        packLong(bytebuffer, Info_Field.RegionHandle);
        packLLVector3(bytebuffer, Info_Field.Position);
        packLLVector3(bytebuffer, Info_Field.LookAt);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.RegionHandle = unpackLong(bytebuffer);
        Info_Field.Position = unpackLLVector3(bytebuffer);
        Info_Field.LookAt = unpackLLVector3(bytebuffer);
    }
}
