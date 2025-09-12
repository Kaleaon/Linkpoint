package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RemoveAttachment extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public AttachmentBlock AttachmentBlock_Field = new AttachmentBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class AttachmentBlock {
        public int AttachmentPoint;
        public UUID ItemID;
    }

    public RemoveAttachment() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveAttachment(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 76);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packByte(byteBuffer, (byte) this.AttachmentBlock_Field.AttachmentPoint);
        packUUID(byteBuffer, this.AttachmentBlock_Field.ItemID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AttachmentBlock_Field.AttachmentPoint = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AttachmentBlock_Field.ItemID = unpackUUID(byteBuffer);
    }
}
