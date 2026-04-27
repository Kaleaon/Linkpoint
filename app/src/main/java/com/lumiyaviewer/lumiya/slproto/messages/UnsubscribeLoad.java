package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class UnsubscribeLoad extends SLMessage {
    public UnsubscribeLoad() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUnsubscribeLoad(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 8);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
    }
}
