package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class SimulatorShutdownRequest extends SLMessage {
    public SimulatorShutdownRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorShutdownRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.CR);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
    }
}
