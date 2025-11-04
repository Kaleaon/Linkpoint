// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ReplyTaskInventory extends SLMessage
{
    public InventoryData InventoryData_Field;
    
    public ReplyTaskInventory() {
        this.zeroCoded = true;
        this.InventoryData_Field = new InventoryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryData_Field.Filename.length + 19 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleReplyTaskInventory(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)34);
        this.packUUID(byteBuffer, this.InventoryData_Field.TaskID);
        this.packShort(byteBuffer, (short)this.InventoryData_Field.Serial);
        this.packVariable(byteBuffer, this.InventoryData_Field.Filename, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.InventoryData_Field.TaskID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.Serial = this.unpackShort(byteBuffer);
        this.InventoryData_Field.Filename = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class InventoryData
    {
        public byte[] Filename;
        public int Serial;
        public UUID TaskID;
    }
}
