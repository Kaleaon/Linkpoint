// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RegionPresenceRequestByRegionID extends SLMessage
{
    public ArrayList<RegionData> RegionData_Fields;
    
    public RegionPresenceRequestByRegionID() {
        this.RegionData_Fields = new ArrayList<RegionData>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RegionData_Fields.size() * 16 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRegionPresenceRequestByRegionID(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)14);
        byteBuffer.put((byte)this.RegionData_Fields.size());
        final Iterator<Object> iterator = this.RegionData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().RegionID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RegionData e = new RegionData();
            e.RegionID = this.unpackUUID(byteBuffer);
            this.RegionData_Fields.add(e);
        }
    }
    
    public static class RegionData
    {
        public UUID RegionID;
    }
}
