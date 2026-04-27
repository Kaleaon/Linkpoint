package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ParcelSelectObjects extends SLMessage {
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    public ArrayList<ReturnIDs> ReturnIDs_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ParcelData {
        public int LocalID;
        public int ReturnType;
    }

    public static class ReturnIDs {
        public UUID ReturnID;
    }

    public ParcelSelectObjects() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize() {
        return (this.ReturnIDs_Fields.size() * 16) + 45;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelSelectObjects(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -54);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ParcelData_Field.LocalID);
        packInt(byteBuffer, this.ParcelData_Field.ReturnType);
        byteBuffer.put((byte) this.ReturnIDs_Fields.size());
        for (ReturnIDs returnIDs : this.ReturnIDs_Fields) {
            packUUID(byteBuffer, returnIDs.ReturnID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer);
        this.ParcelData_Field.ReturnType = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ReturnIDs returnIDs = new ReturnIDs();
            returnIDs.ReturnID = unpackUUID(byteBuffer);
            this.ReturnIDs_Fields.add(returnIDs);
        }
    }
}
