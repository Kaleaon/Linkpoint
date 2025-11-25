package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LandStatReplyMessage : SLMessage() {
    var reportType: Int = 0
    var requestFlags: Int = 0
    var totalObjectCount: Int = 0
    val reportData: MutableList<ReportDataBlock> = mutableListOf()

    data class ReportDataBlock(
        var taskLocalId: Int = 0,
        var taskId: UUID = UUID(0L, 0L),
        var locationX: Float = 0f,
        var locationY: Float = 0f,
        var locationZ: Float = 0f,
        var score: Float = 0f,
        var taskName: ByteArray = ByteArray(0),
        var ownerName: ByteArray = ByteArray(0)
    )


    override fun packPayload(buffer: ByteBuffer) {
        packInt(buffer, reportType)
        packInt(buffer, requestFlags)
        packInt(buffer, totalObjectCount)
        require(reportData.size <= 0xFF) { "ReportData size exceeds 255 (" + reportData.size + ")" }
        packByte(buffer, reportData.size)
        reportData.forEach { entry ->
            packInt(buffer, entry.taskLocalId)
            packUUID(buffer, entry.taskId)
            packFloat(buffer, entry.locationX)
            packFloat(buffer, entry.locationY)
            packFloat(buffer, entry.locationZ)
            packFloat(buffer, entry.score)
            packVariable(buffer, entry.taskName, 1)
            packVariable(buffer, entry.ownerName, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        reportType = unpackInt(buffer)
        requestFlags = unpackInt(buffer)
        totalObjectCount = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            reportData.clear()
            repeat(count) {
                val entry = ReportDataBlock()
                entry.taskLocalId = unpackInt(buffer)
                entry.taskId = unpackUUID(buffer)
                entry.locationX = unpackFloat(buffer)
                entry.locationY = unpackFloat(buffer)
                entry.locationZ = unpackFloat(buffer)
                entry.score = unpackFloat(buffer)
                entry.taskName = unpackVariable(buffer, 1)
                entry.ownerName = unpackVariable(buffer, 1)
                reportData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF01A6.toInt()

    override fun getMessageName(): String = "LandStatReply"
}
