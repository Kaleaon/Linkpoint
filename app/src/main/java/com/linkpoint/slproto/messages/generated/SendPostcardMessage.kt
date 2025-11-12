package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class SendPostcardMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var assetId: UUID = UUID(0L, 0L)
    var posGlobal: LLVector3d = LLVector3d()
    var to: ByteArray = ByteArray(0)
    var from_: ByteArray = ByteArray(0)
    var name: ByteArray = ByteArray(0)
    var subject: ByteArray = ByteArray(0)
    var msg: ByteArray = ByteArray(0)
    var allowPublish: Boolean = false
    var maturePublish: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, assetId)
        posGlobal.pack(buffer)
        packVariable(buffer, to, 1)
        packVariable(buffer, from_, 1)
        packVariable(buffer, name, 1)
        packVariable(buffer, subject, 1)
        packVariable(buffer, msg, 2)
        packBoolean(buffer, allowPublish)
        packBoolean(buffer, maturePublish)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        assetId = unpackUUID(buffer)
        posGlobal = LLVector3d.unpack(buffer)
        to = unpackVariable(buffer, 1)
        from_ = unpackVariable(buffer, 1)
        name = unpackVariable(buffer, 1)
        subject = unpackVariable(buffer, 1)
        msg = unpackVariable(buffer, 2)
        allowPublish = unpackBoolean(buffer)
        maturePublish = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF019C

    override fun getMessageName(): String = "SendPostcard"
}
