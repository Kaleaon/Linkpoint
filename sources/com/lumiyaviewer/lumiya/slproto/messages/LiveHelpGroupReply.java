package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class LiveHelpGroupReply extends SLMessage {
    public ReplyData ReplyData_Field = new ReplyData();

    public static class ReplyData {
        public UUID GroupID;
        public UUID RequestID;
        public byte[] Selection;
    }

    public LiveHelpGroupReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.ReplyData_Field.Selection.length + 33 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLiveHelpGroupReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 124);
        packUUID(byteBuffer, this.ReplyData_Field.RequestID);
        packUUID(byteBuffer, this.ReplyData_Field.GroupID);
        packVariable(byteBuffer, this.ReplyData_Field.Selection, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyData_Field.RequestID = unpackUUID(byteBuffer);
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer);
        this.ReplyData_Field.Selection = unpackVariable(byteBuffer, 1);
    }
}
