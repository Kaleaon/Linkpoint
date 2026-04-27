package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SetSimPresenceInDatabase extends SLMessage {
    public SimData SimData_Field = new SimData();

    public static class SimData {
        public int AgentCount;
        public int GridX;
        public int GridY;
        public byte[] HostName;
        public int PID;
        public UUID RegionID;
        public byte[] Status;
        public int TimeToLive;
    }

    public SetSimPresenceInDatabase() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.SimData_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + this.SimData_Field.Status.length + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetSimPresenceInDatabase(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.ETB);
        packUUID(byteBuffer, this.SimData_Field.RegionID);
        packVariable(byteBuffer, this.SimData_Field.HostName, 1);
        packInt(byteBuffer, this.SimData_Field.GridX);
        packInt(byteBuffer, this.SimData_Field.GridY);
        packInt(byteBuffer, this.SimData_Field.PID);
        packInt(byteBuffer, this.SimData_Field.AgentCount);
        packInt(byteBuffer, this.SimData_Field.TimeToLive);
        packVariable(byteBuffer, this.SimData_Field.Status, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.SimData_Field.RegionID = unpackUUID(byteBuffer);
        this.SimData_Field.HostName = unpackVariable(byteBuffer, 1);
        this.SimData_Field.GridX = unpackInt(byteBuffer);
        this.SimData_Field.GridY = unpackInt(byteBuffer);
        this.SimData_Field.PID = unpackInt(byteBuffer);
        this.SimData_Field.AgentCount = unpackInt(byteBuffer);
        this.SimData_Field.TimeToLive = unpackInt(byteBuffer);
        this.SimData_Field.Status = unpackVariable(byteBuffer, 1);
    }
}
