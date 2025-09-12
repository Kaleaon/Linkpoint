package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RequestParcelTransfer extends SLMessage {
    public Data Data_Field = new Data();
    public RegionData RegionData_Field = new RegionData();

    public static class Data {
        public int ActualArea;
        public int Amount;
        public int BillableArea;
        public UUID DestID;
        public boolean Final;
        public int Flags;
        public UUID OwnerID;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;
    }

    public static class RegionData {
        public int GridX;
        public int GridY;
        public UUID RegionID;
    }

    public RequestParcelTransfer() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 114;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestParcelTransfer(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -36);
        packUUID(byteBuffer, this.Data_Field.TransactionID);
        packInt(byteBuffer, this.Data_Field.TransactionTime);
        packUUID(byteBuffer, this.Data_Field.SourceID);
        packUUID(byteBuffer, this.Data_Field.DestID);
        packUUID(byteBuffer, this.Data_Field.OwnerID);
        packByte(byteBuffer, (byte) this.Data_Field.Flags);
        packInt(byteBuffer, this.Data_Field.TransactionType);
        packInt(byteBuffer, this.Data_Field.Amount);
        packInt(byteBuffer, this.Data_Field.BillableArea);
        packInt(byteBuffer, this.Data_Field.ActualArea);
        packBoolean(byteBuffer, this.Data_Field.Final);
        packUUID(byteBuffer, this.RegionData_Field.RegionID);
        packInt(byteBuffer, this.RegionData_Field.GridX);
        packInt(byteBuffer, this.RegionData_Field.GridY);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.TransactionID = unpackUUID(byteBuffer);
        this.Data_Field.TransactionTime = unpackInt(byteBuffer);
        this.Data_Field.SourceID = unpackUUID(byteBuffer);
        this.Data_Field.DestID = unpackUUID(byteBuffer);
        this.Data_Field.OwnerID = unpackUUID(byteBuffer);
        this.Data_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.Data_Field.TransactionType = unpackInt(byteBuffer);
        this.Data_Field.Amount = unpackInt(byteBuffer);
        this.Data_Field.BillableArea = unpackInt(byteBuffer);
        this.Data_Field.ActualArea = unpackInt(byteBuffer);
        this.Data_Field.Final = unpackBoolean(byteBuffer);
        this.RegionData_Field.RegionID = unpackUUID(byteBuffer);
        this.RegionData_Field.GridX = unpackInt(byteBuffer);
        this.RegionData_Field.GridY = unpackInt(byteBuffer);
    }
}
