// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class KickUser extends SLMessage
{
    public static class TargetBlock
    {

        public Inet4Address TargetIP;
        public int TargetPort;

        public TargetBlock()
        {
        }
    }

    public static class UserInfo
    {

        public UUID AgentID;
        public byte Reason[];
        public UUID SessionID;

        public UserInfo()
        {
        }
    }


    public TargetBlock TargetBlock_Field;
    public UserInfo UserInfo_Field;

    public KickUser()
    {
        zeroCoded = false;
        TargetBlock_Field = new TargetBlock();
        UserInfo_Field = new UserInfo();
    }

    public int CalcPayloadSize()
    {
        return UserInfo_Field.Reason.length + 34 + 10;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleKickUser(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-93);
        packIPAddress(bytebuffer, TargetBlock_Field.TargetIP);
        packShort(bytebuffer, (short)TargetBlock_Field.TargetPort);
        packUUID(bytebuffer, UserInfo_Field.AgentID);
        packUUID(bytebuffer, UserInfo_Field.SessionID);
        packVariable(bytebuffer, UserInfo_Field.Reason, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TargetBlock_Field.TargetIP = unpackIPAddress(bytebuffer);
        TargetBlock_Field.TargetPort = unpackShort(bytebuffer) & 0xffff;
        UserInfo_Field.AgentID = unpackUUID(bytebuffer);
        UserInfo_Field.SessionID = unpackUUID(bytebuffer);
        UserInfo_Field.Reason = unpackVariable(bytebuffer, 2);
    }
}
