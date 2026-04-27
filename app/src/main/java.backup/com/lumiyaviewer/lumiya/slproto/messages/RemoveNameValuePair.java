package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class RemoveNameValuePair extends SLMessage {
    public ArrayList<NameValueData> NameValueData_Fields = new ArrayList<>();
    public TaskData TaskData_Field;

    public static class NameValueData {
        public byte[] NVPair;
    }

    public static class TaskData {
        public UUID ID;
    }

    public RemoveNameValuePair() {
        this.zeroCoded = false;
        this.TaskData_Field = new TaskData();
    }

    public int CalcPayloadSize() {
        int i = 21;
        Iterator<T> it = this.NameValueData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((NameValueData) it.next()).NVPair.length + 2 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveNameValuePair(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 74);
        packUUID(byteBuffer, this.TaskData_Field.ID);
        byteBuffer.put((byte) this.NameValueData_Fields.size());
        for (NameValueData nameValueData : this.NameValueData_Fields) {
            packVariable(byteBuffer, nameValueData.NVPair, 2);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TaskData_Field.ID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            NameValueData nameValueData = new NameValueData();
            nameValueData.NVPair = unpackVariable(byteBuffer, 2);
            this.NameValueData_Fields.add(nameValueData);
        }
    }
}
