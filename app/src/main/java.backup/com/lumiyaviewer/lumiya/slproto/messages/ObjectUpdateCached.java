package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ObjectUpdateCached extends SLMessage {
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();
    public RegionData RegionData_Field;

    public static class ObjectData {
        public int CRC;
        public int ID;
        public int UpdateFlags;
    }

    public static class RegionData {
        public long RegionHandle;
        public int TimeDilation;
    }

    public ObjectUpdateCached() {
        this.zeroCoded = false;
        this.RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 12) + 12;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectUpdateCached(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.SO);
        packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        packShort(byteBuffer, (short) this.RegionData_Field.TimeDilation);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID);
            packInt(byteBuffer, objectData.CRC);
            packInt(byteBuffer, objectData.UpdateFlags);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer);
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535;
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ID = unpackInt(byteBuffer);
            objectData.CRC = unpackInt(byteBuffer);
            objectData.UpdateFlags = unpackInt(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
