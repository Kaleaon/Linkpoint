// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelOverlay extends SLMessage
{
    public ParcelData ParcelData_Field;
    
    public ParcelOverlay() {
        this.zeroCoded = true;
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Field.Data.length + 6 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelOverlay(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-60));
        this.packInt(byteBuffer, this.ParcelData_Field.SequenceID);
        this.packVariable(byteBuffer, this.ParcelData_Field.Data, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ParcelData_Field.SequenceID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.Data = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class ParcelData
    {
        public byte[] Data;
        public int SequenceID;
    }
}
