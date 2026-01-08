package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Object update message
 */
class ObjectUpdateMessage : SLMessage() {
    var regionHandle: Long = 0L
    var timeDilation: Short = 0

    data class ObjectData(
        var id: Int = 0,
        var state: Byte = 0,
        var fullId: UUID = UUID.randomUUID(),
        var crc: Int = 0,
        var pCode: Byte = 0,
        var material: Byte = 0,
        var clickAction: Byte = 0,
        var scale: LLVector3 = LLVector3(),
        var objectData: ByteArray = ByteArray(0),
        var parentId: Int = 0,
        var updateFlags: Int = 0,
        var pathCurve: Byte = 0,
        var profileCurve: Byte = 0,
        var pathBegin: Short = 0,
        var pathEnd: Short = 0,
        var pathScaleX: Byte = 0,
        var pathScaleY: Byte = 0,
        var pathShearX: Byte = 0,
        var pathShearY: Byte = 0,
        var pathTwist: Byte = 0,
        var pathTwistBegin: Byte = 0,
        var pathRadiusOffset: Byte = 0,
        var pathTaperX: Byte = 0,
        var pathTaperY: Byte = 0,
        var pathRevolutions: Byte = 0,
        var pathSkew: Byte = 0,
        var profileBegin: Short = 0,
        var profileEnd: Short = 0,
        var profileHollow: Short = 0,
        var textureEntry: ByteArray = ByteArray(0),
        var textureAnim: ByteArray = ByteArray(0),
        var nameValue: ByteArray = ByteArray(0),
        var data: ByteArray = ByteArray(0),
        var text: String = "",
        var textColor: ByteArray = ByteArray(4),
        var mediaUrl: String = "",
        var psBlock: ByteArray = ByteArray(0),
        var extraParams: ByteArray = ByteArray(0),
        var sound: UUID = UUID.randomUUID(),
        var ownerId: UUID = UUID.randomUUID(),
        var ownerMask: Int = 0,
        var baseMask: Int = 0,
        var everyoneMask: Int = 0,
        var groupMask: Int = 0,
        var nextOwnerMask: Int = 0,
        var groupId: UUID = UUID.randomUUID(),
        var creatorId: UUID = UUID.randomUUID(),
        var lastOwnerId: UUID = UUID.randomUUID(),
        var folderId: UUID = UUID.randomUUID(),
        var fromTaskId: UUID = UUID.randomUUID(),
        var inventorySerial: Int = 0,
        var saleType: Byte = 0,
        var salePrice: Int = 0,
        var name: String = "",
        var description: String = "",
        var touchName: String = "",
        var sitName: String = "",
        var textureIds: List<UUID> = emptyList(),
    )

    val objectList = mutableListOf<ObjectData>()

    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
        packUInt16(buffer, timeDilation.toInt() and 0xFFFF)

        require(objectList.size <= 0xFF) { "Object list too large (${objectList.size})" }
        packByte(buffer, objectList.size)
        for (obj in objectList) {
            packObjectData(buffer, obj)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
        timeDilation = unpackUInt16(buffer).toShort()

        val count = unpackByte(buffer)
        objectList.clear()
        for (i in 0 until count) {
            objectList.add(unpackObjectData(buffer))
        }
    }

