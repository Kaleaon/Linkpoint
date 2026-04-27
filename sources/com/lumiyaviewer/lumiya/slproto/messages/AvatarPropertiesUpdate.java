// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AvatarPropertiesUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class PropertiesData
    {

        public byte AboutText[];
        public boolean AllowPublish;
        public byte FLAboutText[];
        public UUID FLImageID;
        public UUID ImageID;
        public boolean MaturePublish;
        public byte ProfileURL[];

        public PropertiesData()
        {
        }
    }


    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;

    public AvatarPropertiesUpdate()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        PropertiesData_Field = new PropertiesData();
    }

    public int CalcPayloadSize()
    {
        return PropertiesData_Field.AboutText.length + 34 + 1 + PropertiesData_Field.FLAboutText.length + 1 + 1 + 1 + PropertiesData_Field.ProfileURL.length + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAvatarPropertiesUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-82);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, PropertiesData_Field.ImageID);
        packUUID(bytebuffer, PropertiesData_Field.FLImageID);
        packVariable(bytebuffer, PropertiesData_Field.AboutText, 2);
        packVariable(bytebuffer, PropertiesData_Field.FLAboutText, 1);
        packBoolean(bytebuffer, PropertiesData_Field.AllowPublish);
        packBoolean(bytebuffer, PropertiesData_Field.MaturePublish);
        packVariable(bytebuffer, PropertiesData_Field.ProfileURL, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        PropertiesData_Field.ImageID = unpackUUID(bytebuffer);
        PropertiesData_Field.FLImageID = unpackUUID(bytebuffer);
        PropertiesData_Field.AboutText = unpackVariable(bytebuffer, 2);
        PropertiesData_Field.FLAboutText = unpackVariable(bytebuffer, 1);
        PropertiesData_Field.AllowPublish = unpackBoolean(bytebuffer);
        PropertiesData_Field.MaturePublish = unpackBoolean(bytebuffer);
        PropertiesData_Field.ProfileURL = unpackVariable(bytebuffer, 1);
    }
}
