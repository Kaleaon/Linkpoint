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

public class TeleportLocationRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Info
    {

        public LLVector3 LookAt;
        public LLVector3 Position;
        public long RegionHandle;

        public Info()
        {
        }
    }


    public AgentData AgentData_Field;
    public Info Info_Field;

    public TeleportLocationRequest()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return 68;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTeleportLocationRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)63);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packLong(bytebuffer, Info_Field.RegionHandle);
        packLLVector3(bytebuffer, Info_Field.Position);
        packLLVector3(bytebuffer, Info_Field.LookAt);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        Info_Field.RegionHandle = unpackLong(bytebuffer);
        Info_Field.Position = unpackLLVector3(bytebuffer);
        Info_Field.LookAt = unpackLLVector3(bytebuffer);
    }
}
