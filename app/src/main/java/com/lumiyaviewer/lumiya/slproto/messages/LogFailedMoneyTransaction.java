package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

public class LogFailedMoneyTransaction extends SLMessage {
    public TransactionData TransactionData_Field = new TransactionData();

    public static class TransactionData {
        public int Amount;
        public UUID DestID;
        public int FailureType;
        public int Flags;
        public int GridX;
        public int GridY;
        public Inet4Address SimulatorIP;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;
    }

    public LogFailedMoneyTransaction() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 78;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogFailedMoneyTransaction(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.DC4);
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        packInt(byteBuffer, this.TransactionData_Field.TransactionTime);
        packInt(byteBuffer, this.TransactionData_Field.TransactionType);
        packUUID(byteBuffer, this.TransactionData_Field.SourceID);
        packUUID(byteBuffer, this.TransactionData_Field.DestID);
        packByte(byteBuffer, (byte) this.TransactionData_Field.Flags);
        packInt(byteBuffer, this.TransactionData_Field.Amount);
        packIPAddress(byteBuffer, this.TransactionData_Field.SimulatorIP);
        packInt(byteBuffer, this.TransactionData_Field.GridX);
        packInt(byteBuffer, this.TransactionData_Field.GridY);
        packByte(byteBuffer, (byte) this.TransactionData_Field.FailureType);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer);
        this.TransactionData_Field.TransactionTime = unpackInt(byteBuffer);
        this.TransactionData_Field.TransactionType = unpackInt(byteBuffer);
        this.TransactionData_Field.SourceID = unpackUUID(byteBuffer);
        this.TransactionData_Field.DestID = unpackUUID(byteBuffer);
        this.TransactionData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.TransactionData_Field.Amount = unpackInt(byteBuffer);
        this.TransactionData_Field.SimulatorIP = unpackIPAddress(byteBuffer);
        this.TransactionData_Field.GridX = unpackInt(byteBuffer);
        this.TransactionData_Field.GridY = unpackInt(byteBuffer);
        this.TransactionData_Field.FailureType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
