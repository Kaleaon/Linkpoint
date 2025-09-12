// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ObjectSpinStart extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ObjectData
    {

        public UUID ObjectID;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;

    public ObjectSpinStart()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return 52;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectSpinStart(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)120);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, ObjectData_Field.ObjectID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ObjectData_Field.ObjectID = unpackUUID(bytebuffer);
    }
}
