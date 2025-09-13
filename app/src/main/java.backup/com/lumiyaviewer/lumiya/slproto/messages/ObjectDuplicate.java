package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ObjectDuplicate extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();
    public SharedData SharedData_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public int ObjectLocalID;
    }

    public static class SharedData {
        public int DuplicateFlags;
        public LLVector3 Offset;
    }

    public ObjectDuplicate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.SharedData_Field = new SharedData();
    }

    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 69;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectDuplicate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 90);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packLLVector3(byteBuffer, this.SharedData_Field.Offset);
        packInt(byteBuffer, this.SharedData_Field.DuplicateFlags);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.SharedData_Field.Offset = unpackLLVector3(byteBuffer);
        this.SharedData_Field.DuplicateFlags = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectLocalID = unpackInt(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
