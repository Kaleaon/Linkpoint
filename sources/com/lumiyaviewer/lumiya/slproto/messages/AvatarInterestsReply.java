// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AvatarInterestsReply extends SLMessage
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

        public byte LanguagesText[];
        public int SkillsMask;
        public byte SkillsText[];
        public int WantToMask;
        public byte WantToText[];

        public PropertiesData()
        {
        }
    }


    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;

    public AvatarInterestsReply()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        PropertiesData_Field = new PropertiesData();
    }

    public int CalcPayloadSize()
    {
        return PropertiesData_Field.WantToText.length + 5 + 4 + 1 + PropertiesData_Field.SkillsText.length + 1 + PropertiesData_Field.LanguagesText.length + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarInterestsReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-84);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.AvatarID);
        packInt(bytebuffer, PropertiesData_Field.WantToMask);
        packVariable(bytebuffer, PropertiesData_Field.WantToText, 1);
        packInt(bytebuffer, PropertiesData_Field.SkillsMask);
        packVariable(bytebuffer, PropertiesData_Field.SkillsText, 1);
        packVariable(bytebuffer, PropertiesData_Field.LanguagesText, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.AvatarID = unpackUUID(bytebuffer);
        PropertiesData_Field.WantToMask = unpackInt(bytebuffer);
        PropertiesData_Field.WantToText = unpackVariable(bytebuffer, 1);
        PropertiesData_Field.SkillsMask = unpackInt(bytebuffer);
        PropertiesData_Field.SkillsText = unpackVariable(bytebuffer, 1);
        PropertiesData_Field.LanguagesText = unpackVariable(bytebuffer, 1);
    }
}
