// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RpcChannelReply extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public RpcChannelReply() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 52;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRpcChannelReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-98));
        this.packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ItemID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ChannelID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.TaskID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.ItemID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.ChannelID = this.unpackUUID(byteBuffer);
    }
    
    public static class DataBlock
    {
        public UUID ChannelID;
        public UUID ItemID;
        public UUID TaskID;
    }
}
