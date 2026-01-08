package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorLoadMessage : SLMessage() {
    var timeDilation: Float = 0f
    var agentCount: Int = 0
    var canAcceptAgents: Boolean = false
    val agentList: MutableList<AgentListBlock> = mutableListOf()

    data class AgentListBlock(
        var circuitCode: Int = 0,
        var x: Int = 0,
        var y: Int = 0
    )


    override fun packPayload(buffer: ByteBuffer) {
        packFloat(buffer, timeDilation)
        packInt(buffer, agentCount)
        packBoolean(buffer, canAcceptAgents)
        require(agentList.size <= 0xFF) { "AgentList size exceeds 255 (" + agentList.size + ")" }
        packByte(buffer, agentList.size)
        agentList.forEach { entry ->
            packInt(buffer, entry.circuitCode)
            packByte(buffer, entry.x)
            packByte(buffer, entry.y)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        timeDilation = unpackFloat(buffer)
        agentCount = unpackInt(buffer)
        canAcceptAgents = unpackBoolean(buffer)
        run {
            val count = unpackByte(buffer)
            agentList.clear()
            repeat(count) {
                val entry = AgentListBlock()
                entry.circuitCode = unpackInt(buffer)
                entry.x = unpackByte(buffer)
                entry.y = unpackByte(buffer)
                agentList += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF000C.toInt()

    override fun getMessageName(): String = "SimulatorLoad"
}
