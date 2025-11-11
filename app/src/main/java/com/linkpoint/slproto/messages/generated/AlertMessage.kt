package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AlertMessage : SLMessage() {
    var message: ByteArray = ByteArray(0)
    val alertInfo: MutableList<AlertinfoBlock> = mutableListOf()
    val agentInfo: MutableList<AgentinfoBlock> = mutableListOf()

    data class AlertinfoBlock(
        var message: ByteArray = ByteArray(0),
        var extraParams: ByteArray = ByteArray(0)
    )
    data class AgentinfoBlock(
        var agentId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, message, 1)
        packByte(buffer, alertInfo.size)
        alertInfo.forEach { entry ->
            packVariable(buffer, entry.message, 1)
            packVariable(buffer, entry.extraParams, 1)
        }
        packByte(buffer, agentInfo.size)
        agentInfo.forEach { entry ->
            packUUID(buffer, entry.agentId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        message = unpackVariable(buffer, 1)
        run {
            val count = unpackByte(buffer)
            alertInfo.clear()
            repeat(count) {
                val entry = AlertinfoBlock()
                entry.message = unpackVariable(buffer, 1)
                entry.extraParams = unpackVariable(buffer, 1)
                alertInfo += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            agentInfo.clear()
            repeat(count) {
                val entry = AgentinfoBlock()
                entry.agentId = unpackUUID(buffer)
                agentInfo += entry
            }
        }
    }

    override fun getMessageID(): Int = 0x00000086

    override fun getMessageName(): String = "AlertMessage"
}
