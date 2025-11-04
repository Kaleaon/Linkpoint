package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class SendXferPacket extends SLMessage {
    public DataPacket DataPacket_Field;
    public XferID XferID_Field;

    /* loaded from: classes.dex */
    public static class DataPacket {
        public byte[] Data;
    }

    /* loaded from: classes.dex */
    public static class XferID {
        public long ID;
        public int Packet;
    }

    public SendXferPacket() {
        this.zeroCoded = false;
        this.XferID_Field = new XferID();
        this.DataPacket_Field = new DataPacket();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.DataPacket_Field.Data.length + 2 + 13;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSendXferPacket(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.DC2);
        packLong(byteBuffer, this.XferID_Field.ID);
        packInt(byteBuffer, this.XferID_Field.Packet);
        packVariable(byteBuffer, this.DataPacket_Field.Data, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer);
        this.XferID_Field.Packet = unpackInt(byteBuffer);
        this.DataPacket_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
