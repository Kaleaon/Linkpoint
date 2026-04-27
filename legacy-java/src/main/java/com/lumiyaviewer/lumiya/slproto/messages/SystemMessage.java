package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class SystemMessage extends SLMessage {
    public MethodData MethodData_Field;
    public ArrayList<ParamList> ParamList_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class MethodData {
        public byte[] Digest;
        public UUID Invoice;
        public byte[] Method;
    }

    /* loaded from: classes.dex */
    public static class ParamList {
        public byte[] Parameter;
    }

    public SystemMessage() {
        this.zeroCoded = true;
        this.MethodData_Field = new MethodData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int length = this.MethodData_Field.Method.length + 1 + 16 + 32 + 4 + 1;
        Iterator<T> it = this.ParamList_Fields.iterator();
        while (true) {
            int i = length;
            if (!it.hasNext()) {
                return i;
            }
            length = ((ParamList) it.next()).Parameter.length + 1 + i;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSystemMessage(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -108);
        packVariable(byteBuffer, this.MethodData_Field.Method, 1);
        packUUID(byteBuffer, this.MethodData_Field.Invoice);
        packFixed(byteBuffer, this.MethodData_Field.Digest, 32);
        byteBuffer.put((byte) this.ParamList_Fields.size());
        Iterator<T> it = this.ParamList_Fields.iterator();
        while (it.hasNext()) {
            packVariable(byteBuffer, ((ParamList) it.next()).Parameter, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MethodData_Field.Method = unpackVariable(byteBuffer, 1);
        this.MethodData_Field.Invoice = unpackUUID(byteBuffer);
        this.MethodData_Field.Digest = unpackFixed(byteBuffer, 32);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            ParamList paramList = new ParamList();
            paramList.Parameter = unpackVariable(byteBuffer, 1);
            this.ParamList_Fields.add(paramList);
        }
    }
}
