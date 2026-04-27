// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RpcChannelRequest extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public RpcChannelRequest() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 44;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRpcChannelRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-99));
        this.packInt(byteBuffer, this.DataBlock_Field.GridX);
        this.packInt(byteBuffer, this.DataBlock_Field.GridY);
        this.packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ItemID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.GridX = this.unpackInt(byteBuffer);
        this.DataBlock_Field.GridY = this.unpackInt(byteBuffer);
        this.DataBlock_Field.TaskID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.ItemID = this.unpackUUID(byteBuffer);
    }
    
    public static class DataBlock
    {
        public int GridX;
        public int GridY;
        public UUID ItemID;
        public UUID TaskID;
    }
}
