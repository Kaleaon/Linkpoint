package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RpcScriptRequestInbound extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();
    public TargetBlock TargetBlock_Field = new TargetBlock();

    public static class DataBlock {
        public UUID ChannelID;
        public int IntValue;
        public UUID ItemID;
        public byte[] StringValue;
        public UUID TaskID;
    }

    public static class TargetBlock {
        public int GridX;
        public int GridY;
    }

    public RpcScriptRequestInbound() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 54 + 12;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcScriptRequestInbound(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
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
