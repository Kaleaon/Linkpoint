// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EdgeDataPacket extends SLMessage
{
    public EdgeData EdgeData_Field;
    
    public EdgeDataPacket() {
        this.zeroCoded = true;
        this.EdgeData_Field = new EdgeData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.EdgeData_Field.LayerData.length + 4 + 1;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEdgeDataPacket(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)24);
        this.packByte(byteBuffer, (byte)this.EdgeData_Field.LayerType);
        this.packByte(byteBuffer, (byte)this.EdgeData_Field.Direction);
        this.packVariable(byteBuffer, this.EdgeData_Field.LayerData, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.EdgeData_Field.LayerType = (this.unpackByte(byteBuffer) & 0xFF);
        this.EdgeData_Field.Direction = (this.unpackByte(byteBuffer) & 0xFF);
        this.EdgeData_Field.LayerData = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class EdgeData
    {
        public int Direction;
        public byte[] LayerData;
        public int LayerType;
    }
}
