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

public class RegionHandshake extends SLMessage
{
    public static class RegionInfo
    {

        public float BillableFactor;
        public UUID CacheID;
        public boolean IsEstateManager;
        public int RegionFlags;
        public int SimAccess;
        public byte SimName[];
        public UUID SimOwner;
        public UUID TerrainBase0;
        public UUID TerrainBase1;
        public UUID TerrainBase2;
        public UUID TerrainBase3;
        public UUID TerrainDetail0;
        public UUID TerrainDetail1;
        public UUID TerrainDetail2;
        public UUID TerrainDetail3;
        public float TerrainHeightRange00;
        public float TerrainHeightRange01;
        public float TerrainHeightRange10;
        public float TerrainHeightRange11;
        public float TerrainStartHeight00;
        public float TerrainStartHeight01;
        public float TerrainStartHeight10;
        public float TerrainStartHeight11;
        public float WaterHeight;

        public RegionInfo()
        {
        }
    }

    public static class RegionInfo2
    {

        public UUID RegionID;

        public RegionInfo2()
        {
        }
    }

    public static class RegionInfo3
    {

        public int CPUClassID;
        public int CPURatio;
        public byte ColoName[];
        public byte ProductName[];
        public byte ProductSKU[];

        public RegionInfo3()
        {
        }
    }

    public static class RegionInfo4
    {

        public long RegionFlagsExtended;
        public long RegionProtocols;

        public RegionInfo4()
        {
        }
    }


    public RegionInfo2 RegionInfo2_Field;
    public RegionInfo3 RegionInfo3_Field;
    public ArrayList RegionInfo4_Fields;
    public RegionInfo RegionInfo_Field;

    public RegionHandshake()
    {
        RegionInfo4_Fields = new ArrayList();
        zeroCoded = true;
        RegionInfo_Field = new RegionInfo();
        RegionInfo2_Field = new RegionInfo2();
        RegionInfo3_Field = new RegionInfo3();
    }

    public int CalcPayloadSize()
    {
        return RegionInfo_Field.SimName.length + 6 + 16 + 1 + 4 + 4 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 16 + (RegionInfo3_Field.ColoName.length + 9 + 1 + RegionInfo3_Field.ProductSKU.length + 1 + RegionInfo3_Field.ProductName.length) + 1 + RegionInfo4_Fields.size() * 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRegionHandshake(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-108);
        packInt(bytebuffer, RegionInfo_Field.RegionFlags);
        packByte(bytebuffer, (byte)RegionInfo_Field.SimAccess);
        packVariable(bytebuffer, RegionInfo_Field.SimName, 1);
        packUUID(bytebuffer, RegionInfo_Field.SimOwner);
        packBoolean(bytebuffer, RegionInfo_Field.IsEstateManager);
        packFloat(bytebuffer, RegionInfo_Field.WaterHeight);
        packFloat(bytebuffer, RegionInfo_Field.BillableFactor);
        packUUID(bytebuffer, RegionInfo_Field.CacheID);
        packUUID(bytebuffer, RegionInfo_Field.TerrainBase0);
        packUUID(bytebuffer, RegionInfo_Field.TerrainBase1);
        packUUID(bytebuffer, RegionInfo_Field.TerrainBase2);
        packUUID(bytebuffer, RegionInfo_Field.TerrainBase3);
        packUUID(bytebuffer, RegionInfo_Field.TerrainDetail0);
        packUUID(bytebuffer, RegionInfo_Field.TerrainDetail1);
        packUUID(bytebuffer, RegionInfo_Field.TerrainDetail2);
        packUUID(bytebuffer, RegionInfo_Field.TerrainDetail3);
        packFloat(bytebuffer, RegionInfo_Field.TerrainStartHeight00);
        packFloat(bytebuffer, RegionInfo_Field.TerrainStartHeight01);
        packFloat(bytebuffer, RegionInfo_Field.TerrainStartHeight10);
        packFloat(bytebuffer, RegionInfo_Field.TerrainStartHeight11);
        packFloat(bytebuffer, RegionInfo_Field.TerrainHeightRange00);
        packFloat(bytebuffer, RegionInfo_Field.TerrainHeightRange01);
        packFloat(bytebuffer, RegionInfo_Field.TerrainHeightRange10);
        packFloat(bytebuffer, RegionInfo_Field.TerrainHeightRange11);
        packUUID(bytebuffer, RegionInfo2_Field.RegionID);
        packInt(bytebuffer, RegionInfo3_Field.CPUClassID);
        packInt(bytebuffer, RegionInfo3_Field.CPURatio);
        packVariable(bytebuffer, RegionInfo3_Field.ColoName, 1);
        packVariable(bytebuffer, RegionInfo3_Field.ProductSKU, 1);
        packVariable(bytebuffer, RegionInfo3_Field.ProductName, 1);
        bytebuffer.put((byte)RegionInfo4_Fields.size());
        RegionInfo4 regioninfo4;
        for (Iterator iterator = RegionInfo4_Fields.iterator(); iterator.hasNext(); packLong(bytebuffer, regioninfo4.RegionProtocols))
        {
            regioninfo4 = (RegionInfo4)iterator.next();
            packLong(bytebuffer, regioninfo4.RegionFlagsExtended);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RegionInfo_Field.RegionFlags = unpackInt(bytebuffer);
        RegionInfo_Field.SimAccess = unpackByte(bytebuffer) & 0xff;
        RegionInfo_Field.SimName = unpackVariable(bytebuffer, 1);
        RegionInfo_Field.SimOwner = unpackUUID(bytebuffer);
        RegionInfo_Field.IsEstateManager = unpackBoolean(bytebuffer);
        RegionInfo_Field.WaterHeight = unpackFloat(bytebuffer);
        RegionInfo_Field.BillableFactor = unpackFloat(bytebuffer);
        RegionInfo_Field.CacheID = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainBase0 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainBase1 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainBase2 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainBase3 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainDetail0 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainDetail1 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainDetail2 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainDetail3 = unpackUUID(bytebuffer);
        RegionInfo_Field.TerrainStartHeight00 = unpackFloat(bytebuffer);
        RegionInfo_Field.TerrainStartHeight01 = unpackFloat(bytebuffer);
        RegionInfo_Field.TerrainStartHeight10 = unpackFloat(bytebuffer);
        RegionInfo_Field.TerrainStartHeight11 = unpackFloat(bytebuffer);
        RegionInfo_Field.TerrainHeightRange00 = unpackFloat(bytebuffer);
        RegionInfo_Field.TerrainHeightRange01 = unpackFloat(bytebuffer);
        RegionInfo_Field.TerrainHeightRange10 = unpackFloat(bytebuffer);
        RegionInfo_Field.TerrainHeightRange11 = unpackFloat(bytebuffer);
        RegionInfo2_Field.RegionID = unpackUUID(bytebuffer);
        RegionInfo3_Field.CPUClassID = unpackInt(bytebuffer);
        RegionInfo3_Field.CPURatio = unpackInt(bytebuffer);
        RegionInfo3_Field.ColoName = unpackVariable(bytebuffer, 1);
        RegionInfo3_Field.ProductSKU = unpackVariable(bytebuffer, 1);
        RegionInfo3_Field.ProductName = unpackVariable(bytebuffer, 1);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RegionInfo4 regioninfo4 = new RegionInfo4();
            regioninfo4.RegionFlagsExtended = unpackLong(bytebuffer);
            regioninfo4.RegionProtocols = unpackLong(bytebuffer);
            RegionInfo4_Fields.add(regioninfo4);
        }

    }
}
