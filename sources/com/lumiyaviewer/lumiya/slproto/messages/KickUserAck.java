// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class KickUserAck extends SLMessage
{
    public static class UserInfo
    {

        public int Flags;
        public UUID SessionID;

        public UserInfo()
        {
        }
    }


    public UserInfo UserInfo_Field;

    public KickUserAck()
    {
        zeroCoded = false;
        UserInfo_Field = new UserInfo();
    }

    public int CalcPayloadSize()
    {
        return 24;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleKickUserAck(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-92);
        packUUID(bytebuffer, UserInfo_Field.SessionID);
        packInt(bytebuffer, UserInfo_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        UserInfo_Field.SessionID = unpackUUID(bytebuffer);
        UserInfo_Field.Flags = unpackInt(bytebuffer);
    }
}
