package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class RegionIDAndHandleReply extends SLMessage {
    public ReplyBlock ReplyBlock_Field;

    /* loaded from: classes.dex */
    public static class ReplyBlock {
        public long RegionHandle;
        public UUID RegionID;
    }

    public RegionIDAndHandleReply() {
        this.zeroCoded = false;
        this.ReplyBlock_Field = new ReplyBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 28;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionIDAndHandleReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 54);
        packUUID(byteBuffer, this.ReplyBlock_Field.RegionID);
        packLong(byteBuffer, this.ReplyBlock_Field.RegionHandle);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyBlock_Field.RegionID = unpackUUID(byteBuffer);
        this.ReplyBlock_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
