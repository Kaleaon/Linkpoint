package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class RegionInfo extends SLMessage {
    public AgentData AgentData_Field;
    public RegionInfo2 RegionInfo2_Field;
    public ArrayList<RegionInfo3> RegionInfo3_Fields = new ArrayList<>();
    public RegionInfoData RegionInfoData_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class RegionInfo2 {
        public int HardMaxAgents;
        public int HardMaxObjects;
        public int MaxAgents32;
        public byte[] ProductName;
        public byte[] ProductSKU;
    }

    public static class RegionInfo3 {
        public long RegionFlagsExtended;
    }

    public static class RegionInfoData {
        public float BillableFactor;
        public int EstateID;
        public int MaxAgents;
        public float ObjectBonusFactor;
        public int ParentEstateID;
        public int PricePerMeter;
        public int RedirectGridX;
        public int RedirectGridY;
        public int RegionFlags;
        public int SimAccess;
        public byte[] SimName;
        public float SunHour;
        public float TerrainLowerLimit;
        public float TerrainRaiseLimit;
        public boolean UseEstateSun;
        public float WaterHeight;
    }

    public RegionInfo() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RegionInfoData_Field = new RegionInfoData();
        this.RegionInfo2_Field = new RegionInfo2();
    }

    public int CalcPayloadSize() {
        return this.RegionInfoData_Field.SimName.length + 1 + 4 + 4 + 4 + 1 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + 4 + 36 + this.RegionInfo2_Field.ProductSKU.length + 1 + 1 + this.RegionInfo2_Field.ProductName.length + 4 + 4 + 4 + 1 + (this.RegionInfo3_Fields.size() * 8);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionInfo(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -114);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packVariable(byteBuffer, this.RegionInfoData_Field.SimName, 1);
        packInt(byteBuffer, this.RegionInfoData_Field.EstateID);
        packInt(byteBuffer, this.RegionInfoData_Field.ParentEstateID);
        packInt(byteBuffer, this.RegionInfoData_Field.RegionFlags);
        packByte(byteBuffer, (byte) this.RegionInfoData_Field.SimAccess);
        packByte(byteBuffer, (byte) this.RegionInfoData_Field.MaxAgents);
        packFloat(byteBuffer, this.RegionInfoData_Field.BillableFactor);
        packFloat(byteBuffer, this.RegionInfoData_Field.ObjectBonusFactor);
        packFloat(byteBuffer, this.RegionInfoData_Field.WaterHeight);
        packFloat(byteBuffer, this.RegionInfoData_Field.TerrainRaiseLimit);
        packFloat(byteBuffer, this.RegionInfoData_Field.TerrainLowerLimit);
        packInt(byteBuffer, this.RegionInfoData_Field.PricePerMeter);
        packInt(byteBuffer, this.RegionInfoData_Field.RedirectGridX);
        packInt(byteBuffer, this.RegionInfoData_Field.RedirectGridY);
        packBoolean(byteBuffer, this.RegionInfoData_Field.UseEstateSun);
        packFloat(byteBuffer, this.RegionInfoData_Field.SunHour);
        packVariable(byteBuffer, this.RegionInfo2_Field.ProductSKU, 1);
        packVariable(byteBuffer, this.RegionInfo2_Field.ProductName, 1);
        packInt(byteBuffer, this.RegionInfo2_Field.MaxAgents32);
        packInt(byteBuffer, this.RegionInfo2_Field.HardMaxAgents);
        packInt(byteBuffer, this.RegionInfo2_Field.HardMaxObjects);
        byteBuffer.put((byte) this.RegionInfo3_Fields.size());
        for (RegionInfo3 regionInfo3 : this.RegionInfo3_Fields) {
            packLong(byteBuffer, regionInfo3.RegionFlagsExtended);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.RegionInfoData_Field.SimName = unpackVariable(byteBuffer, 1);
        this.RegionInfoData_Field.EstateID = unpackInt(byteBuffer);
        this.RegionInfoData_Field.ParentEstateID = unpackInt(byteBuffer);
        this.RegionInfoData_Field.RegionFlags = unpackInt(byteBuffer);
        this.RegionInfoData_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.RegionInfoData_Field.MaxAgents = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.RegionInfoData_Field.BillableFactor = unpackFloat(byteBuffer);
        this.RegionInfoData_Field.ObjectBonusFactor = unpackFloat(byteBuffer);
        this.RegionInfoData_Field.WaterHeight = unpackFloat(byteBuffer);
        this.RegionInfoData_Field.TerrainRaiseLimit = unpackFloat(byteBuffer);
        this.RegionInfoData_Field.TerrainLowerLimit = unpackFloat(byteBuffer);
        this.RegionInfoData_Field.PricePerMeter = unpackInt(byteBuffer);
        this.RegionInfoData_Field.RedirectGridX = unpackInt(byteBuffer);
        this.RegionInfoData_Field.RedirectGridY = unpackInt(byteBuffer);
        this.RegionInfoData_Field.UseEstateSun = unpackBoolean(byteBuffer);
        this.RegionInfoData_Field.SunHour = unpackFloat(byteBuffer);
        this.RegionInfo2_Field.ProductSKU = unpackVariable(byteBuffer, 1);
        this.RegionInfo2_Field.ProductName = unpackVariable(byteBuffer, 1);
        this.RegionInfo2_Field.MaxAgents32 = unpackInt(byteBuffer);
        this.RegionInfo2_Field.HardMaxAgents = unpackInt(byteBuffer);
        this.RegionInfo2_Field.HardMaxObjects = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            RegionInfo3 regionInfo3 = new RegionInfo3();
            regionInfo3.RegionFlagsExtended = unpackLong(byteBuffer);
            this.RegionInfo3_Fields.add(regionInfo3);
        }
    }
}
