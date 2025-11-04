package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class CreateGroupReply extends SLMessage {
    public AgentData AgentData_Field;
    public ReplyData ReplyData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class ReplyData {
        public UUID GroupID;
        public byte[] Message;
        public boolean Success;
    }

    public CreateGroupReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ReplyData_Field = new ReplyData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.ReplyData_Field.Message.length + 18 + 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateGroupReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 84);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.ReplyData_Field.GroupID);
        packBoolean(byteBuffer, this.ReplyData_Field.Success);
        packVariable(byteBuffer, this.ReplyData_Field.Message, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer);
        this.ReplyData_Field.Success = unpackBoolean(byteBuffer);
        this.ReplyData_Field.Message = unpackVariable(byteBuffer, 1);
    }
}
