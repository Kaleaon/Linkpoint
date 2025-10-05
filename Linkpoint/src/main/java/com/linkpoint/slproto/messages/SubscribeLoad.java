package com.linkpoint.slproto.messages;

import com.linkpoint.slproto.SLMessage;
import java.nio.ByteBuffer;

public class SubscribeLoad extends SLMessage {
    public SubscribeLoad() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSubscribeLoad(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 7);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
    }
}
