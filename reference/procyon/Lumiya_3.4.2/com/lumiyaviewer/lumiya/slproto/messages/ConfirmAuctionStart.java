// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ConfirmAuctionStart extends SLMessage
{
    public AuctionData AuctionData_Field;
    
    public ConfirmAuctionStart() {
        this.zeroCoded = false;
        this.AuctionData_Field = new AuctionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 24;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleConfirmAuctionStart(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-26));
        this.packUUID(byteBuffer, this.AuctionData_Field.ParcelID);
        this.packInt(byteBuffer, this.AuctionData_Field.AuctionID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AuctionData_Field.ParcelID = this.unpackUUID(byteBuffer);
        this.AuctionData_Field.AuctionID = this.unpackInt(byteBuffer);
    }
    
    public static class AuctionData
    {
        public int AuctionID;
        public UUID ParcelID;
    }
}
