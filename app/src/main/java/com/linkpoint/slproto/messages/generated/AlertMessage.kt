package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AlertMessage : SLMessage() {
    var message: ByteArray = ByteArray(0)
    val alertInfo: MutableList<AlertInfoBlock> = mutableListOf()
    val agentInfo: MutableList<AgentInfoBlock> = mutableListOf()

    data class AlertInfoBlock(
        var message: ByteArray = ByteArray(0),
        var extraParams: ByteArray = ByteArray(0)
    )
    data class AgentInfoBlock(
        var agentId: UUID = UUID(0L, 0L)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, message, 1)
        require(alertInfo.size <= 0xFF) { "AlertInfo size exceeds 255 (" + alertInfo.size + ")" }
        packByte(buffer, alertInfo.size)
        alertInfo.forEach { entry ->
            packVariable(buffer, entry.message, 1)
            packVariable(buffer, entry.extraParams, 1)
        }
        require(agentInfo.size <= 0xFF) { "AgentInfo size exceeds 255 (" + agentInfo.size + ")" }
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
                val entry = AlertInfoBlock()
                entry.message = unpackVariable(buffer, 1)
                entry.extraParams = unpackVariable(buffer, 1)
                alertInfo += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            agentInfo.clear()
            repeat(count) {
                val entry = AgentInfoBlock()
                entry.agentId = unpackUUID(buffer)
                agentInfo += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0086

    override fun getMessageName(): String = "AlertMessage"
}
