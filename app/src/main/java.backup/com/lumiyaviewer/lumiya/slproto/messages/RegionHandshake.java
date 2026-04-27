package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class RegionHandshake extends SLMessage {
    public RegionInfo2 RegionInfo2_Field;
    public RegionInfo3 RegionInfo3_Field;
    public ArrayList<RegionInfo4> RegionInfo4_Fields = new ArrayList<>();
    public RegionInfo RegionInfo_Field;

    public static class RegionInfo {
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

    public static class RegionInfo2 {
        public UUID RegionID;
    }

    public static class RegionInfo3 {
        public int CPUClassID;
        public int CPURatio;
        public byte[] ColoName;
        public byte[] ProductName;
        public byte[] ProductSKU;
    }

    public static class RegionInfo4 {
        public long RegionFlagsExtended;
        public long RegionProtocols;
    }

    public RegionHandshake() {
        this.zeroCoded = true;
        this.RegionInfo_Field = new RegionInfo();
        this.RegionInfo2_Field = new RegionInfo2();
        this.RegionInfo3_Field = new RegionInfo3();
    }

    public int CalcPayloadSize() {
        return this.RegionInfo_Field.SimName.length + 6 + 16 + 1 + 4 + 4 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 16 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 16 + this.RegionInfo3_Field.ColoName.length + 9 + 1 + this.RegionInfo3_Field.ProductSKU.length + 1 + this.RegionInfo3_Field.ProductName.length + 1 + (this.RegionInfo4_Fields.size() * 16);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandshake(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -108);
        packInt(byteBuffer, this.RegionInfo_Field.RegionFlags);
        packByte(byteBuffer, (byte) this.RegionInfo_Field.SimAccess);
        packVariable(byteBuffer, this.RegionInfo_Field.SimName, 1);
        packUUID(byteBuffer, this.RegionInfo_Field.SimOwner);
        packBoolean(byteBuffer, this.RegionInfo_Field.IsEstateManager);
        packFloat(byteBuffer, this.RegionInfo_Field.WaterHeight);
        packFloat(byteBuffer, this.RegionInfo_Field.BillableFactor);
        packUUID(byteBuffer, this.RegionInfo_Field.CacheID);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase0);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase1);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase2);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainBase3);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail0);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail1);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail2);
        packUUID(byteBuffer, this.RegionInfo_Field.TerrainDetail3);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight00);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight01);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight10);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainStartHeight11);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange00);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange01);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange10);
        packFloat(byteBuffer, this.RegionInfo_Field.TerrainHeightRange11);
        packUUID(byteBuffer, this.RegionInfo2_Field.RegionID);
        packInt(byteBuffer, this.RegionInfo3_Field.CPUClassID);
        packInt(byteBuffer, this.RegionInfo3_Field.CPURatio);
        packVariable(byteBuffer, this.RegionInfo3_Field.ColoName, 1);
        packVariable(byteBuffer, this.RegionInfo3_Field.ProductSKU, 1);
        packVariable(byteBuffer, this.RegionInfo3_Field.ProductName, 1);
        byteBuffer.put((byte) this.RegionInfo4_Fields.size());
        for (RegionInfo4 regionInfo4 : this.RegionInfo4_Fields) {
            packLong(byteBuffer, regionInfo4.RegionFlagsExtended);
            packLong(byteBuffer, regionInfo4.RegionProtocols);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionInfo_Field.RegionFlags = unpackInt(byteBuffer);
        this.RegionInfo_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.RegionInfo_Field.SimName = unpackVariable(byteBuffer, 1);
        this.RegionInfo_Field.SimOwner = unpackUUID(byteBuffer);
        this.RegionInfo_Field.IsEstateManager = unpackBoolean(byteBuffer);
        this.RegionInfo_Field.WaterHeight = unpackFloat(byteBuffer);
        this.RegionInfo_Field.BillableFactor = unpackFloat(byteBuffer);
        this.RegionInfo_Field.CacheID = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase0 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase1 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase2 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainBase3 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail0 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail1 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail2 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainDetail3 = unpackUUID(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight00 = unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight01 = unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight10 = unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainStartHeight11 = unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange00 = unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange01 = unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange10 = unpackFloat(byteBuffer);
        this.RegionInfo_Field.TerrainHeightRange11 = unpackFloat(byteBuffer);
        this.RegionInfo2_Field.RegionID = unpackUUID(byteBuffer);
        this.RegionInfo3_Field.CPUClassID = unpackInt(byteBuffer);
        this.RegionInfo3_Field.CPURatio = unpackInt(byteBuffer);
        this.RegionInfo3_Field.ColoName = unpackVariable(byteBuffer, 1);
        this.RegionInfo3_Field.ProductSKU = unpackVariable(byteBuffer, 1);
        this.RegionInfo3_Field.ProductName = unpackVariable(byteBuffer, 1);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            RegionInfo4 regionInfo4 = new RegionInfo4();
            regionInfo4.RegionFlagsExtended = unpackLong(byteBuffer);
            regionInfo4.RegionProtocols = unpackLong(byteBuffer);
            this.RegionInfo4_Fields.add(regionInfo4);
        }
    }
}
