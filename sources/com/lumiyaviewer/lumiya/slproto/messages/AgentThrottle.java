// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentThrottle extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Throttle
    {

        public int GenCounter;
        public byte Throttles[];

        public Throttle()
        {
        }
    }


    public AgentData AgentData_Field;
    public Throttle Throttle_Field;

    public AgentThrottle()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        Throttle_Field = new Throttle();
    }

    public int CalcPayloadSize()
    {
        return Throttle_Field.Throttles.length + 5 + 40;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentThrottle(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)81);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.CircuitCode);
        packInt(bytebuffer, Throttle_Field.GenCounter);
        packVariable(bytebuffer, Throttle_Field.Throttles, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.CircuitCode = unpackInt(bytebuffer);
        Throttle_Field.GenCounter = unpackInt(bytebuffer);
        Throttle_Field.Throttles = unpackVariable(bytebuffer, 1);
    }
}
