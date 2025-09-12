package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TransferInfo extends SLMessage {
    public TransferInfoData TransferInfoData_Field = new TransferInfoData();

    public static class TransferInfoData {
        public int ChannelType;
        public byte[] Params;
        public int Size;
        public int Status;
        public int TargetType;
        public UUID TransferID;
    }

    public TransferInfo() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.TransferInfoData_Field.Params.length + 34 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferInfo(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -102);
        packUUID(byteBuffer, this.TransferInfoData_Field.TransferID);
        packInt(byteBuffer, this.TransferInfoData_Field.ChannelType);
        packInt(byteBuffer, this.TransferInfoData_Field.TargetType);
        packInt(byteBuffer, this.TransferInfoData_Field.Status);
        packInt(byteBuffer, this.TransferInfoData_Field.Size);
        packVariable(byteBuffer, this.TransferInfoData_Field.Params, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransferInfoData_Field.TransferID = unpackUUID(byteBuffer);
        this.TransferInfoData_Field.ChannelType = unpackInt(byteBuffer);
        this.TransferInfoData_Field.TargetType = unpackInt(byteBuffer);
        this.TransferInfoData_Field.Status = unpackInt(byteBuffer);
        this.TransferInfoData_Field.Size = unpackInt(byteBuffer);
        this.TransferInfoData_Field.Params = unpackVariable(byteBuffer, 2);
    }
}
