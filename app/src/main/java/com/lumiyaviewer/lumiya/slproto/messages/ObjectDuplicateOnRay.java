package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class ObjectDuplicateOnRay extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public boolean BypassRaycast;
        public boolean CopyCenters;
        public boolean CopyRotates;
        public int DuplicateFlags;
        public UUID GroupID;
        public LLVector3 RayEnd;
        public boolean RayEndIsIntersection;
        public LLVector3 RayStart;
        public UUID RayTargetID;
        public UUID SessionID;
    }

    public static class ObjectData {
        public int ObjectLocalID;
    }

    public ObjectDuplicateOnRay() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 101;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectDuplicateOnRay(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 91);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packLLVector3(byteBuffer, this.AgentData_Field.RayStart);
        packLLVector3(byteBuffer, this.AgentData_Field.RayEnd);
        packBoolean(byteBuffer, this.AgentData_Field.BypassRaycast);
        packBoolean(byteBuffer, this.AgentData_Field.RayEndIsIntersection);
        packBoolean(byteBuffer, this.AgentData_Field.CopyCenters);
        packBoolean(byteBuffer, this.AgentData_Field.CopyRotates);
        packUUID(byteBuffer, this.AgentData_Field.RayTargetID);
        packInt(byteBuffer, this.AgentData_Field.DuplicateFlags);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.AgentData_Field.RayStart = unpackLLVector3(byteBuffer);
        this.AgentData_Field.RayEnd = unpackLLVector3(byteBuffer);
        this.AgentData_Field.BypassRaycast = unpackBoolean(byteBuffer);
        this.AgentData_Field.RayEndIsIntersection = unpackBoolean(byteBuffer);
        this.AgentData_Field.CopyCenters = unpackBoolean(byteBuffer);
        this.AgentData_Field.CopyRotates = unpackBoolean(byteBuffer);
        this.AgentData_Field.RayTargetID = unpackUUID(byteBuffer);
        this.AgentData_Field.DuplicateFlags = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectLocalID = unpackInt(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
