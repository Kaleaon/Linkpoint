package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class StartAuction extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ParcelData ParcelData_Field = new ParcelData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class ParcelData {
        public byte[] Name;
        public UUID ParcelID;
        public UUID SnapshotID;
    }

    public StartAuction() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 33 + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartAuction(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -27);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.ParcelData_Field.ParcelID);
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.ParcelData_Field.ParcelID = unpackUUID(byteBuffer);
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer);
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1);
    }
}
