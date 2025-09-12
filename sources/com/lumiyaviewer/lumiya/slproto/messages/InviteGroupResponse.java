// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class InviteGroupResponse extends SLMessage
{
    public static class InviteData
    {

        public UUID AgentID;
        public UUID GroupID;
        public UUID InviteeID;
        public int MembershipFee;
        public UUID RoleID;

        public InviteData()
        {
        }
    }


    public InviteData InviteData_Field;

    public InviteGroupResponse()
    {
        zeroCoded = false;
        InviteData_Field = new InviteData();
    }

    public int CalcPayloadSize()
    {
        return 72;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleInviteGroupResponse(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)94);
        packUUID(bytebuffer, InviteData_Field.AgentID);
        packUUID(bytebuffer, InviteData_Field.InviteeID);
        packUUID(bytebuffer, InviteData_Field.GroupID);
        packUUID(bytebuffer, InviteData_Field.RoleID);
        packInt(bytebuffer, InviteData_Field.MembershipFee);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        InviteData_Field.AgentID = unpackUUID(bytebuffer);
        InviteData_Field.InviteeID = unpackUUID(bytebuffer);
        InviteData_Field.GroupID = unpackUUID(bytebuffer);
        InviteData_Field.RoleID = unpackUUID(bytebuffer);
        InviteData_Field.MembershipFee = unpackInt(bytebuffer);
    }
}
