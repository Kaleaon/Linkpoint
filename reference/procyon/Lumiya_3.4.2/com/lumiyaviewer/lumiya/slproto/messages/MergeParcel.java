// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MergeParcel extends SLMessage
{
    public MasterParcelData MasterParcelData_Field;
    public ArrayList<SlaveParcelData> SlaveParcelData_Fields;
    
    public MergeParcel() {
        this.SlaveParcelData_Fields = new ArrayList<SlaveParcelData>();
        this.zeroCoded = false;
        this.MasterParcelData_Field = new MasterParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.SlaveParcelData_Fields.size() * 16 + 21;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMergeParcel(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-33));
        this.packUUID(byteBuffer, this.MasterParcelData_Field.MasterID);
        byteBuffer.put((byte)this.SlaveParcelData_Fields.size());
        final Iterator<Object> iterator = this.SlaveParcelData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().SlaveID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.MasterParcelData_Field.MasterID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final SlaveParcelData e = new SlaveParcelData();
            e.SlaveID = this.unpackUUID(byteBuffer);
            this.SlaveParcelData_Fields.add(e);
        }
    }
    
    public static class MasterParcelData
    {
        public UUID MasterID;
    }
    
    public static class SlaveParcelData
    {
        public UUID SlaveID;
    }
}
