// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AvatarPropertiesReply extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID AvatarID;

        public AgentData()
        {
        }
    }

    public static class PropertiesData
    {

        public byte AboutText[];
        public byte BornOn[];
        public byte CharterMember[];
        public byte FLAboutText[];
        public UUID FLImageID;
        public int Flags;
        public UUID ImageID;
        public UUID PartnerID;
        public byte ProfileURL[];

        public PropertiesData()
        {
        }
    }


    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;

    public AvatarPropertiesReply()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        PropertiesData_Field = new PropertiesData();
    }

    public int CalcPayloadSize()
    {
        return PropertiesData_Field.AboutText.length + 50 + 1 + PropertiesData_Field.FLAboutText.length + 1 + PropertiesData_Field.BornOn.length + 1 + PropertiesData_Field.ProfileURL.length + 1 + PropertiesData_Field.CharterMember.length + 4 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarPropertiesReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-85);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.AvatarID);
        packUUID(bytebuffer, PropertiesData_Field.ImageID);
        packUUID(bytebuffer, PropertiesData_Field.FLImageID);
        packUUID(bytebuffer, PropertiesData_Field.PartnerID);
        packVariable(bytebuffer, PropertiesData_Field.AboutText, 2);
        packVariable(bytebuffer, PropertiesData_Field.FLAboutText, 1);
        packVariable(bytebuffer, PropertiesData_Field.BornOn, 1);
        packVariable(bytebuffer, PropertiesData_Field.ProfileURL, 1);
        packVariable(bytebuffer, PropertiesData_Field.CharterMember, 1);
        packInt(bytebuffer, PropertiesData_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.AvatarID = unpackUUID(bytebuffer);
        PropertiesData_Field.ImageID = unpackUUID(bytebuffer);
        PropertiesData_Field.FLImageID = unpackUUID(bytebuffer);
        PropertiesData_Field.PartnerID = unpackUUID(bytebuffer);
        PropertiesData_Field.AboutText = unpackVariable(bytebuffer, 2);
        PropertiesData_Field.FLAboutText = unpackVariable(bytebuffer, 1);
        PropertiesData_Field.BornOn = unpackVariable(bytebuffer, 1);
        PropertiesData_Field.ProfileURL = unpackVariable(bytebuffer, 1);
        PropertiesData_Field.CharterMember = unpackVariable(bytebuffer, 1);
        PropertiesData_Field.Flags = unpackInt(bytebuffer);
    }
}
