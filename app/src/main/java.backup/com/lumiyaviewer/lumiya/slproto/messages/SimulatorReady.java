package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SimulatorReady extends SLMessage {
    public SimulatorBlock SimulatorBlock_Field = new SimulatorBlock();
    public TelehubBlock TelehubBlock_Field = new TelehubBlock();

    public static class SimulatorBlock {
        public int EstateID;
        public int ParentEstateID;
        public int RegionFlags;
        public UUID RegionID;
        public int SimAccess;
        public byte[] SimName;
    }

    public static class TelehubBlock {
        public boolean HasTelehub;
        public LLVector3 TelehubPos;
    }

    public SimulatorReady() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.SimulatorBlock_Field.SimName.length + 1 + 1 + 4 + 16 + 4 + 4 + 4 + 13;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorReady(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 9);
        packVariable(byteBuffer, this.SimulatorBlock_Field.SimName, 1);
        packByte(byteBuffer, (byte) this.SimulatorBlock_Field.SimAccess);
        packInt(byteBuffer, this.SimulatorBlock_Field.RegionFlags);
        packUUID(byteBuffer, this.SimulatorBlock_Field.RegionID);
        packInt(byteBuffer, this.SimulatorBlock_Field.EstateID);
        packInt(byteBuffer, this.SimulatorBlock_Field.ParentEstateID);
        packBoolean(byteBuffer, this.TelehubBlock_Field.HasTelehub);
        packLLVector3(byteBuffer, this.TelehubBlock_Field.TelehubPos);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorBlock_Field.SimName = unpackVariable(byteBuffer, 1);
        this.SimulatorBlock_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.SimulatorBlock_Field.RegionFlags = unpackInt(byteBuffer);
        this.SimulatorBlock_Field.RegionID = unpackUUID(byteBuffer);
        this.SimulatorBlock_Field.EstateID = unpackInt(byteBuffer);
        this.SimulatorBlock_Field.ParentEstateID = unpackInt(byteBuffer);
        this.TelehubBlock_Field.HasTelehub = unpackBoolean(byteBuffer);
        this.TelehubBlock_Field.TelehubPos = unpackLLVector3(byteBuffer);
    }
}
