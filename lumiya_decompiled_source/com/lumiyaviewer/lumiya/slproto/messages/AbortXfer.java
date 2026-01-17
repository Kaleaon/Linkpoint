package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class AbortXfer extends SLMessage {
    public XferID XferID_Field;

    /* loaded from: classes.dex */
    public static class XferID {
        public long ID;
        public int Result;
    }

    public AbortXfer() {
        this.zeroCoded = false;
        this.XferID_Field = new XferID();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 16;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAbortXfer(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -99);
        packLong(byteBuffer, this.XferID_Field.ID);
        packInt(byteBuffer, this.XferID_Field.Result);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer);
        this.XferID_Field.Result = unpackInt(byteBuffer);
    }
}
