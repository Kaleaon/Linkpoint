// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimulatorReady extends SLMessage
{
    public SimulatorBlock SimulatorBlock_Field;
    public TelehubBlock TelehubBlock_Field;
    
    public SimulatorReady() {
        this.zeroCoded = true;
        this.SimulatorBlock_Field = new SimulatorBlock();
        this.TelehubBlock_Field = new TelehubBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 4 + 13;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimulatorReady(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)9);
        this.packVariable(byteBuffer, this.SimulatorBlock_Field.SimName, 1);
        this.packByte(byteBuffer, (byte)this.SimulatorBlock_Field.SimAccess);
        this.packInt(byteBuffer, this.SimulatorBlock_Field.RegionFlags);
        this.packUUID(byteBuffer, this.SimulatorBlock_Field.RegionID);
        this.packInt(byteBuffer, this.SimulatorBlock_Field.EstateID);
        this.packInt(byteBuffer, this.SimulatorBlock_Field.ParentEstateID);
        this.packBoolean(byteBuffer, this.TelehubBlock_Field.HasTelehub);
        this.packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SimulatorBlock_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.SimulatorBlock_Field.SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
        this.SimulatorBlock_Field.RegionFlags = this.unpackInt(byteBuffer);
        this.SimulatorBlock_Field.RegionID = this.unpackUUID(byteBuffer);
        this.SimulatorBlock_Field.EstateID = this.unpackInt(byteBuffer);
        this.SimulatorBlock_Field.ParentEstateID = this.unpackInt(byteBuffer);
        this.TelehubBlock_Field.HasTelehub = this.unpackBoolean(byteBuffer);
        this.TelehubBlock_Field.TelehubPos = this.unpackLLVector3(byteBuffer);
    }
    
    public static class SimulatorBlock
    {
        public int EstateID;
        public int ParentEstateID;
        public int RegionFlags;
        public UUID RegionID;
        public int SimAccess;
        public byte[] SimName;
    }
    
    public static class TelehubBlock
    {
        public boolean HasTelehub;
        public LLVector3 TelehubPos;
    }
}
