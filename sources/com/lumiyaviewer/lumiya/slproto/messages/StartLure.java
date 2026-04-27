// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class StartLure extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Info
    {

        public int LureType;
        public byte Message[];

        public Info()
        {
        }
    }

    public static class TargetData
    {

        public UUID TargetID;

        public TargetData()
        {
        }
    }


    public AgentData AgentData_Field;
    public Info Info_Field;
    public ArrayList TargetData_Fields;

    public StartLure()
    {
        TargetData_Fields = new ArrayList();
        zeroCoded = false;
        AgentData_Field = new AgentData();
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return Info_Field.Message.length + 2 + 36 + 1 + TargetData_Fields.size() * 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleStartLure(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)70);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packByte(bytebuffer, (byte)Info_Field.LureType);
        packVariable(bytebuffer, Info_Field.Message, 1);
        bytebuffer.put((byte)TargetData_Fields.size());
        for (Iterator iterator = TargetData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((TargetData)iterator.next()).TargetID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        Info_Field.LureType = unpackByte(bytebuffer) & 0xff;
        Info_Field.Message = unpackVariable(bytebuffer, 1);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            TargetData targetdata = new TargetData();
            targetdata.TargetID = unpackUUID(bytebuffer);
            TargetData_Fields.add(targetdata);
        }

    }
}
