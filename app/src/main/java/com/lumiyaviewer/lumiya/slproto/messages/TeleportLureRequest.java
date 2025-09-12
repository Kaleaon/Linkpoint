package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TeleportLureRequest extends SLMessage {
    public Info Info_Field = new Info();

    public static class Info {
        public UUID AgentID;
        public UUID LureID;
        public UUID SessionID;
        public int TeleportFlags;
    }

    public TeleportLureRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 56;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLureRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 71);
        packUUID(byteBuffer, this.Info_Field.AgentID);
        packUUID(byteBuffer, this.Info_Field.SessionID);
        packUUID(byteBuffer, this.Info_Field.LureID);
        packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer);
        this.Info_Field.SessionID = unpackUUID(byteBuffer);
        this.Info_Field.LureID = unpackUUID(byteBuffer);
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer);
    }
}
