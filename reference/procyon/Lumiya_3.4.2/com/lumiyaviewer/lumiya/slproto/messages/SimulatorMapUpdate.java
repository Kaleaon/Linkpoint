// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimulatorMapUpdate extends SLMessage
{
    public MapData MapData_Field;
    
    public SimulatorMapUpdate() {
        this.zeroCoded = false;
        this.MapData_Field = new MapData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 8;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimulatorMapUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)5);
        this.packInt(byteBuffer, this.MapData_Field.Flags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.MapData_Field.Flags = this.unpackInt(byteBuffer);
    }
    
    public static class MapData
    {
        public int Flags;
    }
}
