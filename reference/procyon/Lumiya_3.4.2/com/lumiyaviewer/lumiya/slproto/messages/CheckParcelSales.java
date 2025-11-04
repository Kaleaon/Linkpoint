// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CheckParcelSales extends SLMessage
{
    public ArrayList<RegionData> RegionData_Fields;
    
    public CheckParcelSales() {
        this.RegionData_Fields = new ArrayList<RegionData>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RegionData_Fields.size() * 8 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCheckParcelSales(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-31));
        byteBuffer.put((byte)this.RegionData_Fields.size());
        final Iterator<Object> iterator = this.RegionData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packLong(byteBuffer, iterator.next().RegionHandle);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RegionData e = new RegionData();
            e.RegionHandle = this.unpackLong(byteBuffer);
            this.RegionData_Fields.add(e);
        }
    }
    
    public static class RegionData
    {
        public long RegionHandle;
    }
}
