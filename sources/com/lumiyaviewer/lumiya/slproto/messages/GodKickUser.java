// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class GodKickUser extends SLMessage
{
    public static class UserInfo
    {

        public UUID AgentID;
        public UUID GodID;
        public UUID GodSessionID;
        public int KickFlags;
        public byte Reason[];

        public UserInfo()
        {
        }
    }


    public UserInfo UserInfo_Field;

    public GodKickUser()
    {
        zeroCoded = false;
        UserInfo_Field = new UserInfo();
    }

    public int CalcPayloadSize()
    {
        return UserInfo_Field.Reason.length + 54 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGodKickUser(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-91);
        packUUID(bytebuffer, UserInfo_Field.GodID);
        packUUID(bytebuffer, UserInfo_Field.GodSessionID);
        packUUID(bytebuffer, UserInfo_Field.AgentID);
        packInt(bytebuffer, UserInfo_Field.KickFlags);
        packVariable(bytebuffer, UserInfo_Field.Reason, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        UserInfo_Field.GodID = unpackUUID(bytebuffer);
        UserInfo_Field.GodSessionID = unpackUUID(bytebuffer);
        UserInfo_Field.AgentID = unpackUUID(bytebuffer);
        UserInfo_Field.KickFlags = unpackInt(bytebuffer);
        UserInfo_Field.Reason = unpackVariable(bytebuffer, 2);
    }
}
