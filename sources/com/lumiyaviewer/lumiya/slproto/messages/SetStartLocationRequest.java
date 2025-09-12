// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SetStartLocationRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class StartLocationData
    {

        public int LocationID;
        public LLVector3 LocationLookAt;
        public LLVector3 LocationPos;
        public byte SimName[];

        public StartLocationData()
        {
        }
    }


    public AgentData AgentData_Field;
    public StartLocationData StartLocationData_Field;

    public SetStartLocationRequest()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        StartLocationData_Field = new StartLocationData();
    }

    public int CalcPayloadSize()
    {
        return StartLocationData_Field.SimName.length + 1 + 4 + 12 + 12 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSetStartLocationRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)68);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packVariable(bytebuffer, StartLocationData_Field.SimName, 1);
        packInt(bytebuffer, StartLocationData_Field.LocationID);
        packLLVector3(bytebuffer, StartLocationData_Field.LocationPos);
        packLLVector3(bytebuffer, StartLocationData_Field.LocationLookAt);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        StartLocationData_Field.SimName = unpackVariable(bytebuffer, 1);
        StartLocationData_Field.LocationID = unpackInt(bytebuffer);
        StartLocationData_Field.LocationPos = unpackLLVector3(bytebuffer);
        StartLocationData_Field.LocationLookAt = unpackLLVector3(bytebuffer);
    }
}
