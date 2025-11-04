// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GodUpdateRegionInfo extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<RegionInfo2> RegionInfo2_Fields;
    public RegionInfo RegionInfo_Field;
    
    public GodUpdateRegionInfo() {
        this.RegionInfo2_Fields = new ArrayList<RegionInfo2>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RegionInfo_Field = new RegionInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RegionInfo_Field.SimName.length + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 36 + 1 + this.RegionInfo2_Fields.size() * 8;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGodUpdateRegionInfo(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-113));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packVariable(byteBuffer, this.RegionInfo_Field.SimName, 1);
        this.packInt(byteBuffer, this.RegionInfo_Field.EstateID);
        this.packInt(byteBuffer, this.RegionInfo_Field.ParentEstateID);
        this.packInt(byteBuffer, this.RegionInfo_Field.RegionFlags);
        this.packFloat(byteBuffer, this.RegionInfo_Field.BillableFactor);
        this.packInt(byteBuffer, this.RegionInfo_Field.PricePerMeter);
        this.packInt(byteBuffer, this.RegionInfo_Field.RedirectGridX);
        this.packInt(byteBuffer, this.RegionInfo_Field.RedirectGridY);
        byteBuffer.put((byte)this.RegionInfo2_Fields.size());
        final Iterator<Object> iterator = this.RegionInfo2_Fields.iterator();
        while (iterator.hasNext()) {
            this.packLong(byteBuffer, iterator.next().RegionFlagsExtended);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.RegionInfo_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.RegionInfo_Field.EstateID = this.unpackInt(byteBuffer);
        this.RegionInfo_Field.ParentEstateID = this.unpackInt(byteBuffer);
        this.RegionInfo_Field.RegionFlags = this.unpackInt(byteBuffer);
        this.RegionInfo_Field.BillableFactor = this.unpackFloat(byteBuffer);
        this.RegionInfo_Field.PricePerMeter = this.unpackInt(byteBuffer);
        this.RegionInfo_Field.RedirectGridX = this.unpackInt(byteBuffer);
        this.RegionInfo_Field.RedirectGridY = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RegionInfo2 e = new RegionInfo2();
            e.RegionFlagsExtended = this.unpackLong(byteBuffer);
            this.RegionInfo2_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class RegionInfo
    {
        public float BillableFactor;
        public int EstateID;
        public int ParentEstateID;
        public int PricePerMeter;
        public int RedirectGridX;
        public int RedirectGridY;
        public int RegionFlags;
        public byte[] SimName;
    }
    
    public static class RegionInfo2
    {
        public long RegionFlagsExtended;
    }
}
