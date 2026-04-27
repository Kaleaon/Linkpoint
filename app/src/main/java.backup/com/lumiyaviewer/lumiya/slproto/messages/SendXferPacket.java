package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class SendXferPacket extends SLMessage {
    public DataPacket DataPacket_Field = new DataPacket();
    public XferID XferID_Field = new XferID();

    public static class DataPacket {
        public byte[] Data;
    }

    public static class XferID {
        public long ID;
        public int Packet;
    }

    public SendXferPacket() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DataPacket_Field.Data.length + 2 + 13;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSendXferPacket(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.DC2);
        packLong(byteBuffer, this.XferID_Field.ID);
        packInt(byteBuffer, this.XferID_Field.Packet);
        packVariable(byteBuffer, this.DataPacket_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer);
        this.XferID_Field.Packet = unpackInt(byteBuffer);
        this.DataPacket_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
