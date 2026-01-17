package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class ImagePacket extends SLMessage {
    public ImageData ImageData_Field;
    public ImageID ImageID_Field;

    /* loaded from: classes.dex */
    public static class ImageData {
        public byte[] Data;
    }

    /* loaded from: classes.dex */
    public static class ImageID {
        public UUID ID;
        public int Packet;
    }

    public ImagePacket() {
        this.zeroCoded = false;
        this.ImageID_Field = new ImageID();
        this.ImageData_Field = new ImageData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.ImageData_Field.Data.length + 2 + 19;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImagePacket(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 10);
        packUUID(byteBuffer, this.ImageID_Field.ID);
        packShort(byteBuffer, (short) this.ImageID_Field.Packet);
        packVariable(byteBuffer, this.ImageData_Field.Data, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer);
        this.ImageID_Field.Packet = unpackShort(byteBuffer) & 65535;
        this.ImageData_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
