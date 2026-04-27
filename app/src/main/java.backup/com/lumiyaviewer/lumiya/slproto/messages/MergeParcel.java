package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class MergeParcel extends SLMessage {
    public MasterParcelData MasterParcelData_Field;
    public ArrayList<SlaveParcelData> SlaveParcelData_Fields = new ArrayList<>();

    public static class MasterParcelData {
        public UUID MasterID;
    }

    public static class SlaveParcelData {
        public UUID SlaveID;
    }

    public MergeParcel() {
        this.zeroCoded = false;
        this.MasterParcelData_Field = new MasterParcelData();
    }

    public int CalcPayloadSize() {
        return (this.SlaveParcelData_Fields.size() * 16) + 21;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMergeParcel(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -33);
        packUUID(byteBuffer, this.MasterParcelData_Field.MasterID);
        byteBuffer.put((byte) this.SlaveParcelData_Fields.size());
        for (SlaveParcelData slaveParcelData : this.SlaveParcelData_Fields) {
            packUUID(byteBuffer, slaveParcelData.SlaveID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MasterParcelData_Field.MasterID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            SlaveParcelData slaveParcelData = new SlaveParcelData();
            slaveParcelData.SlaveID = unpackUUID(byteBuffer);
            this.SlaveParcelData_Fields.add(slaveParcelData);
        }
    }
}