    private fun packObjectData(
        buffer: ByteBuffer,
        obj: ObjectData,
    ) {
        packInt(buffer, obj.id)
        packByte(buffer, obj.state.toInt())
        packUUID(buffer, obj.fullId)
        packInt(buffer, obj.crc)
        packByte(buffer, obj.pCode.toInt())
        packByte(buffer, obj.material.toInt())
        packByte(buffer, obj.clickAction.toInt())
        obj.scale.pack(buffer)

        // Object data (variable)
        packVariable(buffer, obj.objectData, 1)

        packInt(buffer, obj.parentId)
        packInt(buffer, obj.updateFlags)

        // Path and profile data
        packByte(buffer, obj.pathCurve.toInt())
        packByte(buffer, obj.profileCurve.toInt())
        packUInt16(buffer, obj.pathBegin.toInt() and 0xFFFF)
        packUInt16(buffer, obj.pathEnd.toInt() and 0xFFFF)
        packByte(buffer, obj.pathScaleX.toInt())
        packByte(buffer, obj.pathScaleY.toInt())
        packByte(buffer, obj.pathShearX.toInt())
        packByte(buffer, obj.pathShearY.toInt())
        packByte(buffer, obj.pathTwist.toInt())
        packByte(buffer, obj.pathTwistBegin.toInt())
        packByte(buffer, obj.pathRadiusOffset.toInt())
        packByte(buffer, obj.pathTaperX.toInt())
        packByte(buffer, obj.pathTaperY.toInt())
        packByte(buffer, obj.pathRevolutions.toInt())
        packByte(buffer, obj.pathSkew.toInt())
        packUInt16(buffer, obj.profileBegin.toInt() and 0xFFFF)
        packUInt16(buffer, obj.profileEnd.toInt() and 0xFFFF)
        packUInt16(buffer, obj.profileHollow.toInt() and 0xFFFF)

        // Texture entry
        packVariable(buffer, obj.textureEntry, 2)

        // Texture animation
        packVariable(buffer, obj.textureAnim, 1)

        // Name value
        packVariable(buffer, obj.nameValue, 2)

        // Data
        packVariable(buffer, obj.data, 2)

        // Text
        packVariable(buffer, obj.text.toByteArray(StandardCharsets.UTF_8), 1)

        // Text color
        buffer.put(obj.textColor)

        // Media URL
        packVariable(buffer, obj.mediaUrl.toByteArray(StandardCharsets.UTF_8), 1)

        // PS block
        packVariable(buffer, obj.psBlock, 1)

        // Extra params
        packVariable(buffer, obj.extraParams, 2)

        // Sound
        packUUID(buffer, obj.sound)

        // Owner and permissions
        packUUID(buffer, obj.ownerId)
        packInt(buffer, obj.ownerMask)
        packInt(buffer, obj.baseMask)
        packInt(buffer, obj.everyoneMask)
        packInt(buffer, obj.groupMask)
        packInt(buffer, obj.nextOwnerMask)

        // Group and creator
        packUUID(buffer, obj.groupId)
        packUUID(buffer, obj.creatorId)
        packUUID(buffer, obj.lastOwnerId)
        packUUID(buffer, obj.folderId)
        packUUID(buffer, obj.fromTaskId)

        packInt(buffer, obj.inventorySerial)
        packByte(buffer, obj.saleType.toInt())
        packInt(buffer, obj.salePrice)

        // Name and description
        packVariable(buffer, obj.name.toByteArray(StandardCharsets.UTF_8), 1)

        packVariable(buffer, obj.description.toByteArray(StandardCharsets.UTF_8), 1)

        packVariable(buffer, obj.touchName.toByteArray(StandardCharsets.UTF_8), 1)

        packVariable(buffer, obj.sitName.toByteArray(StandardCharsets.UTF_8), 1)
    }

    private fun unpackObjectData(buffer: ByteBuffer): ObjectData {
        val obj = ObjectData()

        obj.id = unpackInt(buffer)
        obj.state = unpackByte(buffer).toByte()

        obj.fullId = unpackUUID(buffer)

        obj.crc = unpackInt(buffer)
        obj.pCode = unpackByte(buffer).toByte()
        obj.material = unpackByte(buffer).toByte()
        obj.clickAction = unpackByte(buffer).toByte()
        obj.scale = LLVector3.unpack(buffer)

        // Object data
        obj.objectData = unpackVariable(buffer, 1)

        obj.parentId = unpackInt(buffer)
        obj.updateFlags = unpackInt(buffer)

        // Path and profile
        obj.pathCurve = unpackByte(buffer).toByte()
        obj.profileCurve = unpackByte(buffer).toByte()
        obj.pathBegin = unpackUInt16(buffer).toShort()
        obj.pathEnd = unpackUInt16(buffer).toShort()
        obj.pathScaleX = unpackByte(buffer).toByte()
        obj.pathScaleY = unpackByte(buffer).toByte()
        obj.pathShearX = unpackByte(buffer).toByte()
        obj.pathShearY = unpackByte(buffer).toByte()
        obj.pathTwist = unpackByte(buffer).toByte()
        obj.pathTwistBegin = unpackByte(buffer).toByte()
        obj.pathRadiusOffset = unpackByte(buffer).toByte()
        obj.pathTaperX = unpackByte(buffer).toByte()
        obj.pathTaperY = unpackByte(buffer).toByte()
        obj.pathRevolutions = unpackByte(buffer).toByte()
        obj.pathSkew = unpackByte(buffer).toByte()
        obj.profileBegin = unpackUInt16(buffer).toShort()
        obj.profileEnd = unpackUInt16(buffer).toShort()
        obj.profileHollow = unpackUInt16(buffer).toShort()

        // Texture entry
        obj.textureEntry = unpackVariable(buffer, 2)

        // Texture animation
        obj.textureAnim = unpackVariable(buffer, 1)

        // Name value
        obj.nameValue = unpackVariable(buffer, 2)

        // Data
        obj.data = unpackVariable(buffer, 2)

        // Text
        obj.text = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)

