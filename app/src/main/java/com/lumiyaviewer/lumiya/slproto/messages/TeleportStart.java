package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class TeleportStart extends SLMessage {
    public Info Info_Field = new Info();

    public static class Info {
        public int TeleportFlags;
    }

    public TeleportStart() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 8;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportStart(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 73);
        packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer);
    }
}
