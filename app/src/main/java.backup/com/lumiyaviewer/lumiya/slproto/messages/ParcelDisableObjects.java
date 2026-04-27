package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ParcelDisableObjects extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<OwnerIDs> OwnerIDs_Fields = new ArrayList<>();
    public ParcelData ParcelData_Field;
    public ArrayList<TaskIDs> TaskIDs_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class OwnerIDs {
        public UUID OwnerID;
    }

    public static class ParcelData {
        public int LocalID;
        public int ReturnType;
    }

    public static class TaskIDs {
        public UUID TaskID;
    }

    public ParcelDisableObjects() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize() {
        return (this.TaskIDs_Fields.size() * 16) + 45 + 1 + (this.OwnerIDs_Fields.size() * 16);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelDisableObjects(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -55);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ParcelData_Field.LocalID);
        packInt(byteBuffer, this.ParcelData_Field.ReturnType);
        byteBuffer.put((byte) this.TaskIDs_Fields.size());
        for (TaskIDs taskIDs : this.TaskIDs_Fields) {
            packUUID(byteBuffer, taskIDs.TaskID);
        }
        byteBuffer.put((byte) this.OwnerIDs_Fields.size());
        for (OwnerIDs ownerIDs : this.OwnerIDs_Fields) {
            packUUID(byteBuffer, ownerIDs.OwnerID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer);
        this.ParcelData_Field.ReturnType = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            TaskIDs taskIDs = new TaskIDs();
            taskIDs.TaskID = unpackUUID(byteBuffer);
            this.TaskIDs_Fields.add(taskIDs);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            OwnerIDs ownerIDs = new OwnerIDs();
            ownerIDs.OwnerID = unpackUUID(byteBuffer);
            this.OwnerIDs_Fields.add(ownerIDs);
        }
    }
}
