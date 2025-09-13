package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class SystemMessage extends SLMessage {
    public MethodData MethodData_Field;
    public ArrayList<ParamList> ParamList_Fields = new ArrayList<>();

    public static class MethodData {
        public byte[] Digest;
        public UUID Invoice;
        public byte[] Method;
    }

    public static class ParamList {
        public byte[] Parameter;
    }

    public SystemMessage() {
        this.zeroCoded = true;
        this.MethodData_Field = new MethodData();
    }

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

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSystemMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -108);
        packVariable(byteBuffer, this.MethodData_Field.Method, 1);
        packUUID(byteBuffer, this.MethodData_Field.Invoice);
        packFixed(byteBuffer, this.MethodData_Field.Digest, 32);
        byteBuffer.put((byte) this.ParamList_Fields.size());
        for (ParamList paramList : this.ParamList_Fields) {
            packVariable(byteBuffer, paramList.Parameter, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MethodData_Field.Method = unpackVariable(byteBuffer, 1);
        this.MethodData_Field.Invoice = unpackUUID(byteBuffer);
        this.MethodData_Field.Digest = unpackFixed(byteBuffer, 32);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ParamList paramList = new ParamList();
            paramList.Parameter = unpackVariable(byteBuffer, 1);
            this.ParamList_Fields.add(paramList);
        }
    }
}
