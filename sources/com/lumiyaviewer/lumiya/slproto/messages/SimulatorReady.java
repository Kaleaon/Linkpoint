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

public class SimulatorReady extends SLMessage
{
    public static class SimulatorBlock
    {

        public int EstateID;
        public int ParentEstateID;
        public int RegionFlags;
        public UUID RegionID;
        public int SimAccess;
        public byte SimName[];

        public SimulatorBlock()
        {
        }
    }

    public static class TelehubBlock
    {

        public boolean HasTelehub;
        public LLVector3 TelehubPos;

        public TelehubBlock()
        {
        }
    }


    public SimulatorBlock SimulatorBlock_Field;
    public TelehubBlock TelehubBlock_Field;

    public SimulatorReady()
    {
        zeroCoded = true;
        SimulatorBlock_Field = new SimulatorBlock();
        TelehubBlock_Field = new TelehubBlock();
    }

    public int CalcPayloadSize()
    {
        return SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 4 + 13;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimulatorReady(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)9);
        packVariable(bytebuffer, SimulatorBlock_Field.SimName, 1);
        packByte(bytebuffer, (byte)SimulatorBlock_Field.SimAccess);
        packInt(bytebuffer, SimulatorBlock_Field.RegionFlags);
        packUUID(bytebuffer, SimulatorBlock_Field.RegionID);
        packInt(bytebuffer, SimulatorBlock_Field.EstateID);
        packInt(bytebuffer, SimulatorBlock_Field.ParentEstateID);
        packBoolean(bytebuffer, TelehubBlock_Field.HasTelehub);
        packLLVector3(bytebuffer, TelehubBlock_Field.TelehubPos);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SimulatorBlock_Field.SimName = unpackVariable(bytebuffer, 1);
        SimulatorBlock_Field.SimAccess = unpackByte(bytebuffer) & 0xff;
        SimulatorBlock_Field.RegionFlags = unpackInt(bytebuffer);
        SimulatorBlock_Field.RegionID = unpackUUID(bytebuffer);
        SimulatorBlock_Field.EstateID = unpackInt(bytebuffer);
        SimulatorBlock_Field.ParentEstateID = unpackInt(bytebuffer);
        TelehubBlock_Field.HasTelehub = unpackBoolean(bytebuffer);
        TelehubBlock_Field.TelehubPos = unpackLLVector3(bytebuffer);
    }
}
