package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ObjectAddMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var pCode: Int = 0
    var material: Int = 0
    var addFlags: Int = 0
    var pathCurve: Int = 0
    var profileCurve: Int = 0
    var pathBegin: Int = 0
    var pathEnd: Int = 0
    var pathScaleX: Int = 0
    var pathScaleY: Int = 0
    var pathShearX: Int = 0
    var pathShearY: Int = 0
    var pathTwist: Int = 0
    var pathTwistBegin: Int = 0
    var pathRadiusOffset: Int = 0
    var pathTaperX: Int = 0
    var pathTaperY: Int = 0
    var pathRevolutions: Int = 0
    var pathSkew: Int = 0
    var profileBegin: Int = 0
    var profileEnd: Int = 0
    var profileHollow: Int = 0
    var bypassRaycast: Int = 0
    var rayStart: LLVector3 = LLVector3()
    var rayEnd: LLVector3 = LLVector3()
    var rayTargetId: UUID = UUID(0L, 0L)
    var rayEndIsIntersection: Int = 0
    var scale: LLVector3 = LLVector3()
    var rotation: LLQuaternion = LLQuaternion()
    var state: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packByte(buffer, pCode)
        packByte(buffer, material)
        packInt(buffer, addFlags)
        packByte(buffer, pathCurve)
        packByte(buffer, profileCurve)
        packUInt16(buffer, pathBegin)
        packUInt16(buffer, pathEnd)
        packByte(buffer, pathScaleX)
        packByte(buffer, pathScaleY)
        packByte(buffer, pathShearX)
        packByte(buffer, pathShearY)
        packByte(buffer, pathTwist)
        packByte(buffer, pathTwistBegin)
        packByte(buffer, pathRadiusOffset)
        packByte(buffer, pathTaperX)
        packByte(buffer, pathTaperY)
        packByte(buffer, pathRevolutions)
        packByte(buffer, pathSkew)
        packUInt16(buffer, profileBegin)
        packUInt16(buffer, profileEnd)
        packUInt16(buffer, profileHollow)
        packByte(buffer, bypassRaycast)
        rayStart.pack(buffer)
        rayEnd.pack(buffer)
        packUUID(buffer, rayTargetId)
        packByte(buffer, rayEndIsIntersection)
        scale.pack(buffer)
        rotation.pack(buffer)
        packByte(buffer, state)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        pCode = unpackByte(buffer)
        material = unpackByte(buffer)
        addFlags = unpackInt(buffer)
        pathCurve = unpackByte(buffer)
        profileCurve = unpackByte(buffer)
        pathBegin = unpackUInt16(buffer)
        pathEnd = unpackUInt16(buffer)
        pathScaleX = unpackByte(buffer)
        pathScaleY = unpackByte(buffer)
        pathShearX = unpackByte(buffer)
        pathShearY = unpackByte(buffer)
        pathTwist = unpackByte(buffer)
        pathTwistBegin = unpackByte(buffer)
        pathRadiusOffset = unpackByte(buffer)
        pathTaperX = unpackByte(buffer)
        pathTaperY = unpackByte(buffer)
        pathRevolutions = unpackByte(buffer)
        pathSkew = unpackByte(buffer)
        profileBegin = unpackUInt16(buffer)
        profileEnd = unpackUInt16(buffer)
        profileHollow = unpackUInt16(buffer)
        bypassRaycast = unpackByte(buffer)
        rayStart = LLVector3.unpack(buffer)
        rayEnd = LLVector3.unpack(buffer)
        rayTargetId = unpackUUID(buffer)
        rayEndIsIntersection = unpackByte(buffer)
        scale = LLVector3.unpack(buffer)
        rotation = LLQuaternion.unpack(buffer)
        state = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0x0000FF01

    override fun getMessageName(): String = "ObjectAdd"
}
