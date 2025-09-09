package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ParcelRename extends SLMessage {
    public ArrayList<ParcelData> ParcelData_Fields = new ArrayList<>();

    public static class ParcelData {
        public byte[] NewName;
        public UUID ParcelID;
    }

    public ParcelRename() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.ParcelData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((ParcelData) it.next()).NewName.length + 17 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelRename(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -110);
        byteBuffer.put((byte) this.ParcelData_Fields.size());
        for (ParcelData parcelData : this.ParcelData_Fields) {
            packUUID(byteBuffer, parcelData.ParcelID);
            packVariable(byteBuffer, parcelData.NewName, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ParcelData parcelData = new ParcelData();
            parcelData.ParcelID = unpackUUID(byteBuffer);
            parcelData.NewName = unpackVariable(byteBuffer, 1);
            this.ParcelData_Fields.add(parcelData);
        }
    }
}
