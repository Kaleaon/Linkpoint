package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ParcelPropertiesRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ParcelData ParcelData_Field = new ParcelData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ParcelData {
        public float East;
        public float North;
        public int SequenceID;
        public boolean SnapSelection;
        public float South;
        public float West;
    }

    public ParcelPropertiesRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 55;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelPropertiesRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put(Ascii.VT);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ParcelData_Field.SequenceID);
        packFloat(byteBuffer, this.ParcelData_Field.West);
        packFloat(byteBuffer, this.ParcelData_Field.South);
        packFloat(byteBuffer, this.ParcelData_Field.East);
        packFloat(byteBuffer, this.ParcelData_Field.North);
        packBoolean(byteBuffer, this.ParcelData_Field.SnapSelection);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer);
        this.ParcelData_Field.West = unpackFloat(byteBuffer);
        this.ParcelData_Field.South = unpackFloat(byteBuffer);
        this.ParcelData_Field.East = unpackFloat(byteBuffer);
        this.ParcelData_Field.North = unpackFloat(byteBuffer);
        this.ParcelData_Field.SnapSelection = unpackBoolean(byteBuffer);
    }
}
