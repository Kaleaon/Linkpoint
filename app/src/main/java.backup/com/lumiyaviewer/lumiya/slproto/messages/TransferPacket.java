package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TransferPacket extends SLMessage {
    public TransferData TransferData_Field = new TransferData();

    public static class TransferData {
        public int ChannelType;
        public byte[] Data;
        public int Packet;
        public int Status;
        public UUID TransferID;
    }

    public TransferPacket() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.TransferData_Field.Data.length + 30 + 1;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferPacket(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 17);
        packUUID(byteBuffer, this.TransferData_Field.TransferID);
        packInt(byteBuffer, this.TransferData_Field.ChannelType);
        packInt(byteBuffer, this.TransferData_Field.Packet);
        packInt(byteBuffer, this.TransferData_Field.Status);
        packVariable(byteBuffer, this.TransferData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferData_Field.TransferID = unpackUUID(byteBuffer);
        this.TransferData_Field.ChannelType = unpackInt(byteBuffer);
        this.TransferData_Field.Packet = unpackInt(byteBuffer);
        this.TransferData_Field.Status = unpackInt(byteBuffer);
        this.TransferData_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
