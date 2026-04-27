// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RegionInfo extends SLMessage
{
    public AgentData AgentData_Field;
    public RegionInfo2 RegionInfo2_Field;
    public ArrayList<RegionInfo3> RegionInfo3_Fields;
    public RegionInfoData RegionInfoData_Field;
    
    public RegionInfo() {
        this.RegionInfo3_Fields = new ArrayList<RegionInfo3>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RegionInfoData_Field = new RegionInfoData();
        this.RegionInfo2_Field = new RegionInfo2();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RegionInfoData_Field.SimName.length + 1 + 4 + 4 + 4 + 1 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + 4 + 36 + (this.RegionInfo2_Field.ProductSKU.length + 1 + 1 + this.RegionInfo2_Field.ProductName.length + 4 + 4 + 4) + 1 + this.RegionInfo3_Fields.size() * 8;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRegionInfo(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-114));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packVariable(byteBuffer, this.RegionInfoData_Field.SimName, 1);
        this.packInt(byteBuffer, this.RegionInfoData_Field.EstateID);
        this.packInt(byteBuffer, this.RegionInfoData_Field.ParentEstateID);
        this.packInt(byteBuffer, this.RegionInfoData_Field.RegionFlags);
        this.packByte(byteBuffer, (byte)this.RegionInfoData_Field.SimAccess);
        this.packByte(byteBuffer, (byte)this.RegionInfoData_Field.MaxAgents);
        this.packFloat(byteBuffer, this.RegionInfoData_Field.BillableFactor);
        this.packFloat(byteBuffer, this.RegionInfoData_Field.ObjectBonusFactor);
        this.packFloat(byteBuffer, this.RegionInfoData_Field.WaterHeight);
        this.packFloat(byteBuffer, this.RegionInfoData_Field.TerrainRaiseLimit);
        this.packFloat(byteBuffer, this.RegionInfoData_Field.TerrainLowerLimit);
        this.packInt(byteBuffer, this.RegionInfoData_Field.PricePerMeter);
        this.packInt(byteBuffer, this.RegionInfoData_Field.RedirectGridX);
        this.packInt(byteBuffer, this.RegionInfoData_Field.RedirectGridY);
        this.packBoolean(byteBuffer, this.RegionInfoData_Field.UseEstateSun);
        this.packFloat(byteBuffer, this.RegionInfoData_Field.SunHour);
        this.packVariable(byteBuffer, this.RegionInfo2_Field.ProductSKU, 1);
        this.packVariable(byteBuffer, this.RegionInfo2_Field.ProductName, 1);
        this.packInt(byteBuffer, this.RegionInfo2_Field.MaxAgents32);
        this.packInt(byteBuffer, this.RegionInfo2_Field.HardMaxAgents);
        this.packInt(byteBuffer, this.RegionInfo2_Field.HardMaxObjects);
        byteBuffer.put((byte)this.RegionInfo3_Fields.size());
        final Iterator<Object> iterator = this.RegionInfo3_Fields.iterator();
        while (iterator.hasNext()) {
            this.packLong(byteBuffer, iterator.next().RegionFlagsExtended);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.RegionInfoData_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.RegionInfoData_Field.EstateID = this.unpackInt(byteBuffer);
        this.RegionInfoData_Field.ParentEstateID = this.unpackInt(byteBuffer);
        this.RegionInfoData_Field.RegionFlags = this.unpackInt(byteBuffer);
        this.RegionInfoData_Field.SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
        this.RegionInfoData_Field.MaxAgents = (this.unpackByte(byteBuffer) & 0xFF);
        this.RegionInfoData_Field.BillableFactor = this.unpackFloat(byteBuffer);
        this.RegionInfoData_Field.ObjectBonusFactor = this.unpackFloat(byteBuffer);
        this.RegionInfoData_Field.WaterHeight = this.unpackFloat(byteBuffer);
        this.RegionInfoData_Field.TerrainRaiseLimit = this.unpackFloat(byteBuffer);
        this.RegionInfoData_Field.TerrainLowerLimit = this.unpackFloat(byteBuffer);
        this.RegionInfoData_Field.PricePerMeter = this.unpackInt(byteBuffer);
        this.RegionInfoData_Field.RedirectGridX = this.unpackInt(byteBuffer);
        this.RegionInfoData_Field.RedirectGridY = this.unpackInt(byteBuffer);
        this.RegionInfoData_Field.UseEstateSun = this.unpackBoolean(byteBuffer);
        this.RegionInfoData_Field.SunHour = this.unpackFloat(byteBuffer);
        this.RegionInfo2_Field.ProductSKU = this.unpackVariable(byteBuffer, 1);
        this.RegionInfo2_Field.ProductName = this.unpackVariable(byteBuffer, 1);
        this.RegionInfo2_Field.MaxAgents32 = this.unpackInt(byteBuffer);
        this.RegionInfo2_Field.HardMaxAgents = this.unpackInt(byteBuffer);
        this.RegionInfo2_Field.HardMaxObjects = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RegionInfo3 e = new RegionInfo3();
            e.RegionFlagsExtended = this.unpackLong(byteBuffer);
            this.RegionInfo3_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class RegionInfo2
    {
        public int HardMaxAgents;
        public int HardMaxObjects;
        public int MaxAgents32;
        public byte[] ProductName;
        public byte[] ProductSKU;
    }
    
    public static class RegionInfo3
    {
        public long RegionFlagsExtended;
    }
    
    public static class RegionInfoData
    {
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
}
