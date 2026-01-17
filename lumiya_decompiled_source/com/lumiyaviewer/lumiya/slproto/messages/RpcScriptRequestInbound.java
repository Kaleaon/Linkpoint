package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class RpcScriptRequestInbound extends SLMessage {
    public DataBlock DataBlock_Field;
    public TargetBlock TargetBlock_Field;

    /* loaded from: classes.dex */
    public static class DataBlock {
        public UUID ChannelID;
        public int IntValue;
        public UUID ItemID;
        public byte[] StringValue;
        public UUID TaskID;
    }

    /* loaded from: classes.dex */
    public static class TargetBlock {
        public int GridX;
        public int GridY;
    }

    public RpcScriptRequestInbound() {
        this.zeroCoded = false;
        this.TargetBlock_Field = new TargetBlock();
        this.DataBlock_Field = new DataBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 54 + 12;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcScriptRequestInbound(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -97);
        packInt(byteBuffer, this.TargetBlock_Field.GridX);
        packInt(byteBuffer, this.TargetBlock_Field.GridY);
        packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        packUUID(byteBuffer, this.DataBlock_Field.ItemID);
        packUUID(byteBuffer, this.DataBlock_Field.ChannelID);
        packInt(byteBuffer, this.DataBlock_Field.IntValue);
        packVariable(byteBuffer, this.DataBlock_Field.StringValue, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TargetBlock_Field.GridX = unpackInt(byteBuffer);
        this.TargetBlock_Field.GridY = unpackInt(byteBuffer);
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer);
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer);
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer);
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer);
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2);
    }
}
