package com.lumiyaviewer.lumiya.slproto.messages;
import java.util.*;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class SimulatorMapUpdate extends SLMessage {
    public MapData MapData_Field = new MapData();

    public static class MapData {
        public int Flags;
    }

    public SimulatorMapUpdate() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 8;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorMapUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 5);
        packInt(byteBuffer, this.MapData_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MapData_Field.Flags = unpackInt(byteBuffer);
    }
}
