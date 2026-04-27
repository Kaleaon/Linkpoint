package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SimulatorSetMap extends SLMessage {
    public MapData MapData_Field = new MapData();

    public static class MapData {
        public UUID MapImage;
        public long RegionHandle;
        public int Type;
    }

    public SimulatorSetMap() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 32;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorSetMap(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 6);
        packLong(byteBuffer, this.MapData_Field.RegionHandle);
        packInt(byteBuffer, this.MapData_Field.Type);
        packUUID(byteBuffer, this.MapData_Field.MapImage);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MapData_Field.RegionHandle = unpackLong(byteBuffer);
        this.MapData_Field.Type = unpackInt(byteBuffer);
        this.MapData_Field.MapImage = unpackUUID(byteBuffer);
    }
}