        // Text color
        obj.textColor = ByteArray(4)
        buffer.get(obj.textColor)

        // Media URL
        obj.mediaUrl = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)

        // PS block
        obj.psBlock = unpackVariable(buffer, 1)

        // Extra params
        obj.extraParams = unpackVariable(buffer, 2)

        // Sound
        obj.sound = unpackUUID(buffer)

        // Owner and permissions
        obj.ownerId = unpackUUID(buffer)

        obj.ownerMask = unpackInt(buffer)
        obj.baseMask = unpackInt(buffer)
        obj.everyoneMask = unpackInt(buffer)
        obj.groupMask = unpackInt(buffer)
        obj.nextOwnerMask = unpackInt(buffer)

        // Group and creator
        obj.groupId = unpackUUID(buffer)

        obj.creatorId = unpackUUID(buffer)

        obj.lastOwnerId = unpackUUID(buffer)

        obj.folderId = unpackUUID(buffer)

        obj.fromTaskId = unpackUUID(buffer)

        obj.inventorySerial = unpackInt(buffer)
        obj.saleType = unpackByte(buffer).toByte()
        obj.salePrice = unpackInt(buffer)

        // Name
        obj.name = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)

        // Description
        obj.description = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)

        // Touch name
        obj.touchName = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)

        // Sit name
        obj.sitName = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)

        return obj
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.OBJECT_UPDATE

    override fun getMessageName(): String = "ObjectUpdate"
}

/**
 * Object update compressed message
 */
class ObjectUpdateCompressedMessage : SLMessage() {
    var regionHandle: Long = 0L
    var timeDilation: Short = 0

    data class CompressedObjectData(
        var updateFlags: Int = 0,
        var data: ByteArray = ByteArray(0),
    )

    val objectList = mutableListOf<CompressedObjectData>()

    override fun packPayload(buffer: ByteBuffer) {
        packLong(buffer, regionHandle)
        packUInt16(buffer, timeDilation.toInt() and 0xFFFF)

        require(objectList.size <= 0xFF) { "Compressed object list too large (${objectList.size})" }
        packByte(buffer, objectList.size)
        for (obj in objectList) {
            packInt(buffer, obj.updateFlags)
            packVariable(buffer, obj.data, 2)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
        timeDilation = unpackUInt16(buffer).toShort()

        val count = unpackByte(buffer)
        objectList.clear()
        for (i in 0 until count) {
            val updateFlags = unpackInt(buffer)
            val data = unpackVariable(buffer, 2)
            objectList.add(CompressedObjectData(updateFlags, data))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.OBJECT_UPDATE_COMPRESSED

    override fun getMessageName(): String = "ObjectUpdateCompressed"
}

/**
 * Object update cached message
 */
class ObjectUpdateCachedMessage : SLMessage() {
    var regionHandle: Long = 0L
    var timeDilation: Int = 0

    data class CachedObjectData(
        var id: Int = 0,
        var crc: Int = 0,
        var updateFlags: Int = 0,
    )

    val objectList: MutableList<CachedObjectData> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        require(objectList.size <= 0xFF) { "Too many cached objects (${objectList.size})" }
        packLong(buffer, regionHandle)
        packUInt16(buffer, timeDilation)
        packByte(buffer, objectList.size)
        objectList.forEach { obj ->
            packInt(buffer, obj.id)
            packInt(buffer, obj.crc)
            packInt(buffer, obj.updateFlags)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = unpackLong(buffer)
        timeDilation = unpackUInt16(buffer)
        val count = unpackByte(buffer)
        objectList.clear()
        repeat(count) {
            val obj =
                CachedObjectData(
                    id = unpackInt(buffer),
                    crc = unpackInt(buffer),
                    updateFlags = unpackInt(buffer),
                )
            objectList += obj
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.OBJECT_UPDATE_CACHED

    override fun getMessageName(): String = "ObjectUpdateCached"
}

/**
 * Kill object message
 */
class KillObjectMessage : SLMessage() {
    val objectIds = mutableListOf<Int>()

    override fun packPayload(buffer: ByteBuffer) {
        require(objectIds.size <= 0xFF) { "KillObject id list too large (${objectIds.size})" }
        packByte(buffer, objectIds.size)
        objectIds.forEach { id -> packInt(buffer, id) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        val count = unpackByte(buffer)
        objectIds.clear()
        for (i in 0 until count) {
            objectIds.add(unpackInt(buffer))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.KILL_OBJECT

    override fun getMessageName(): String = "KillObject"
}
