package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptRequestInboundForwardMessage : SLMessage() {
    var rpcServerIp: Inet4Address? = null
    var rpcServerPort: Int = 0
    var taskId: UUID = UUID(0L, 0L)
    var itemId: UUID = UUID(0L, 0L)
    var channelId: UUID = UUID(0L, 0L)
    var intValue: Int = 0
    var stringValue: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packIPAddress(buffer, rpcServerIp)
        packUInt16(buffer, rpcServerPort)
        packUUID(buffer, taskId)
        packUUID(buffer, itemId)
        packUUID(buffer, channelId)
        packInt(buffer, intValue)
        packVariable(buffer, stringValue, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        rpcServerIp = unpackIPAddress(buffer)
        rpcServerPort = unpackUInt16(buffer)
        taskId = unpackUUID(buffer)
        itemId = unpackUUID(buffer)
        channelId = unpackUUID(buffer)
        intValue = unpackInt(buffer)
        stringValue = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF01A0

    override fun getMessageName(): String = "RpcScriptRequestInboundForward"
}
