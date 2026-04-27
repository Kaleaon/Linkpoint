package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class ParcelOverlay extends SLMessage {
    public ParcelData ParcelData_Field;

    /* loaded from: classes.dex */
    public static class ParcelData {
        public byte[] Data;
        public int SequenceID;
    }

    public ParcelOverlay() {
        this.zeroCoded = true;
        this.ParcelData_Field = new ParcelData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.ParcelData_Field.Data.length + 6 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelOverlay(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -60);
        packInt(byteBuffer, this.ParcelData_Field.SequenceID);
        packVariable(byteBuffer, this.ParcelData_Field.Data, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer);
        this.ParcelData_Field.Data = unpackVariable(byteBuffer, 2);
    }
}
