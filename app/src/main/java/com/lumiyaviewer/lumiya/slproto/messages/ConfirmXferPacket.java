package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class ConfirmXferPacket extends SLMessage {
    public XferID XferID_Field = new XferID();

    public static class XferID {
        public long ID;
        public int Packet;
    }

    public ConfirmXferPacket() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 13;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleConfirmXferPacket(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 19);
        packLong(byteBuffer, this.XferID_Field.ID);
        packInt(byteBuffer, this.XferID_Field.Packet);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer);
        this.XferID_Field.Packet = unpackInt(byteBuffer);
    }
}
