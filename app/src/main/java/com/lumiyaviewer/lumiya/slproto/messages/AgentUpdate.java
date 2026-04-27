package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentUpdate extends SLMessage {
    public AgentData AgentData_Field = new AgentData();

    public static class AgentData {
        public UUID AgentID;
        public LLQuaternion BodyRotation;
        public LLVector3 CameraAtAxis;
        public LLVector3 CameraCenter;
        public LLVector3 CameraLeftAxis;
        public LLVector3 CameraUpAxis;
        public int ControlFlags;
        public float Far;
        public int Flags;
        public LLQuaternion HeadRotation;
        public UUID SessionID;
        public int State;
    }

    public AgentUpdate() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 115;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 4);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation);
        packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation);
        packByte(byteBuffer, (byte) this.AgentData_Field.State);
        packLLVector3(byteBuffer, this.AgentData_Field.CameraCenter);
        packLLVector3(byteBuffer, this.AgentData_Field.CameraAtAxis);
        packLLVector3(byteBuffer, this.AgentData_Field.CameraLeftAxis);
        packLLVector3(byteBuffer, this.AgentData_Field.CameraUpAxis);
        packFloat(byteBuffer, this.AgentData_Field.Far);
        packInt(byteBuffer, this.AgentData_Field.ControlFlags);
        packByte(byteBuffer, (byte) this.AgentData_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.BodyRotation = unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.HeadRotation = unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.State = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentData_Field.CameraCenter = unpackLLVector3(byteBuffer);
        this.AgentData_Field.CameraAtAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.CameraLeftAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.CameraUpAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.Far = unpackFloat(byteBuffer);
        this.AgentData_Field.ControlFlags = unpackInt(byteBuffer);
        this.AgentData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
