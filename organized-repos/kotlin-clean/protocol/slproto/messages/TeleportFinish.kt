package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class TeleportFinish : SLMessage() {
    public Info Info_Field = Info()

    @JvmStatic
    class Info {
        public UUID AgentID
        public Int LocationID
        public Long RegionHandle
        public ByteArray SeedCapability
        public Int SimAccess
        public Inet4Address SimIP
        public Int SimPort
        public Int TeleportFlags
    }

    public TeleportFinish() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.Info_Field.SeedCapability.length + 36 + 1 + 4 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportFinish(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 69)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packInt(byteBuffer, this.Info_Field.LocationID)
        packIPAddress(byteBuffer, this.Info_Field.SimIP)
        packShort(byteBuffer, (Short) this.Info_Field.SimPort)
        packLong(byteBuffer, this.Info_Field.RegionHandle)
        packVariable(byteBuffer, this.Info_Field.SeedCapability, 2)
        packByte(byteBuffer, (Byte) this.Info_Field.SimAccess)
        packInt(byteBuffer, this.Info_Field.TeleportFlags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.LocationID = unpackInt(byteBuffer)
        this.Info_Field.SimIP = unpackIPAddress(byteBuffer)
        this.Info_Field.SimPort = unpackShort(byteBuffer) & 65535
        this.Info_Field.RegionHandle = unpackLong(byteBuffer)
        this.Info_Field.SeedCapability = unpackVariable(byteBuffer, 2)
        this.Info_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer)
    }
}
