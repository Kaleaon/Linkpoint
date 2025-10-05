package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class CrossedRegion : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Info Info_Field = Info()
    public RegionData RegionData_Field = RegionData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Info {
        public LLVector3 LookAt
        public LLVector3 Position
    }

    @JvmStatic
    class RegionData {
        public Long RegionHandle
        public Byte[] SeedCapability
        public Inet4Address SimIP
        public Int SimPort
    }

    public CrossedRegion() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.RegionData_Field.SeedCapability.length + 16 + 34 + 24
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCrossedRegion(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 7)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packIPAddress(byteBuffer, this.RegionData_Field.SimIP)
        packShort(byteBuffer, (Short) this.RegionData_Field.SimPort)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packVariable(byteBuffer, this.RegionData_Field.SeedCapability, 2)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RegionData_Field.SimIP = unpackIPAddress(byteBuffer)
        this.RegionData_Field.SimPort = unpackShort(byteBuffer) & 65535
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.SeedCapability = unpackVariable(byteBuffer, 2)
        this.Info_Field.Position = unpackLLVector3(byteBuffer)
        this.Info_Field.LookAt = unpackLLVector3(byteBuffer)
    }
}
