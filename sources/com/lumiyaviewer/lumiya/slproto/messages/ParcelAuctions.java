package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ParcelAuctions extends SLMessage {
    public ArrayList<ParcelData> ParcelData_Fields = new ArrayList<>();

    public static class ParcelData {
        public UUID ParcelID;
        public UUID WinnerID;
    }

    public ParcelAuctions() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.ParcelData_Fields.size() * 32) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelAuctions(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -22);
        byteBuffer.put((byte) this.ParcelData_Fields.size());
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID);
            packUUID(byteBuffer, parcelData.WinnerID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ParcelData parcelData = new ParcelData();
            parcelData.ParcelID = unpackUUID(byteBuffer);
            parcelData.WinnerID = unpackUUID(byteBuffer);
            this.ParcelData_Fields.add(parcelData);
        }
    }
}
