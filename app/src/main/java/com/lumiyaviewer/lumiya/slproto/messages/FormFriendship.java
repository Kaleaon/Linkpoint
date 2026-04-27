package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class FormFriendship extends SLMessage {
    public AgentBlock AgentBlock_Field = new AgentBlock();

    public static class AgentBlock {
        public UUID DestID;
        public UUID SourceID;
    }

    public FormFriendship() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFormFriendship(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 43);
        packUUID(byteBuffer, this.AgentBlock_Field.SourceID);
        packUUID(byteBuffer, this.AgentBlock_Field.DestID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentBlock_Field.SourceID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer);
    }
}
