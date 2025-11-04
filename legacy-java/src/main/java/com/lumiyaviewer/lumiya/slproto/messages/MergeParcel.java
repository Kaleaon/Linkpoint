package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class MergeParcel extends SLMessage {
    public MasterParcelData MasterParcelData_Field;
    public ArrayList<SlaveParcelData> SlaveParcelData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class MasterParcelData {
        public UUID MasterID;
    }

    /* loaded from: classes.dex */
    public static class SlaveParcelData {
        public UUID SlaveID;
    }

    public MergeParcel() {
        this.zeroCoded = false;
        this.MasterParcelData_Field = new MasterParcelData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.SlaveParcelData_Fields.size() * 16) + 21;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMergeParcel(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -33);
        packUUID(byteBuffer, this.MasterParcelData_Field.MasterID);
        byteBuffer.put((byte) this.SlaveParcelData_Fields.size());
        Iterator<T> it = this.SlaveParcelData_Fields.iterator();
        while (it.hasNext()) {
            packUUID(byteBuffer, ((SlaveParcelData) it.next()).SlaveID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MasterParcelData_Field.MasterID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            SlaveParcelData slaveParcelData = new SlaveParcelData();
            slaveParcelData.SlaveID = unpackUUID(byteBuffer);
            this.SlaveParcelData_Fields.add(slaveParcelData);
        }
    }
}
