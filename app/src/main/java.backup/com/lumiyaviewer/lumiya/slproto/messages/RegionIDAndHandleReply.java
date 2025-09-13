package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RegionIDAndHandleReply extends SLMessage {
    public ReplyBlock ReplyBlock_Field = new ReplyBlock();

    public static class ReplyBlock {
        public long RegionHandle;
        public UUID RegionID;
    }

    public RegionIDAndHandleReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 28;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionIDAndHandleReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 54);
        packUUID(byteBuffer, this.ReplyBlock_Field.RegionID);
        packLong(byteBuffer, this.ReplyBlock_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyBlock_Field.RegionID = unpackUUID(byteBuffer);
        this.ReplyBlock_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
