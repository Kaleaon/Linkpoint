// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelAuctions extends SLMessage
{
    public ArrayList<ParcelData> ParcelData_Fields;
    
    public ParcelAuctions() {
        this.ParcelData_Fields = new ArrayList<ParcelData>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Fields.size() * 32 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelAuctions(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-22));
        byteBuffer.put((byte)this.ParcelData_Fields.size());
        for (final ParcelData parcelData : this.ParcelData_Fields) {
            this.packUUID(byteBuffer, parcelData.ParcelID);
            this.packUUID(byteBuffer, parcelData.WinnerID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ParcelData e = new ParcelData();
            e.ParcelID = this.unpackUUID(byteBuffer);
            e.WinnerID = this.unpackUUID(byteBuffer);
            this.ParcelData_Fields.add(e);
        }
    }
    
    public static class ParcelData
    {
        public UUID ParcelID;
        public UUID WinnerID;
    }
}
