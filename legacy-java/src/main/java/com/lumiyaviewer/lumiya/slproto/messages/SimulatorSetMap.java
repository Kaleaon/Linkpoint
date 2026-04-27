package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class SimulatorSetMap extends SLMessage {
    public MapData MapData_Field;

    /* loaded from: classes.dex */
    public static class MapData {
        public UUID MapImage;
        public long RegionHandle;
        public int Type;
    }

    public SimulatorSetMap() {
        this.zeroCoded = false;
        this.MapData_Field = new MapData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 32;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorSetMap(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 6);
        packLong(byteBuffer, this.MapData_Field.RegionHandle);
        packInt(byteBuffer, this.MapData_Field.Type);
        packUUID(byteBuffer, this.MapData_Field.MapImage);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MapData_Field.RegionHandle = unpackLong(byteBuffer);
        this.MapData_Field.Type = unpackInt(byteBuffer);
        this.MapData_Field.MapImage = unpackUUID(byteBuffer);
    }
}
