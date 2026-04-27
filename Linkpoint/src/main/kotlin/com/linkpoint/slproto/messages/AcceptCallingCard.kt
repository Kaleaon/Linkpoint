package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AcceptCallingCard : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<FolderData> FolderData_Fields = ArrayList<>()
    public TransactionBlock TransactionBlock_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class FolderData {
        public UUID FolderID
    }

    @JvmStatic
    class TransactionBlock {
        public UUID TransactionID
    }

    public AcceptCallingCard() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.TransactionBlock_Field = TransactionBlock()
    }

    public fun CalcPayloadSize(): Int {
        return (this.FolderData_Fields.size() * 16) + 53
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAcceptCallingCard(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 46)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.TransactionBlock_Field.TransactionID)
        byteBuffer.put((Byte) this.FolderData_Fields.size())
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.TransactionBlock_Field.TransactionID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val folderData: FolderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            this.FolderData_Fields.add(folderData)
        }
    }
}
