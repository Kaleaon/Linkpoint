package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class ConfirmAuctionStart extends SLMessage {
    public AuctionData AuctionData_Field;

    /* loaded from: classes.dex */
    public static class AuctionData {
        public int AuctionID;
        public UUID ParcelID;
    }

    public ConfirmAuctionStart() {
        this.zeroCoded = false;
        this.AuctionData_Field = new AuctionData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 24;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleConfirmAuctionStart(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -26);
        packUUID(byteBuffer, this.AuctionData_Field.ParcelID);
        packInt(byteBuffer, this.AuctionData_Field.AuctionID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AuctionData_Field.ParcelID = unpackUUID(byteBuffer);
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer);
    }
}
