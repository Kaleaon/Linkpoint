// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RegionHandshake extends SLMessage
{
    public RegionInfo2 RegionInfo2_Field;
    public RegionInfo3 RegionInfo3_Field;
    public ArrayList<RegionInfo4> RegionInfo4_Fields;
    public RegionInfo RegionInfo_Field;
    
    public RegionHandshake() {
        this.RegionInfo4_Fields = new ArrayList<RegionInfo4>();
        this.zeroCoded = true;
        this.RegionInfo_Field = new RegionInfo();
        this.RegionInfo2_Field = new RegionInfo2();
        this.RegionInfo3_Field = new RegionInfo3();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RegionInfo_Field.SimName.length + 6 + 16 + 1 + 4 + 4 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 16 + (this.RegionInfo3_Field.ColoName.length + 9 + 1 + this.RegionInfo3_Field.ProductSKU.length + 1 + this.RegionInfo3_Field.ProductName.length) + 1 + this.RegionInfo4_Fields.size() * 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRegionHandshake(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-108));
        this.packInt(byteBuffer, this.RegionInfo_Field.RegionFlags);
        this.packByte(byteBuffer, (byte)this.RegionInfo_Field.SimAccess);
        this.packVariable(byteBuffer, this.RegionInfo_Field.SimName, 1);
        this.packUUID(byteBuffer, this.RegionInfo_Field.SimOwner);
        this.packBoolean(byteBuffer, this.RegionInfo_Field.IsEstateManager);
        this.packFloat(byteBuffer, this.RegionInfo_Field.WaterHeight);
        this.packFloat(byteBuffer, this.RegionInfo_Field.BillableFactor);
        this.packUUID(byteBuffer, this.RegionInfo_Field.CacheID);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase0);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase1);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase2);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase3);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail0);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail1);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail2);
        this.packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail3);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight00);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight01);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight10);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight11);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange00);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange01);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange10);
        this.packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange11);
        this.packUUID(byteBuffer, this.RegionInfo2_Field.RegionID);
        this.packInt(byteBuffer, this.RegionInfo3_Field.CPUClassID);
        this.packInt(byteBuffer, this.RegionInfo3_Field.CPURatio);
        this.packVariable(byteBuffer, this.RegionInfo3_Field.ColoName, 1);
        this.packVariable(byteBuffer, this.RegionInfo3_Field.ProductSKU, 1);
        this.packVariable(byteBuffer, this.RegionInfo3_Field.ProductName, 1);
        byteBuffer.put((byte)this.RegionInfo4_Fields.size());
        for (final RegionInfo4 regionInfo4 : this.RegionInfo4_Fields) {
            this.packLong(byteBuffer, regionInfo4.RegionFlagsExtended);
            this.packLong(byteBuffer, regionInfo4.RegionProtocols);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RegionInfo_Field.RegionFlags = this.unpackInt(byteBuffer);
        this.RegionInfo_Field.SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
        this.RegionInfo_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.RegionInfo_Field.SimOwner = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.IsEstateManager = this.unpackBoolean(byteBuffer);
        this.RegionInfo_Field.WaterHeight = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.BillableFactor = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.CacheID = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase0 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase1 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase2 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase3 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail0 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail1 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail2 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail3 = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight00 = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight01 = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight10 = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight11 = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange00 = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange01 = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange10 = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange11 = this.unpackFloat(byteBuffer);
        this.RegionInfo2_Field.RegionID = this.unpackUUID(byteBuffer);
        this.RegionInfo3_Field.CPUClassID = this.unpackInt(byteBuffer);
        this.RegionInfo3_Field.CPURatio = this.unpackInt(byteBuffer);
        this.RegionInfo3_Field.ColoName = this.unpackVariable(byteBuffer, 1);
        this.RegionInfo3_Field.ProductSKU = this.unpackVariable(byteBuffer, 1);
        this.RegionInfo3_Field.ProductName = this.unpackVariable(byteBuffer, 1);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RegionInfo4 e = new RegionInfo4();
            e.RegionFlagsExtended = this.unpackLong(byteBuffer);
            e.RegionProtocols = this.unpackLong(byteBuffer);
            this.RegionInfo4_Fields.add(e);
        }
    }
    
    public static class RegionInfo
    {
        public float BillableFactor;
        public UUID CacheID;
        public boolean IsEstateManager;
        public int RegionFlags;
        public int SimAccess;
        public byte[] SimName;
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
    }
    
    public static class RegionInfo2
    {
        public UUID RegionID;
    }
    
    public static class RegionInfo3
    {
        public int CPUClassID;
        public int CPURatio;
        public byte[] ColoName;
        public byte[] ProductName;
        public byte[] ProductSKU;
    }
    
    public static class RegionInfo4
    {
        public long RegionFlagsExtended;
        public long RegionProtocols;
    }
}
