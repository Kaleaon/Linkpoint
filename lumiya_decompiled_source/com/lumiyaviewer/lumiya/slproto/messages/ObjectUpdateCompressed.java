package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ObjectUpdateCompressed extends SLMessage {
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();
    public RegionData RegionData_Field;

    /* loaded from: classes.dex */
    public static class ObjectData {
        public byte[] Data;
        public int UpdateFlags;
    }

    /* loaded from: classes.dex */
    public static class RegionData {
        public long RegionHandle;
        public int TimeDilation;
    }

    public ObjectUpdateCompressed() {
        this.zeroCoded = false;
        this.RegionData_Field = new RegionData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 12;
        Iterator<T> it = this.ObjectData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((ObjectData) it.next()).Data.length + 6 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectUpdateCompressed(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.CR);
        packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        packShort(byteBuffer, (short) this.RegionData_Field.TimeDilation);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.UpdateFlags);
            packVariable(byteBuffer, objectData.Data, 2);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer);
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535;
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            ObjectData objectData = new ObjectData();
            objectData.UpdateFlags = unpackInt(byteBuffer);
            objectData.Data = unpackVariable(byteBuffer, 2);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
