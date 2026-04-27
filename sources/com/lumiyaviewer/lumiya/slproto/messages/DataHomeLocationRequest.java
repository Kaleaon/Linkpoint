// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DataHomeLocationRequest extends SLMessage
{
    public static class AgentInfo
    {

        public int AgentEffectiveMaturity;

        public AgentInfo()
        {
        }
    }

    public static class Info
    {

        public UUID AgentID;
        public int KickedFromEstateID;

        public Info()
        {
        }
    }


    public AgentInfo AgentInfo_Field;
    public Info Info_Field;

    public DataHomeLocationRequest()
    {
        zeroCoded = true;
        Info_Field = new Info();
        AgentInfo_Field = new AgentInfo();
    }

    public int CalcPayloadSize()
    {
        return 28;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDataHomeLocationRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)67);
        packUUID(bytebuffer, Info_Field.AgentID);
        packInt(bytebuffer, Info_Field.KickedFromEstateID);
        packInt(bytebuffer, AgentInfo_Field.AgentEffectiveMaturity);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.AgentID = unpackUUID(bytebuffer);
        Info_Field.KickedFromEstateID = unpackInt(bytebuffer);
        AgentInfo_Field.AgentEffectiveMaturity = unpackInt(bytebuffer);
    }
}
