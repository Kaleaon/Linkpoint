package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class RemoveNameValuePair extends SLMessage {
    public ArrayList<NameValueData> NameValueData_Fields = new ArrayList<>();
    public TaskData TaskData_Field;

    /* loaded from: classes.dex */
    public static class NameValueData {
        public byte[] NVPair;
    }

    /* loaded from: classes.dex */
    public static class TaskData {
        public UUID ID;
    }

    public RemoveNameValuePair() {
        this.zeroCoded = false;
        this.TaskData_Field = new TaskData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveNameValuePair(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 74);
        packUUID(byteBuffer, this.TaskData_Field.ID);
        byteBuffer.put((byte) this.NameValueData_Fields.size());
        Iterator<T> it = this.NameValueData_Fields.iterator();
        while (it.hasNext()) {
            packVariable(byteBuffer, ((NameValueData) it.next()).NVPair, 2);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TaskData_Field.ID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            NameValueData nameValueData = new NameValueData();
            nameValueData.NVPair = unpackVariable(byteBuffer, 2);
            this.NameValueData_Fields.add(nameValueData);
        }
    }
}
