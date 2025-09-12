package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EstateCovenantReply extends SLMessage {
    public Data Data_Field = new Data();

    public static class Data {
        public UUID CovenantID;
        public int CovenantTimestamp;
        public byte[] EstateName;
        public UUID EstateOwnerID;
    }

    public EstateCovenantReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.EstateName.length + 21 + 16 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEstateCovenantReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -52);
        packUUID(byteBuffer, this.Data_Field.CovenantID);
        packInt(byteBuffer, this.Data_Field.CovenantTimestamp);
        packVariable(byteBuffer, this.Data_Field.EstateName, 1);
        packUUID(byteBuffer, this.Data_Field.EstateOwnerID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.CovenantID = unpackUUID(byteBuffer);
        this.Data_Field.CovenantTimestamp = unpackInt(byteBuffer);
        this.Data_Field.EstateName = unpackVariable(byteBuffer, 1);
        this.Data_Field.EstateOwnerID = unpackUUID(byteBuffer);
    }
}
