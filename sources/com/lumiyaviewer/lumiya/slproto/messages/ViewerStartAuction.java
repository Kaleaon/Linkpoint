package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ViewerStartAuction extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ParcelData ParcelData_Field = new ParcelData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ParcelData {
        public int LocalID;
        public UUID SnapshotID;
    }

    public ViewerStartAuction() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 56;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerStartAuction(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -28);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ParcelData_Field.LocalID);
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer);
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer);
    }
}
