package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CrossedRegion extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Info Info_Field = new Info();
    public RegionData RegionData_Field = new RegionData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Info {
        public LLVector3 LookAt;
        public LLVector3 Position;
    }

    public static class RegionData {
        public long RegionHandle;
        public byte[] SeedCapability;
        public Inet4Address SimIP;
        public int SimPort;
    }

    public CrossedRegion() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.RegionData_Field.SeedCapability.length + 16 + 34 + 24;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCrossedRegion(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 7);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packIPAddress(byteBuffer, this.RegionData_Field.SimIP);
        packShort(byteBuffer, (short) this.RegionData_Field.SimPort);
        packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        packVariable(byteBuffer, this.RegionData_Field.SeedCapability, 2);
        packLLVector3(byteBuffer, this.Info_Field.Position);
        packLLVector3(byteBuffer, this.Info_Field.LookAt);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.RegionData_Field.SimIP = unpackIPAddress(byteBuffer);
        this.RegionData_Field.SimPort = unpackShort(byteBuffer) & 65535;
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer);
        this.RegionData_Field.SeedCapability = unpackVariable(byteBuffer, 2);
        this.Info_Field.Position = unpackLLVector3(byteBuffer);
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer);
    }
}
