package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SetStartLocationRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public StartLocationData StartLocationData_Field = new StartLocationData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class StartLocationData {
        public int LocationID;
        public LLVector3 LocationLookAt;
        public LLVector3 LocationPos;
        public byte[] SimName;
    }

    public SetStartLocationRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.StartLocationData_Field.SimName.length + 1 + 4 + 12 + 12 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetStartLocationRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 68);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packVariable(byteBuffer, this.StartLocationData_Field.SimName, 1);
        packInt(byteBuffer, this.StartLocationData_Field.LocationID);
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos);
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.StartLocationData_Field.SimName = unpackVariable(byteBuffer, 1);
        this.StartLocationData_Field.LocationID = unpackInt(byteBuffer);
        this.StartLocationData_Field.LocationPos = unpackLLVector3(byteBuffer);
        this.StartLocationData_Field.LocationLookAt = unpackLLVector3(byteBuffer);
    }
}
