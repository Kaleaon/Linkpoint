// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LayerData extends SLMessage
{
    public LayerDataData LayerDataData_Field;
    public LayerID LayerID_Field;
    
    public LayerData() {
        this.zeroCoded = false;
        this.LayerID_Field = new LayerID();
        this.LayerDataData_Field = new LayerDataData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.LayerDataData_Field.Data.length + 2 + 2;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLayerData(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)11);
        this.packByte(byteBuffer, (byte)this.LayerID_Field.Type);
        this.packVariable(byteBuffer, this.LayerDataData_Field.Data, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.LayerID_Field.Type = (this.unpackByte(byteBuffer) & 0xFF);
        this.LayerDataData_Field.Data = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class LayerDataData
    {
        public byte[] Data;
    }
    
    public static class LayerID
    {
        public int Type;
    }
}
