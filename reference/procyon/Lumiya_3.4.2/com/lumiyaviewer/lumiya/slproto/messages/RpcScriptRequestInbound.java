// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RpcScriptRequestInbound extends SLMessage
{
    public DataBlock DataBlock_Field;
    public TargetBlock TargetBlock_Field;
    
    public RpcScriptRequestInbound() {
        this.zeroCoded = false;
        this.TargetBlock_Field = new TargetBlock();
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 54 + 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRpcScriptRequestInbound(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-97));
        this.packInt(byteBuffer, this.TargetBlock_Field.GridX);
        this.packInt(byteBuffer, this.TargetBlock_Field.GridY);
        this.packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ItemID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ChannelID);
        this.packInt(byteBuffer, this.DataBlock_Field.IntValue);
        this.packVariable(byteBuffer, this.DataBlock_Field.StringValue, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TargetBlock_Field.GridX = this.unpackInt(byteBuffer);
        this.TargetBlock_Field.GridY = this.unpackInt(byteBuffer);
        this.DataBlock_Field.TaskID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.ItemID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.ChannelID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.IntValue = this.unpackInt(byteBuffer);
        this.DataBlock_Field.StringValue = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class DataBlock
    {
        public UUID ChannelID;
        public int IntValue;
        public UUID ItemID;
        public byte[] StringValue;
        public UUID TaskID;
    }
    
    public static class TargetBlock
    {
        public int GridX;
        public int GridY;
    }
}
