package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class RpcChannelRequest extends SLMessage {
    public DataBlock DataBlock_Field;

    /* loaded from: classes.dex */
    public static class DataBlock {
        public int GridX;
        public int GridY;
        public UUID ItemID;
        public UUID TaskID;
    }

    public RpcChannelRequest() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 44;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcChannelRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -99);
        packInt(byteBuffer, this.DataBlock_Field.GridX);
        packInt(byteBuffer, this.DataBlock_Field.GridY);
        packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        packUUID(byteBuffer, this.DataBlock_Field.ItemID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.GridX = unpackInt(byteBuffer);
        this.DataBlock_Field.GridY = unpackInt(byteBuffer);
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer);
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer);
    }
}
