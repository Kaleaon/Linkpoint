package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class LiveHelpGroupReply extends SLMessage {
    public ReplyData ReplyData_Field;

    /* loaded from: classes.dex */
    public static class ReplyData {
        public UUID GroupID;
        public UUID RequestID;
        public byte[] Selection;
    }

    public LiveHelpGroupReply() {
        this.zeroCoded = false;
        this.ReplyData_Field = new ReplyData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.ReplyData_Field.Selection.length + 33 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLiveHelpGroupReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 124);
        packUUID(byteBuffer, this.ReplyData_Field.RequestID);
        packUUID(byteBuffer, this.ReplyData_Field.GroupID);
        packVariable(byteBuffer, this.ReplyData_Field.Selection, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyData_Field.RequestID = unpackUUID(byteBuffer);
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer);
        this.ReplyData_Field.Selection = unpackVariable(byteBuffer, 1);
    }
}
