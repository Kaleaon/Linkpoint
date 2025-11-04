// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateSimulator extends SLMessage
{
    public SimulatorInfo SimulatorInfo_Field;
    
    public UpdateSimulator() {
        this.zeroCoded = false;
        this.SimulatorInfo_Field = new SimulatorInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.SimulatorInfo_Field.SimName.length + 17 + 4 + 1 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateSimulator(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)17);
        this.packUUID(byteBuffer, this.SimulatorInfo_Field.RegionID);
        this.packVariable(byteBuffer, this.SimulatorInfo_Field.SimName, 1);
        this.packInt(byteBuffer, this.SimulatorInfo_Field.EstateID);
        this.packByte(byteBuffer, (byte)this.SimulatorInfo_Field.SimAccess);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SimulatorInfo_Field.RegionID = this.unpackUUID(byteBuffer);
        this.SimulatorInfo_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.SimulatorInfo_Field.EstateID = this.unpackInt(byteBuffer);
        this.SimulatorInfo_Field.SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class SimulatorInfo
    {
        public int EstateID;
        public UUID RegionID;
        public int SimAccess;
        public byte[] SimName;
    }
}
