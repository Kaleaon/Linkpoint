package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AcceptCallingCard : SLMessage {
    val AgentData_Field = AgentData()
    val FolderData_Fields = ArrayList<FolderData>()
    val TransactionBlock_Field = TransactionBlock()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class FolderData {
        var FolderID: UUID? = null
    }

    class TransactionBlock {
        var TransactionID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (this.FolderData_Fields.size * 16) + 53
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAcceptCallingCard(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(46.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.SessionID)
        packUUID(buffer, this.TransactionBlock_Field.TransactionID)
        buffer.put(this.FolderData_Fields.size.toByte())
        for (folderData in this.FolderData_Fields) {
            packUUID(buffer, folderData.FolderID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.SessionID = unpackUUID(buffer)
        this.TransactionBlock_Field.TransactionID = unpackUUID(buffer)
        val count = buffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val folderData = FolderData()
            folderData.FolderID = unpackUUID(buffer)
            this.FolderData_Fields.add(folderData)
        }
    }
}
