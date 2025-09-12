// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class UpdateSimulator extends SLMessage
{
    public static class SimulatorInfo
    {

        public int EstateID;
        public UUID RegionID;
        public int SimAccess;
        public byte SimName[];

        public SimulatorInfo()
        {
        }
    }


    public SimulatorInfo SimulatorInfo_Field;

    public UpdateSimulator()
    {
        zeroCoded = false;
        SimulatorInfo_Field = new SimulatorInfo();
    }

    public int CalcPayloadSize()
    {
        return SimulatorInfo_Field.SimName.length + 17 + 4 + 1 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUpdateSimulator(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)17);
        packUUID(bytebuffer, SimulatorInfo_Field.RegionID);
        packVariable(bytebuffer, SimulatorInfo_Field.SimName, 1);
        packInt(bytebuffer, SimulatorInfo_Field.EstateID);
        packByte(bytebuffer, (byte)SimulatorInfo_Field.SimAccess);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SimulatorInfo_Field.RegionID = unpackUUID(bytebuffer);
        SimulatorInfo_Field.SimName = unpackVariable(bytebuffer, 1);
        SimulatorInfo_Field.EstateID = unpackInt(bytebuffer);
        SimulatorInfo_Field.SimAccess = unpackByte(bytebuffer) & 0xff;
    }
}
