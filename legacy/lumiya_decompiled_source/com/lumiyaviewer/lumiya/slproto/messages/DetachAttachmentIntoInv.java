package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class DetachAttachmentIntoInv extends SLMessage {
    public ObjectData ObjectData_Field;

    /* loaded from: classes.dex */
    public static class ObjectData {
        public UUID AgentID;
        public UUID ItemID;
    }

    public DetachAttachmentIntoInv() {
        this.zeroCoded = false;
        this.ObjectData_Field = new ObjectData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDetachAttachmentIntoInv(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -115);
        packUUID(byteBuffer, this.ObjectData_Field.AgentID);
        packUUID(byteBuffer, this.ObjectData_Field.ItemID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.AgentID = unpackUUID(byteBuffer);
        this.ObjectData_Field.ItemID = unpackUUID(byteBuffer);
    }
}
