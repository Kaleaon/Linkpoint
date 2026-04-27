// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimulatorSetMap extends SLMessage
{
    public MapData MapData_Field;
    
    public SimulatorSetMap() {
        this.zeroCoded = false;
        this.MapData_Field = new MapData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 32;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimulatorSetMap(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)6);
        this.packLong(byteBuffer, this.MapData_Field.RegionHandle);
        this.packInt(byteBuffer, this.MapData_Field.Type);
        this.packUUID(byteBuffer, this.MapData_Field.MapImage);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.MapData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.MapData_Field.Type = this.unpackInt(byteBuffer);
        this.MapData_Field.MapImage = this.unpackUUID(byteBuffer);
    }
    
    public static class MapData
    {
        public UUID MapImage;
        public long RegionHandle;
        public int Type;
    }
}
