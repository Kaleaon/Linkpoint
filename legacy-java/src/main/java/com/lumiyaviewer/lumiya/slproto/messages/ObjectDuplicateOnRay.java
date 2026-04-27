package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class ObjectDuplicateOnRay extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
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

    /* loaded from: classes.dex */
    public static class ObjectData {
        public int ObjectLocalID;
    }

    public ObjectDuplicateOnRay() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 4) + 101;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectDuplicateOnRay(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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
        Iterator<T> it = this.ObjectData_Fields.iterator();
        while (it.hasNext()) {
            packInt(byteBuffer, ((ObjectData) it.next()).ObjectLocalID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
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
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectLocalID = unpackInt(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
