package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class CrossedRegion : SLMessage {
    AgentData AgentData_Field = AgentData()
    Info Info_Field = Info()
    RegionData RegionData_Field = RegionData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Info {
        LLVector3 LookAt
        LLVector3 Position
    }

    class RegionData {
        Long RegionHandle
        byte[] SeedCapability
        Inet4Address SimIP
        Int SimPort
    }

    CrossedRegion() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.RegionData_Field.SeedCapability.length + 16 + 34 + 24
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCrossedRegion(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1)
        byteBuffer.put((byte) 7)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packIPAddress(byteBuffer, this.RegionData_Field.SimIP)
        packShort(byteBuffer, (short) this.RegionData_Field.SimPort)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packVariable(byteBuffer, this.RegionData_Field.SeedCapability, 2)
        packLLVector3(byteBuffer, this.Info_Field.Position)
        packLLVector3(byteBuffer, this.Info_Field.LookAt)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
