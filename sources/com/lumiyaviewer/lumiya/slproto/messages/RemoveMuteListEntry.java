// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RemoveMuteListEntry extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class MuteData
    {

        public UUID MuteID;
        public byte MuteName[];

        public MuteData()
        {
        }
    }


    public AgentData AgentData_Field;
    public MuteData MuteData_Field;

    public RemoveMuteListEntry()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        MuteData_Field = new MuteData();
    }

    public int CalcPayloadSize()
    {
        return MuteData_Field.MuteName.length + 17 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRemoveMuteListEntry(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)8);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, MuteData_Field.MuteID);
        packVariable(bytebuffer, MuteData_Field.MuteName, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        MuteData_Field.MuteID = unpackUUID(bytebuffer);
        MuteData_Field.MuteName = unpackVariable(bytebuffer, 1);
    }
}
