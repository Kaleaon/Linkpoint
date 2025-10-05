package com.linkpoint.slproto;

import com.linkpoint.slproto.messages.SLMessageHandler;
import java.nio.ByteBuffer;

public class SLDefaultMessage extends SLMessage {
    public int CalcPayloadSize() {
        return 0;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.DefaultMessageHandler(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
    }
}
