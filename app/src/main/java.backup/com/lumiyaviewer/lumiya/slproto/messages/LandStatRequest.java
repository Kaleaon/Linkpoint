package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class LandStatRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public RequestData RequestData_Field = new RequestData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class RequestData {
        public byte[] Filter;
        public int ParcelLocalID;
        public int ReportType;
        public int RequestFlags;
    }

    public LandStatRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.RequestData_Field.Filter.length + 9 + 4 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLandStatRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -91);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.RequestData_Field.ReportType);
        packInt(byteBuffer, this.RequestData_Field.RequestFlags);
        packVariable(byteBuffer, this.RequestData_Field.Filter, 1);
        packInt(byteBuffer, this.RequestData_Field.ParcelLocalID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.RequestData_Field.ReportType = unpackInt(byteBuffer);
        this.RequestData_Field.RequestFlags = unpackInt(byteBuffer);
        this.RequestData_Field.Filter = unpackVariable(byteBuffer, 1);
        this.RequestData_Field.ParcelLocalID = unpackInt(byteBuffer);
    }
}
