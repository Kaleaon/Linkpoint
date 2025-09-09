package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class GroupNoticeAdd extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public MessageBlock MessageBlock_Field = new MessageBlock();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class MessageBlock {
        public byte[] BinaryBucket;
        public int Dialog;
        public byte[] FromAgentName;
        public UUID ID;
        public byte[] Message;
        public UUID ToGroupID;
    }

    public GroupNoticeAdd() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.MessageBlock_Field.FromAgentName.length + 34 + 2 + this.MessageBlock_Field.Message.length + 2 + this.MessageBlock_Field.BinaryBucket.length + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupNoticeAdd(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 61);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.MessageBlock_Field.ToGroupID);
        packUUID(byteBuffer, this.MessageBlock_Field.ID);
        packByte(byteBuffer, (byte) this.MessageBlock_Field.Dialog);
        packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1);
        packVariable(byteBuffer, this.MessageBlock_Field.Message, 2);
        packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.MessageBlock_Field.ToGroupID = unpackUUID(byteBuffer);
        this.MessageBlock_Field.ID = unpackUUID(byteBuffer);
        this.MessageBlock_Field.Dialog = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MessageBlock_Field.FromAgentName = unpackVariable(byteBuffer, 1);
        this.MessageBlock_Field.Message = unpackVariable(byteBuffer, 2);
        this.MessageBlock_Field.BinaryBucket = unpackVariable(byteBuffer, 2);
    }
}
