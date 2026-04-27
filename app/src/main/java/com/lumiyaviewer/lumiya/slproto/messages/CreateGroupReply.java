package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CreateGroupReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ReplyData ReplyData_Field = new ReplyData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class ReplyData {
        public UUID GroupID;
        public byte[] Message;
        public boolean Success;
    }

    public CreateGroupReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.ReplyData_Field.Message.length + 18 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateGroupReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 84);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.ReplyData_Field.GroupID);
        packBoolean(byteBuffer, this.ReplyData_Field.Success);
        packVariable(byteBuffer, this.ReplyData_Field.Message, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer);
        this.ReplyData_Field.Success = unpackBoolean(byteBuffer);
        this.ReplyData_Field.Message = unpackVariable(byteBuffer, 1);
    }
}
