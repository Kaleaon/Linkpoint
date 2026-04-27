package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RegionHandshakeReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public RegionInfo RegionInfo_Field = new RegionInfo();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class RegionInfo {
        public int Flags;
    }

    public RegionHandshakeReply() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 40;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandshakeReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -107);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.RegionInfo_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.RegionInfo_Field.Flags = unpackInt(byteBuffer);
    }
}
