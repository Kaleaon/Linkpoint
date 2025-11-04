// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectUpdateCached extends SLMessage
{
    public ArrayList<ObjectData> ObjectData_Fields;
    public RegionData RegionData_Field;
    
    public ObjectUpdateCached() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = false;
        this.RegionData_Field = new RegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Fields.size() * 12 + 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectUpdateCached(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)14);
        this.packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        this.packShort(byteBuffer, (short)this.RegionData_Field.TimeDilation);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packInt(byteBuffer, objectData.ID);
            this.packInt(byteBuffer, objectData.CRC);
            this.packInt(byteBuffer, objectData.UpdateFlags);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.RegionData_Field.TimeDilation = (this.unpackShort(byteBuffer) & 0xFFFF);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ID = this.unpackInt(byteBuffer);
            e.CRC = this.unpackInt(byteBuffer);
            e.UpdateFlags = this.unpackInt(byteBuffer);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class ObjectData
    {
        public int CRC;
        public int ID;
        public int UpdateFlags;
    }
    
    public static class RegionData
    {
        public long RegionHandle;
        public int TimeDilation;
    }
}
