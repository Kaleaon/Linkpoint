package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RpcChannelRequest extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public int GridX;
        public int GridY;
        public UUID ItemID;
        public UUID TaskID;
    }

    public RpcChannelRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 44;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcChannelRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -99);
        packInt(byteBuffer, this.DataBlock_Field.GridX);
        packInt(byteBuffer, this.DataBlock_Field.GridY);
        packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        packUUID(byteBuffer, this.DataBlock_Field.ItemID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.GridX = unpackInt(byteBuffer);
        this.DataBlock_Field.GridY = unpackInt(byteBuffer);
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer);
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer);
    }
}
