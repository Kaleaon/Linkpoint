package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SimulatorMapUpdate extends SLMessage {
    public MapData MapData_Field;

    /* loaded from: classes.dex */
    public static class MapData {
        public int Flags;
    }

    public SimulatorMapUpdate() {
        this.zeroCoded = false;
        this.MapData_Field = new MapData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 8;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorMapUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 5);
        packInt(byteBuffer, this.MapData_Field.Flags);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MapData_Field.Flags = unpackInt(byteBuffer);
    }
}
