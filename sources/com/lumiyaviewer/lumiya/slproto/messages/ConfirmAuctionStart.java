package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ConfirmAuctionStart extends SLMessage {
    public AuctionData AuctionData_Field = new AuctionData();

    public static class AuctionData {
        public int AuctionID;
        public UUID ParcelID;
    }

    public ConfirmAuctionStart() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 24;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleConfirmAuctionStart(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -26);
        packUUID(byteBuffer, this.AuctionData_Field.ParcelID);
        packInt(byteBuffer, this.AuctionData_Field.AuctionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AuctionData_Field.ParcelID = unpackUUID(byteBuffer);
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer);
    }
}
