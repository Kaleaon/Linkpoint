// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class UserInfoReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class UserData
    {

        public byte DirectoryVisibility[];
        public byte EMail[];
        public boolean IMViaEMail;

        public UserData()
        {
        }
    }


    public AgentData AgentData_Field;
    public UserData UserData_Field;

    public UserInfoReply()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        UserData_Field = new UserData();
    }

    public int CalcPayloadSize()
    {
        return UserData_Field.DirectoryVisibility.length + 2 + 2 + UserData_Field.EMail.length + 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUserInfoReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-112);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packBoolean(bytebuffer, UserData_Field.IMViaEMail);
        packVariable(bytebuffer, UserData_Field.DirectoryVisibility, 1);
        packVariable(bytebuffer, UserData_Field.EMail, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        UserData_Field.IMViaEMail = unpackBoolean(bytebuffer);
        UserData_Field.DirectoryVisibility = unpackVariable(bytebuffer, 1);
        UserData_Field.EMail = unpackVariable(bytebuffer, 2);
    }
}
