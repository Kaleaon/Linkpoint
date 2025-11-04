// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelRename extends SLMessage
{
    public ArrayList<ParcelData> ParcelData_Fields;
    
    public ParcelRename() {
        this.ParcelData_Fields = new ArrayList<ParcelData>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ParcelData_Fields.iterator();
        int n = 5;
        while (iterator.hasNext()) {
            n += iterator.next().NewName.length + 17;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelRename(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-110));
        byteBuffer.put((byte)this.ParcelData_Fields.size());
        for (final ParcelData parcelData : this.ParcelData_Fields) {
            this.packUUID(byteBuffer, parcelData.ParcelID);
            this.packVariable(byteBuffer, parcelData.NewName, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ParcelData e = new ParcelData();
            e.ParcelID = this.unpackUUID(byteBuffer);
            e.NewName = this.unpackVariable(byteBuffer, 1);
            this.ParcelData_Fields.add(e);
        }
    }
    
    public static class ParcelData
    {
        public byte[] NewName;
        public UUID ParcelID;
    }
}
