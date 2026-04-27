package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class CloseCircuit extends SLMessage {
    public CloseCircuit() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCloseCircuit(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) -3);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
    }
}
