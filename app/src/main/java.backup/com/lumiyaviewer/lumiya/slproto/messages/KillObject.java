package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class KillObject extends SLMessage {
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class ObjectData {
        public int ID;
    }

    public KillObject() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 2;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKillObject(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 16);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ID = unpackInt(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
