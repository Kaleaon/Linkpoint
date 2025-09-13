package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TeleportCancel extends SLMessage {
    public Info Info_Field = new Info();

    public static class Info {
        public UUID AgentID;
        public UUID SessionID;
    }

    public TeleportCancel() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportCancel(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 72);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packUUID(byteBuffer, this.Info_Field.SessionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.SessionID = unpackUUID(byteBuffer);
    }
}
