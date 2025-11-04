package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class TransferAbort extends SLMessage {
    public TransferInfo TransferInfo_Field;

    /* loaded from: classes.dex */
    public static class TransferInfo {
        public int ChannelType;
        public UUID TransferID;
    }

    public TransferAbort() {
        this.zeroCoded = true;
        this.TransferInfo_Field = new TransferInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 24;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferAbort(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -101);
        packUUID(byteBuffer, this.TransferInfo_Field.TransferID);
        packInt(byteBuffer, this.TransferInfo_Field.ChannelType);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = unpackUUID(byteBuffer);
        this.TransferInfo_Field.ChannelType = unpackInt(byteBuffer);
    }
}
