// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RpcScriptRequestInboundForward extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public RpcScriptRequestInboundForward() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 60 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRpcScriptRequestInboundForward(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-96));
        this.packIPAddress(byteBuffer, this.DataBlock_Field.RPCServerIP);
        this.packShort(byteBuffer, (short)this.DataBlock_Field.RPCServerPort);
        this.packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ItemID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ChannelID);
        this.packInt(byteBuffer, this.DataBlock_Field.IntValue);
        this.packVariable(byteBuffer, this.DataBlock_Field.StringValue, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.RPCServerIP = this.unpackIPAddress(byteBuffer);
        this.DataBlock_Field.RPCServerPort = (this.unpackShort(byteBuffer) & 0xFFFF);
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
        public Inet4Address RPCServerIP;
        public int RPCServerPort;
        public byte[] StringValue;
        public UUID TaskID;
    }
}
