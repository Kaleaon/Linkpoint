package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class FormFriendship extends SLMessage {
    public AgentBlock AgentBlock_Field;

    /* loaded from: classes.dex */
    public static class AgentBlock {
        public UUID DestID;
        public UUID SourceID;
    }

    public FormFriendship() {
        this.zeroCoded = false;
        this.AgentBlock_Field = new AgentBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFormFriendship(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 43);
        packUUID(byteBuffer, this.AgentBlock_Field.SourceID);
        packUUID(byteBuffer, this.AgentBlock_Field.DestID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentBlock_Field.SourceID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer);
    }
}
