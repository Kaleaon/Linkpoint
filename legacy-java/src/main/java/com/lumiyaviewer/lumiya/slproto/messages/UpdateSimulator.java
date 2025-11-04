package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class UpdateSimulator extends SLMessage {
    public SimulatorInfo SimulatorInfo_Field;

    /* loaded from: classes.dex */
    public static class SimulatorInfo {
        public int EstateID;
        public UUID RegionID;
        public int SimAccess;
        public byte[] SimName;
    }

    public UpdateSimulator() {
        this.zeroCoded = false;
        this.SimulatorInfo_Field = new SimulatorInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.SimulatorInfo_Field.SimName.length + 17 + 4 + 1 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateSimulator(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 17);
        packUUID(byteBuffer, this.SimulatorInfo_Field.RegionID);
        packVariable(byteBuffer, this.SimulatorInfo_Field.SimName, 1);
        packInt(byteBuffer, this.SimulatorInfo_Field.EstateID);
        packByte(byteBuffer, (byte) this.SimulatorInfo_Field.SimAccess);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimulatorInfo_Field.RegionID = unpackUUID(byteBuffer);
        this.SimulatorInfo_Field.SimName = unpackVariable(byteBuffer, 1);
        this.SimulatorInfo_Field.EstateID = unpackInt(byteBuffer);
        this.SimulatorInfo_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
