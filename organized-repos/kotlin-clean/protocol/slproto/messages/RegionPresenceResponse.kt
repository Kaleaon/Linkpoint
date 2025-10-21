package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class RegionPresenceResponse : SLMessage() {
    public ArrayList<RegionData> RegionData_Fields = ArrayList<>()

    @JvmStatic
    class RegionData {
        public Inet4Address ExternalRegionIP
        public Inet4Address InternalRegionIP
        public Byte[] Message
        public Long RegionHandle
        public UUID RegionID
        public Int RegionPort
        public Double ValidUntil
    }

    public RegionPresenceResponse() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        Int i = 5
        Iterator<T> it = this.RegionData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((RegionData) it.next()).Message.length + 43 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionPresenceResponse(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 16)
        byteBuffer.put((Byte) this.RegionData_Fields.size())
        for (RegionData regionData : this.RegionData_Fields) {
            packUUID(byteBuffer, regionData.RegionID)
            packLong(byteBuffer, regionData.RegionHandle)
            packIPAddress(byteBuffer, regionData.InternalRegionIP)
            packIPAddress(byteBuffer, regionData.ExternalRegionIP)
            packShort(byteBuffer, (Short) regionData.RegionPort)
            packDouble(byteBuffer, regionData.ValidUntil)
            packVariable(byteBuffer, regionData.Message, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RegionData regionData = RegionData()
            regionData.RegionID = unpackUUID(byteBuffer)
            regionData.RegionHandle = unpackLong(byteBuffer)
            regionData.InternalRegionIP = unpackIPAddress(byteBuffer)
            regionData.ExternalRegionIP = unpackIPAddress(byteBuffer)
            regionData.RegionPort = unpackShort(byteBuffer) & 65535
            regionData.ValidUntil = unpackDouble(byteBuffer)
            regionData.Message = unpackVariable(byteBuffer, 1)
            this.RegionData_Fields.add(regionData)
        }
    }
}
