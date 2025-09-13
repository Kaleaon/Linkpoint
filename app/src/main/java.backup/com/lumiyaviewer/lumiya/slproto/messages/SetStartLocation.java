package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SetStartLocation extends SLMessage {
    public StartLocationData StartLocationData_Field = new StartLocationData();

    public static class StartLocationData {
        public UUID AgentID;
        public int LocationID;
        public LLVector3 LocationLookAt;
        public LLVector3 LocationPos;
        public long RegionHandle;
        public UUID RegionID;
    }

    public SetStartLocation() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 72;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetStartLocation(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 69);
        packUUID(byteBuffer, this.StartLocationData_Field.AgentID);
        packUUID(byteBuffer, this.StartLocationData_Field.RegionID);
        packInt(byteBuffer, this.StartLocationData_Field.LocationID);
        packLong(byteBuffer, this.StartLocationData_Field.RegionHandle);
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos);
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.StartLocationData_Field.AgentID = unpackUUID(byteBuffer);
        this.StartLocationData_Field.RegionID = unpackUUID(byteBuffer);
        this.StartLocationData_Field.LocationID = unpackInt(byteBuffer);
        this.StartLocationData_Field.RegionHandle = unpackLong(byteBuffer);
        this.StartLocationData_Field.LocationPos = unpackLLVector3(byteBuffer);
        this.StartLocationData_Field.LocationLookAt = unpackLLVector3(byteBuffer);
    }
}
