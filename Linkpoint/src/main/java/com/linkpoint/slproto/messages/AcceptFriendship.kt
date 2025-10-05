package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AcceptFriendship : SLMessage() {
    val AgentData_Field = AgentData()
    val TransactionBlock_Field = TransactionBlock()
    val FolderData_Fields = ArrayList<FolderData>()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class TransactionBlock {
        var TransactionID: UUID? = null
    }

    class FolderData {
        var FolderID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (FolderData_Fields.size * 16) + 53

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAcceptFriendship(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(41.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, TransactionBlock_Field.TransactionID)
        buffer.put(FolderData_Fields.size.toByte())
        for (folderData in FolderData_Fields) {
            packUUID(buffer, folderData.FolderID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        TransactionBlock_Field.TransactionID = unpackUUID(buffer)
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val folderData = FolderData()
            folderData.FolderID = unpackUUID(buffer)
            FolderData_Fields.add(folderData)
        }
    }
}