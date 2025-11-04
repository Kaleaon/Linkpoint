// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    
    public AgentUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 115;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)4);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation);
        this.packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation);
        this.packByte(byteBuffer, (byte)this.AgentData_Field.State);
        this.packLLVector3(byteBuffer, this.AgentData_Field.CameraCenter);
        this.packLLVector3(byteBuffer, this.AgentData_Field.CameraAtAxis);
        this.packLLVector3(byteBuffer, this.AgentData_Field.CameraLeftAxis);
        this.packLLVector3(byteBuffer, this.AgentData_Field.CameraUpAxis);
        this.packFloat(byteBuffer, this.AgentData_Field.Far);
        this.packInt(byteBuffer, this.AgentData_Field.ControlFlags);
        this.packByte(byteBuffer, (byte)this.AgentData_Field.Flags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.BodyRotation = this.unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.HeadRotation = this.unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.State = (this.unpackByte(byteBuffer) & 0xFF);
        this.AgentData_Field.CameraCenter = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.CameraAtAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.CameraLeftAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.CameraUpAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.Far = this.unpackFloat(byteBuffer);
        this.AgentData_Field.ControlFlags = this.unpackInt(byteBuffer);
        this.AgentData_Field.Flags = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class AgentData
    {
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
}
