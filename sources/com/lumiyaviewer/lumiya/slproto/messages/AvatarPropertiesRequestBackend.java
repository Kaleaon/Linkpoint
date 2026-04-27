// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AvatarPropertiesRequestBackend extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID AvatarID;
        public int GodLevel;
        public boolean WebProfilesDisabled;

        public AgentData()
        {
        }
    }


    public AgentData AgentData_Field;

    public AvatarPropertiesRequestBackend()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return 38;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarPropertiesRequestBackend(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-86);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.AvatarID);
        packByte(bytebuffer, (byte)AgentData_Field.GodLevel);
        packBoolean(bytebuffer, AgentData_Field.WebProfilesDisabled);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.AvatarID = unpackUUID(bytebuffer);
        AgentData_Field.GodLevel = unpackByte(bytebuffer) & 0xff;
        AgentData_Field.WebProfilesDisabled = unpackBoolean(bytebuffer);
    }
}
