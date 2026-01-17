package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class RemoveAttachment extends SLMessage {
    public AgentData AgentData_Field;
    public AttachmentBlock AttachmentBlock_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class AttachmentBlock {
        public int AttachmentPoint;
        public UUID ItemID;
    }

    public RemoveAttachment() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.AttachmentBlock_Field = new AttachmentBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 53;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveAttachment(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 76);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packByte(byteBuffer, (byte) this.AttachmentBlock_Field.AttachmentPoint);
        packUUID(byteBuffer, this.AttachmentBlock_Field.ItemID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AttachmentBlock_Field.AttachmentPoint = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AttachmentBlock_Field.ItemID = unpackUUID(byteBuffer);
    }
}
