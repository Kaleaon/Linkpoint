package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class TransferRequest extends SLMessage {
    public TransferInfo TransferInfo_Field;

    /* loaded from: classes.dex */
    public static class TransferInfo {
        public int ChannelType;
        public byte[] Params;
        public float Priority;
        public int SourceType;
        public UUID TransferID;
    }

    public TransferRequest() {
        this.zeroCoded = true;
        this.TransferInfo_Field = new TransferInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.TransferInfo_Field.Params.length + 30 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -103);
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID);
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType);
        packInt(byteBuffer, this.TransferInfo_Field.SourceType);
        packFloat(byteBuffer, this.TransferInfo_Field.Priority);
        packVariable(byteBuffer, this.TransferInfo_Field.Params, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer);
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer);
        this.TransferInfo_Field.SourceType = unpackInt(byteBuffer);
        this.TransferInfo_Field.Priority = unpackFloat(byteBuffer);
        this.TransferInfo_Field.Params = unpackVariable(byteBuffer, 2);
    }
}
