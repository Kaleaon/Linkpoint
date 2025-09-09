package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ChildAgentPositionUpdate extends SLMessage {
    public AgentData AgentData_Field = new AgentData();

    public static class AgentData {
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

    public ChildAgentPositionUpdate() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 130;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChildAgentPositionUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.ESC);
        packLong(byteBuffer, this.AgentData_Field.RegionHandle);
        packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packLLVector3(byteBuffer, this.AgentData_Field.AgentPos);
        packLLVector3(byteBuffer, this.AgentData_Field.AgentVel);
        packLLVector3(byteBuffer, this.AgentData_Field.Center);
        packLLVector3(byteBuffer, this.AgentData_Field.Size);
        packLLVector3(byteBuffer, this.AgentData_Field.AtAxis);
        packLLVector3(byteBuffer, this.AgentData_Field.LeftAxis);
        packLLVector3(byteBuffer, this.AgentData_Field.UpAxis);
        packBoolean(byteBuffer, this.AgentData_Field.ChangedGrid);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.RegionHandle = unpackLong(byteBuffer);
        this.AgentData_Field.ViewerCircuitCode = unpackInt(byteBuffer);
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.AgentPos = unpackLLVector3(byteBuffer);
        this.AgentData_Field.AgentVel = unpackLLVector3(byteBuffer);
        this.AgentData_Field.Center = unpackLLVector3(byteBuffer);
        this.AgentData_Field.Size = unpackLLVector3(byteBuffer);
        this.AgentData_Field.AtAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.LeftAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.UpAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.ChangedGrid = unpackBoolean(byteBuffer);
    }
}
