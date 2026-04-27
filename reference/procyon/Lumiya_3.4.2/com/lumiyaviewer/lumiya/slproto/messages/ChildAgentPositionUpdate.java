// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ChildAgentPositionUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    
    public ChildAgentPositionUpdate() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 130;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleChildAgentPositionUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)27);
        this.packLong(byteBuffer, this.AgentData_Field.RegionHandle);
        this.packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packLLVector3(byteBuffer, this.AgentData_Field.AgentPos);
        this.packLLVector3(byteBuffer, this.AgentData_Field.AgentVel);
        this.packLLVector3(byteBuffer, this.AgentData_Field.Center);
        this.packLLVector3(byteBuffer, this.AgentData_Field.Size);
        this.packLLVector3(byteBuffer, this.AgentData_Field.AtAxis);
        this.packLLVector3(byteBuffer, this.AgentData_Field.LeftAxis);
        this.packLLVector3(byteBuffer, this.AgentData_Field.UpAxis);
        this.packBoolean(byteBuffer, this.AgentData_Field.ChangedGrid);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.AgentData_Field.ViewerCircuitCode = this.unpackInt(byteBuffer);
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.AgentPos = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.AgentVel = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.Center = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.Size = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.AtAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.LeftAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.UpAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.ChangedGrid = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public LLVector3 AgentPos;
        public LLVector3 AgentVel;
        public LLVector3 AtAxis;
        public LLVector3 Center;
        public boolean ChangedGrid;
        public LLVector3 LeftAxis;
        public long RegionHandle;
        public UUID SessionID;
        public LLVector3 Size;
        public LLVector3 UpAxis;
        public int ViewerCircuitCode;
    }
}
