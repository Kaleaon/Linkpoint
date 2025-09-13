package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DetachAttachmentIntoInv extends SLMessage {
    public ObjectData ObjectData_Field = new ObjectData();

    public static class ObjectData {
        public UUID AgentID;
        public UUID ItemID;
    }

    public DetachAttachmentIntoInv() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDetachAttachmentIntoInv(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -115);
        packUUID(byteBuffer, this.ObjectData_Field.AgentID);
        packUUID(byteBuffer, this.ObjectData_Field.ItemID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.AgentID = unpackUUID(byteBuffer);
        this.ObjectData_Field.ItemID = unpackUUID(byteBuffer);
    }
}
