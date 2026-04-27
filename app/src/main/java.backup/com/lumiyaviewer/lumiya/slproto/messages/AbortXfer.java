package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class AbortXfer extends SLMessage {
    public XferID XferID_Field = new XferID();

    public static class XferID {
        public long ID;
        public int Result;
    }

    public AbortXfer() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 16;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAbortXfer(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -99);
        packLong(byteBuffer, this.XferID_Field.ID);
        packInt(byteBuffer, this.XferID_Field.Result);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer);
        this.XferID_Field.Result = unpackInt(byteBuffer);
    }
}
