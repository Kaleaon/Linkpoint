package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class RpcScriptRequestInboundForward extends SLMessage {
    public DataBlock DataBlock_Field;

    /* loaded from: classes.dex */
    public static class DataBlock {
        public UUID ChannelID;
        public int IntValue;
        public UUID ItemID;
        public Inet4Address RPCServerIP;
        public int RPCServerPort;
        public byte[] StringValue;
        public UUID TaskID;
    }

    public RpcScriptRequestInboundForward() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 60 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcScriptRequestInboundForward(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -96);
        packIPAddress(byteBuffer, this.DataBlock_Field.RPCServerIP);
        packShort(byteBuffer, (short) this.DataBlock_Field.RPCServerPort);
        packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        packUUID(byteBuffer, this.DataBlock_Field.ItemID);
        packUUID(byteBuffer, this.DataBlock_Field.ChannelID);
        packInt(byteBuffer, this.DataBlock_Field.IntValue);
        packVariable(byteBuffer, this.DataBlock_Field.StringValue, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.RPCServerIP = unpackIPAddress(byteBuffer);
        this.DataBlock_Field.RPCServerPort = unpackShort(byteBuffer) & 65535;
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer);
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer);
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer);
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer);
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2);
    }
}
