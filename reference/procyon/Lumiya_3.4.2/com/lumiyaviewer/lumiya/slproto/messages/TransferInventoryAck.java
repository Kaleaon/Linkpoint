// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TransferInventoryAck extends SLMessage
{
    public InfoBlock InfoBlock_Field;
    
    public TransferInventoryAck() {
        this.zeroCoded = true;
        this.InfoBlock_Field = new InfoBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTransferInventoryAck(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)40);
        this.packUUID(byteBuffer, this.InfoBlock_Field.TransactionID);
        this.packUUID(byteBuffer, this.InfoBlock_Field.InventoryID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.InfoBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.InfoBlock_Field.InventoryID = this.unpackUUID(byteBuffer);
    }
    
    public static class InfoBlock
    {
        public UUID InventoryID;
        public UUID TransactionID;
    }
}
