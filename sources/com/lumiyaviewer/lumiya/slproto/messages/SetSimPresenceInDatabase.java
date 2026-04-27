// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SetSimPresenceInDatabase extends SLMessage
{
    public static class SimData
    {

        public int AgentCount;
        public int GridX;
        public int GridY;
        public byte HostName[];
        public int PID;
        public UUID RegionID;
        public byte Status[];
        public int TimeToLive;

        public SimData()
        {
        }
    }


    public SimData SimData_Field;

    public SetSimPresenceInDatabase()
    {
        zeroCoded = false;
        SimData_Field = new SimData();
    }

    public int CalcPayloadSize()
    {
        return SimData_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + SimData_Field.Status.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSetSimPresenceInDatabase(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)23);
        packUUID(bytebuffer, SimData_Field.RegionID);
        packVariable(bytebuffer, SimData_Field.HostName, 1);
        packInt(bytebuffer, SimData_Field.GridX);
        packInt(bytebuffer, SimData_Field.GridY);
        packInt(bytebuffer, SimData_Field.PID);
        packInt(bytebuffer, SimData_Field.AgentCount);
        packInt(bytebuffer, SimData_Field.TimeToLive);
        packVariable(bytebuffer, SimData_Field.Status, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SimData_Field.RegionID = unpackUUID(bytebuffer);
        SimData_Field.HostName = unpackVariable(bytebuffer, 1);
        SimData_Field.GridX = unpackInt(bytebuffer);
        SimData_Field.GridY = unpackInt(bytebuffer);
        SimData_Field.PID = unpackInt(bytebuffer);
        SimData_Field.AgentCount = unpackInt(bytebuffer);
        SimData_Field.TimeToLive = unpackInt(bytebuffer);
        SimData_Field.Status = unpackVariable(bytebuffer, 1);
    }
}
