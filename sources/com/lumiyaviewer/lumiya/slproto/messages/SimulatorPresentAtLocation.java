// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SimulatorPresentAtLocation extends SLMessage
{
    public static class NeighborBlock
    {

        public Inet4Address IP;
        public int Port;

        public NeighborBlock()
        {
        }
    }

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

    public static class SimulatorPublicHostBlock
    {

        public int GridX;
        public int GridY;
        public int Port;
        public Inet4Address SimulatorIP;

        public SimulatorPublicHostBlock()
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


    public NeighborBlock NeighborBlock_Fields[];
    public SimulatorBlock SimulatorBlock_Field;
    public SimulatorPublicHostBlock SimulatorPublicHostBlock_Field;
    public ArrayList TelehubBlock_Fields;

    public SimulatorPresentAtLocation()
    {
        int i = 0;
        super();
        NeighborBlock_Fields = new NeighborBlock[4];
        TelehubBlock_Fields = new ArrayList();
        zeroCoded = false;
        SimulatorPublicHostBlock_Field = new SimulatorPublicHostBlock();
        for (; i < 4; i++)
        {
            NeighborBlock_Fields[i] = new NeighborBlock();
        }

        SimulatorBlock_Field = new SimulatorBlock();
    }

    public int CalcPayloadSize()
    {
        return SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 42 + 1 + TelehubBlock_Fields.size() * 13;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimulatorPresentAtLocation(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        int i = 0;
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)11);
        packShort(bytebuffer, (short)SimulatorPublicHostBlock_Field.Port);
        packIPAddress(bytebuffer, SimulatorPublicHostBlock_Field.SimulatorIP);
        packInt(bytebuffer, SimulatorPublicHostBlock_Field.GridX);
        packInt(bytebuffer, SimulatorPublicHostBlock_Field.GridY);
        for (; i < 4; i++)
        {
            packIPAddress(bytebuffer, NeighborBlock_Fields[i].IP);
            packShort(bytebuffer, (short)NeighborBlock_Fields[i].Port);
        }

        packVariable(bytebuffer, SimulatorBlock_Field.SimName, 1);
        packByte(bytebuffer, (byte)SimulatorBlock_Field.SimAccess);
        packInt(bytebuffer, SimulatorBlock_Field.RegionFlags);
        packUUID(bytebuffer, SimulatorBlock_Field.RegionID);
        packInt(bytebuffer, SimulatorBlock_Field.EstateID);
        packInt(bytebuffer, SimulatorBlock_Field.ParentEstateID);
        bytebuffer.put((byte)TelehubBlock_Fields.size());
        TelehubBlock telehubblock;
        for (Iterator iterator = TelehubBlock_Fields.iterator(); iterator.hasNext(); packLLVector3(bytebuffer, telehubblock.TelehubPos))
        {
            telehubblock = (TelehubBlock)iterator.next();
            packBoolean(bytebuffer, telehubblock.HasTelehub);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        SimulatorPublicHostBlock_Field.Port = unpackShort(bytebuffer) & 0xffff;
        SimulatorPublicHostBlock_Field.SimulatorIP = unpackIPAddress(bytebuffer);
        SimulatorPublicHostBlock_Field.GridX = unpackInt(bytebuffer);
        SimulatorPublicHostBlock_Field.GridY = unpackInt(bytebuffer);
        for (int i = 0; i < 4; i++)
        {
            NeighborBlock_Fields[i].IP = unpackIPAddress(bytebuffer);
            NeighborBlock_Fields[i].Port = unpackShort(bytebuffer) & 0xffff;
        }

        SimulatorBlock_Field.SimName = unpackVariable(bytebuffer, 1);
        SimulatorBlock_Field.SimAccess = unpackByte(bytebuffer) & 0xff;
        SimulatorBlock_Field.RegionFlags = unpackInt(bytebuffer);
        SimulatorBlock_Field.RegionID = unpackUUID(bytebuffer);
        SimulatorBlock_Field.EstateID = unpackInt(bytebuffer);
        SimulatorBlock_Field.ParentEstateID = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            TelehubBlock telehubblock = new TelehubBlock();
            telehubblock.HasTelehub = unpackBoolean(bytebuffer);
            telehubblock.TelehubPos = unpackLLVector3(bytebuffer);
            TelehubBlock_Fields.add(telehubblock);
        }

    }
}
