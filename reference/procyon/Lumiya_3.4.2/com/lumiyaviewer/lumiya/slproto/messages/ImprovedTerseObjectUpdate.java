// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ImprovedTerseObjectUpdate extends SLMessage
{
    public ArrayList<ObjectData> ObjectData_Fields;
    public RegionData RegionData_Field;
    
    public ImprovedTerseObjectUpdate() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = false;
        this.RegionData_Field = new RegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        int n = 12;
        while (iterator.hasNext()) {
            final ObjectData objectData = iterator.next();
            n += objectData.TextureEntry.length + (objectData.Data.length + 1 + 2);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleImprovedTerseObjectUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)15);
        this.packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        this.packShort(byteBuffer, (short)this.RegionData_Field.TimeDilation);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packVariable(byteBuffer, objectData.Data, 1);
            this.packVariable(byteBuffer, objectData.TextureEntry, 2);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.RegionData_Field.TimeDilation = (this.unpackShort(byteBuffer) & 0xFFFF);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.Data = this.unpackVariable(byteBuffer, 1);
            e.TextureEntry = this.unpackVariable(byteBuffer, 2);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class ObjectData
    {
        public byte[] Data;
        public byte[] TextureEntry;
    }
    
    public static class RegionData
    {
        public long RegionHandle;
        public int TimeDilation;
    }
}
