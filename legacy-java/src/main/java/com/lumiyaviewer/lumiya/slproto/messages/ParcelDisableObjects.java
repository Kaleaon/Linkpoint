package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class ParcelDisableObjects extends SLMessage {
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    public ArrayList<TaskIDs> TaskIDs_Fields = new ArrayList<>();
    public ArrayList<OwnerIDs> OwnerIDs_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class OwnerIDs {
        public UUID OwnerID;
    }

    /* loaded from: classes.dex */
    public static class ParcelData {
        public int LocalID;
        public int ReturnType;
    }

    /* loaded from: classes.dex */
    public static class TaskIDs {
        public UUID TaskID;
    }

    public ParcelDisableObjects() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.TaskIDs_Fields.size() * 16) + 45 + 1 + (this.OwnerIDs_Fields.size() * 16);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelDisableObjects(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -55);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ParcelData_Field.LocalID);
        packInt(byteBuffer, this.ParcelData_Field.ReturnType);
        byteBuffer.put((byte) this.TaskIDs_Fields.size());
        Iterator<T> it = this.TaskIDs_Fields.iterator();
        while (it.hasNext()) {
            packUUID(byteBuffer, ((TaskIDs) it.next()).TaskID);
        }
        byteBuffer.put((byte) this.OwnerIDs_Fields.size());
        Iterator<T> it2 = this.OwnerIDs_Fields.iterator();
        while (it2.hasNext()) {
            packUUID(byteBuffer, ((OwnerIDs) it2.next()).OwnerID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer);
        this.ParcelData_Field.ReturnType = unpackInt(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            TaskIDs taskIDs = new TaskIDs();
            taskIDs.TaskID = unpackUUID(byteBuffer);
            this.TaskIDs_Fields.add(taskIDs);
        }
        int i3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i4 = 0; i4 < i3; i4++) {
            OwnerIDs ownerIDs = new OwnerIDs();
            ownerIDs.OwnerID = unpackUUID(byteBuffer);
            this.OwnerIDs_Fields.add(ownerIDs);
        }
    }
}
