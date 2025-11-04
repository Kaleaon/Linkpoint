// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TransferInventory extends SLMessage
{
    public InfoBlock InfoBlock_Field;
    public ArrayList<InventoryBlock> InventoryBlock_Fields;
    
    public TransferInventory() {
        this.InventoryBlock_Fields = new ArrayList<InventoryBlock>();
        this.zeroCoded = true;
        this.InfoBlock_Field = new InfoBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryBlock_Fields.size() * 17 + 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTransferInventory(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)39);
        this.packUUID(byteBuffer, this.InfoBlock_Field.SourceID);
        this.packUUID(byteBuffer, this.InfoBlock_Field.DestID);
        this.packUUID(byteBuffer, this.InfoBlock_Field.TransactionID);
        byteBuffer.put((byte)this.InventoryBlock_Fields.size());
        for (final InventoryBlock inventoryBlock : this.InventoryBlock_Fields) {
            this.packUUID(byteBuffer, inventoryBlock.InventoryID);
            this.packByte(byteBuffer, (byte)inventoryBlock.Type);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.InfoBlock_Field.SourceID = this.unpackUUID(byteBuffer);
        this.InfoBlock_Field.DestID = this.unpackUUID(byteBuffer);
        this.InfoBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final InventoryBlock e = new InventoryBlock();
            e.InventoryID = this.unpackUUID(byteBuffer);
            e.Type = this.unpackByte(byteBuffer);
            this.InventoryBlock_Fields.add(e);
        }
    }
    
    public static class InfoBlock
    {
        public UUID DestID;
        public UUID SourceID;
        public UUID TransactionID;
    }
    
    public static class InventoryBlock
    {
        public UUID InventoryID;
        public int Type;
    }
}
