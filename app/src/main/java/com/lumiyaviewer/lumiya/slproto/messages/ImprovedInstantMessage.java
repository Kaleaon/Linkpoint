package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ImprovedInstantMessage extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public MessageBlock MessageBlock_Field = new MessageBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class MessageBlock {
        public byte[] BinaryBucket;
        public int Dialog;
        public byte[] FromAgentName;
        public boolean FromGroup;
        public UUID ID;
        public byte[] Message;
        public int Offline;
        public int ParentEstateID;
        public LLVector3 Position;
        public UUID RegionID;
        public int Timestamp;
        public UUID ToAgentID;
    }

    public ImprovedInstantMessage() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.MessageBlock_Field.FromAgentName.length + 72 + 2 + this.MessageBlock_Field.Message.length + 2 + this.MessageBlock_Field.BinaryBucket.length + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImprovedInstantMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -2);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packBoolean(byteBuffer, this.MessageBlock_Field.FromGroup);
        packUUID(byteBuffer, this.MessageBlock_Field.ToAgentID);
        packInt(byteBuffer, this.MessageBlock_Field.ParentEstateID);
        packUUID(byteBuffer, this.MessageBlock_Field.RegionID);
        packLLVector3(byteBuffer, this.MessageBlock_Field.Position);
        packByte(byteBuffer, (byte) this.MessageBlock_Field.Offline);
        packByte(byteBuffer, (byte) this.MessageBlock_Field.Dialog);
        packUUID(byteBuffer, this.MessageBlock_Field.ID);
        packInt(byteBuffer, this.MessageBlock_Field.Timestamp);
        packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1);
        packVariable(byteBuffer, this.MessageBlock_Field.Message, 2);
        packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.MessageBlock_Field.FromGroup = unpackBoolean(byteBuffer);
        this.MessageBlock_Field.ToAgentID = unpackUUID(byteBuffer);
        this.MessageBlock_Field.ParentEstateID = unpackInt(byteBuffer);
        this.MessageBlock_Field.RegionID = unpackUUID(byteBuffer);
        this.MessageBlock_Field.Position = unpackLLVector3(byteBuffer);
        this.MessageBlock_Field.Offline = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MessageBlock_Field.Dialog = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.MessageBlock_Field.ID = unpackUUID(byteBuffer);
        this.MessageBlock_Field.Timestamp = unpackInt(byteBuffer);
        this.MessageBlock_Field.FromAgentName = unpackVariable(byteBuffer, 1);
        this.MessageBlock_Field.Message = unpackVariable(byteBuffer, 2);
        this.MessageBlock_Field.BinaryBucket = unpackVariable(byteBuffer, 2);
    }
}
