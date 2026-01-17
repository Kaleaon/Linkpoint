package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class TransferPacket extends SLMessage {
    public TransferData TransferData_Field;

    /* loaded from: classes.dex */
    public static class TransferData {
        public int ChannelType;
        public byte[] Data;
        public int Packet;
        public int Status;
        public UUID TransferID;
    }

    public TransferPacket() {
        this.zeroCoded = false;
        this.TransferData_Field = new TransferData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.TransferData_Field.Data.length + 30 + 1;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferPacket(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 17);
        packUUID(byteBuffer, this.TransferData_Field.TransferID);
        packInt(byteBuffer, this.TransferData_Field.ChannelType);
        packInt(byteBuffer, this.TransferData_Field.Packet);
        packInt(byteBuffer, this.TransferData_Field.Status);
        packVariable(byteBuffer, this.TransferData_Field.Data, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferData_Field.TransferID = unpackUUID(byteBuffer);
        this.TransferData_Field.ChannelType = unpackInt(byteBuffer);
        this.TransferData_Field.Packet = unpackInt(byteBuffer);
        this.TransferData_Field.Status = unpackInt(byteBuffer);
        this.TransferData_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
