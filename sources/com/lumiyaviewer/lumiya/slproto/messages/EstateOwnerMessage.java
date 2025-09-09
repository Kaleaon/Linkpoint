package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class EstateOwnerMessage extends SLMessage {
    public AgentData AgentData_Field;
    public MethodData MethodData_Field;
    public ArrayList<ParamList> ParamList_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
        public UUID TransactionID;
    }

    public static class MethodData {
        public UUID Invoice;
        public byte[] Method;
    }

    public static class ParamList {
        public byte[] Parameter;
    }

    public EstateOwnerMessage() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MethodData_Field = new MethodData();
    }

    public int CalcPayloadSize() {
        int length = this.MethodData_Field.Method.length + 1 + 16 + 52 + 1;
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
        sLMessageHandler.HandleEstateOwnerMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 4);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.TransactionID);
        packVariable(byteBuffer, this.MethodData_Field.Method, 1);
        packUUID(byteBuffer, this.MethodData_Field.Invoice);
        byteBuffer.put((byte) this.ParamList_Fields.size());
        for (ParamList paramList : this.ParamList_Fields) {
            packVariable(byteBuffer, paramList.Parameter, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.TransactionID = unpackUUID(byteBuffer);
        this.MethodData_Field.Method = unpackVariable(byteBuffer, 1);
        this.MethodData_Field.Invoice = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ParamList paramList = new ParamList();
            paramList.Parameter = unpackVariable(byteBuffer, 1);
            this.ParamList_Fields.add(paramList);
        }
    }
}
