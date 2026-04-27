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
        buffer.putLong(regionHandle)
        buffer.putShort(timeDilation)

        buffer.put(objectList.size.toByte())
        for (obj in objectList) {
            packObjectData(buffer, obj)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = buffer.getLong()
        timeDilation = buffer.getShort()

        val count = buffer.get().toInt() and 0xFF
        objectList.clear()
        for (i in 0 until count) {
            objectList.add(unpackObjectData(buffer))
        }
    }

    private fun packObjectData(
        buffer: ByteBuffer,
        obj: ObjectData,
    ) {
        buffer.putInt(obj.id)
        buffer.put(obj.state)
        buffer.putLong(obj.fullId.mostSignificantBits)
        buffer.putLong(obj.fullId.leastSignificantBits)
        buffer.putInt(obj.crc)
        buffer.put(obj.pCode)
        buffer.put(obj.material)
        buffer.put(obj.clickAction)
        obj.scale.pack(buffer)

        // Object data (variable)
        buffer.put(obj.objectData.size.toByte())
        buffer.put(obj.objectData)

        buffer.putInt(obj.parentId)
        buffer.putInt(obj.updateFlags)

        // Path and profile data
        buffer.put(obj.pathCurve)
        buffer.put(obj.profileCurve)
        buffer.putShort(obj.pathBegin)
        buffer.putShort(obj.pathEnd)
        buffer.put(obj.pathScaleX)
        buffer.put(obj.pathScaleY)
        buffer.put(obj.pathShearX)
        buffer.put(obj.pathShearY)
        buffer.put(obj.pathTwist)
        buffer.put(obj.pathTwistBegin)
        buffer.put(obj.pathRadiusOffset)
        buffer.put(obj.pathTaperX)
        buffer.put(obj.pathTaperY)
        buffer.put(obj.pathRevolutions)
        buffer.put(obj.pathSkew)
        buffer.putShort(obj.profileBegin)
        buffer.putShort(obj.profileEnd)
        buffer.putShort(obj.profileHollow)

        // Texture entry
        buffer.putShort(obj.textureEntry.size.toShort())
        buffer.put(obj.textureEntry)

        // Texture animation
        buffer.put(obj.textureAnim.size.toByte())
        buffer.put(obj.textureAnim)

        // Name value
        buffer.putShort(obj.nameValue.size.toShort())
        buffer.put(obj.nameValue)

        // Data
        buffer.putShort(obj.data.size.toShort())
        buffer.put(obj.data)

        // Text
        val textBytes = obj.text.toByteArray(StandardCharsets.UTF_8)
        buffer.put(textBytes.size.toByte())
        buffer.put(textBytes)

        // Text color
        buffer.put(obj.textColor)

        // Media URL
        val mediaBytes = obj.mediaUrl.toByteArray(StandardCharsets.UTF_8)
        buffer.put(mediaBytes.size.toByte())
        buffer.put(mediaBytes)

        // PS block
        buffer.put(obj.psBlock.size.toByte())
        buffer.put(obj.psBlock)

        // Extra params
        buffer.putShort(obj.extraParams.size.toShort())
        buffer.put(obj.extraParams)

        // Sound
        buffer.putLong(obj.sound.mostSignificantBits)
        buffer.putLong(obj.sound.leastSignificantBits)

        // Owner and permissions
        buffer.putLong(obj.ownerId.mostSignificantBits)
        buffer.putLong(obj.ownerId.leastSignificantBits)
        buffer.putInt(obj.ownerMask)
        buffer.putInt(obj.baseMask)
        buffer.putInt(obj.everyoneMask)
        buffer.putInt(obj.groupMask)
        buffer.putInt(obj.nextOwnerMask)

        // Group and creator
        buffer.putLong(obj.groupId.mostSignificantBits)
        buffer.putLong(obj.groupId.leastSignificantBits)
        buffer.putLong(obj.creatorId.mostSignificantBits)
        buffer.putLong(obj.creatorId.leastSignificantBits)
        buffer.putLong(obj.lastOwnerId.mostSignificantBits)
        buffer.putLong(obj.lastOwnerId.leastSignificantBits)
        buffer.putLong(obj.folderId.mostSignificantBits)
        buffer.putLong(obj.folderId.leastSignificantBits)
        buffer.putLong(obj.fromTaskId.mostSignificantBits)
        buffer.putLong(obj.fromTaskId.leastSignificantBits)

        buffer.putInt(obj.inventorySerial)
        buffer.put(obj.saleType)
        buffer.putInt(obj.salePrice)

        // Name and description
        val nameBytes = obj.name.toByteArray(StandardCharsets.UTF_8)
        buffer.put(nameBytes.size.toByte())
        buffer.put(nameBytes)

        val descBytes = obj.description.toByteArray(StandardCharsets.UTF_8)
        buffer.put(descBytes.size.toByte())
        buffer.put(descBytes)

        val touchBytes = obj.touchName.toByteArray(StandardCharsets.UTF_8)
        buffer.put(touchBytes.size.toByte())
        buffer.put(touchBytes)

        val sitBytes = obj.sitName.toByteArray(StandardCharsets.UTF_8)
        buffer.put(sitBytes.size.toByte())
        buffer.put(sitBytes)
    }

    private fun unpackObjectData(buffer: ByteBuffer): ObjectData {
        val obj = ObjectData()

        obj.id = buffer.getInt()
        obj.state = buffer.get()

        val fullIdMsb = buffer.getLong()
        val fullIdLsb = buffer.getLong()
        obj.fullId = UUID(fullIdMsb, fullIdLsb)

        obj.crc = buffer.getInt()
        obj.pCode = buffer.get()
        obj.material = buffer.get()
        obj.clickAction = buffer.get()
        obj.scale = LLVector3.unpack(buffer)

        // Object data
        val objectDataLength = buffer.get().toInt() and 0xFF
        obj.objectData = ByteArray(objectDataLength)
        buffer.get(obj.objectData)

        obj.parentId = buffer.getInt()
        obj.updateFlags = buffer.getInt()

        // Path and profile
        obj.pathCurve = buffer.get()
        obj.profileCurve = buffer.get()
        obj.pathBegin = buffer.getShort()
        obj.pathEnd = buffer.getShort()
        obj.pathScaleX = buffer.get()
        obj.pathScaleY = buffer.get()
        obj.pathShearX = buffer.get()
        obj.pathShearY = buffer.get()
        obj.pathTwist = buffer.get()
        obj.pathTwistBegin = buffer.get()
        obj.pathRadiusOffset = buffer.get()
        obj.pathTaperX = buffer.get()
        obj.pathTaperY = buffer.get()
        obj.pathRevolutions = buffer.get()
        obj.pathSkew = buffer.get()
        obj.profileBegin = buffer.getShort()
        obj.profileEnd = buffer.getShort()
        obj.profileHollow = buffer.getShort()

        // Texture entry
        val textureLength = buffer.getShort().toInt() and 0xFFFF
        obj.textureEntry = ByteArray(textureLength)
        buffer.get(obj.textureEntry)

        // Texture animation
        val texAnimLength = buffer.get().toInt() and 0xFF
        obj.textureAnim = ByteArray(texAnimLength)
        buffer.get(obj.textureAnim)

        // Name value
        val nvLength = buffer.getShort().toInt() and 0xFFFF
        obj.nameValue = ByteArray(nvLength)
        buffer.get(obj.nameValue)

        // Data
        val dataLength = buffer.getShort().toInt() and 0xFFFF
        obj.data = ByteArray(dataLength)
        buffer.get(obj.data)

        // Text
        val textLength = buffer.get().toInt() and 0xFF
        val textBytes = ByteArray(textLength)
        buffer.get(textBytes)
        obj.text = String(textBytes, StandardCharsets.UTF_8)

        // Text color
        obj.textColor = ByteArray(4)
        buffer.get(obj.textColor)

        // Media URL
        val mediaLength = buffer.get().toInt() and 0xFF
        val mediaBytes = ByteArray(mediaLength)
        buffer.get(mediaBytes)
        obj.mediaUrl = String(mediaBytes, StandardCharsets.UTF_8)

        // PS block
        val psLength = buffer.get().toInt() and 0xFF
        obj.psBlock = ByteArray(psLength)
        buffer.get(obj.psBlock)

        // Extra params
        val extraLength = buffer.getShort().toInt() and 0xFFFF
        obj.extraParams = ByteArray(extraLength)
        buffer.get(obj.extraParams)

        // Sound
        val soundMsb = buffer.getLong()
        val soundLsb = buffer.getLong()
        obj.sound = UUID(soundMsb, soundLsb)

        // Owner and permissions
        val ownerMsb = buffer.getLong()
        val ownerLsb = buffer.getLong()
        obj.ownerId = UUID(ownerMsb, ownerLsb)

        obj.ownerMask = buffer.getInt()
        obj.baseMask = buffer.getInt()
        obj.everyoneMask = buffer.getInt()
        obj.groupMask = buffer.getInt()
        obj.nextOwnerMask = buffer.getInt()

        // Group and creator
        val groupMsb = buffer.getLong()
        val groupLsb = buffer.getLong()
        obj.groupId = UUID(groupMsb, groupLsb)

        val creatorMsb = buffer.getLong()
        val creatorLsb = buffer.getLong()
        obj.creatorId = UUID(creatorMsb, creatorLsb)

        val lastOwnerMsb = buffer.getLong()
        val lastOwnerLsb = buffer.getLong()
        obj.lastOwnerId = UUID(lastOwnerMsb, lastOwnerLsb)

        val folderMsb = buffer.getLong()
        val folderLsb = buffer.getLong()
        obj.folderId = UUID(folderMsb, folderLsb)

        val taskMsb = buffer.getLong()
        val taskLsb = buffer.getLong()
        obj.fromTaskId = UUID(taskMsb, taskLsb)

        obj.inventorySerial = buffer.getInt()
        obj.saleType = buffer.get()
        obj.salePrice = buffer.getInt()

        // Name
        val nameLength = buffer.get().toInt() and 0xFF
        val nameBytes = ByteArray(nameLength)
        buffer.get(nameBytes)
        obj.name = String(nameBytes, StandardCharsets.UTF_8)

        // Description
        val descLength = buffer.get().toInt() and 0xFF
        val descBytes = ByteArray(descLength)
        buffer.get(descBytes)
        obj.description = String(descBytes, StandardCharsets.UTF_8)

        // Touch name
        val touchLength = buffer.get().toInt() and 0xFF
        val touchBytes = ByteArray(touchLength)
        buffer.get(touchBytes)
        obj.touchName = String(touchBytes, StandardCharsets.UTF_8)

        // Sit name
        val sitLength = buffer.get().toInt() and 0xFF
        val sitBytes = ByteArray(sitLength)
        buffer.get(sitBytes)
        obj.sitName = String(sitBytes, StandardCharsets.UTF_8)

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
        buffer.putLong(regionHandle)
        buffer.putShort(timeDilation)

        buffer.put(objectList.size.toByte())
        for (obj in objectList) {
            buffer.putInt(obj.updateFlags)
            buffer.putShort(obj.data.size.toShort())
            buffer.put(obj.data)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        regionHandle = buffer.getLong()
        timeDilation = buffer.getShort()

        val count = buffer.get().toInt() and 0xFF
        objectList.clear()
        for (i in 0 until count) {
            val updateFlags = buffer.getInt()
            val dataLength = buffer.getShort().toInt() and 0xFFFF
            val data = ByteArray(dataLength)
            buffer.get(data)
            objectList.add(CompressedObjectData(updateFlags, data))
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.OBJECT_UPDATE_COMPRESSED

    override fun getMessageName(): String = "ObjectUpdateCompressed"
}

/**
 * Kill object message
 */
class KillObjectMessage : SLMessage() {
    val objectIds = mutableListOf<Int>()

    override fun packPayload(buffer: ByteBuffer) {
        buffer.put(objectIds.size.toByte())
        for (id in objectIds) {
            buffer.putInt(id)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and 0xFF
        objectIds.clear()
        for (i in 0 until count) {
            objectIds.add(buffer.getInt())
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.KILL_OBJECT

    override fun getMessageName(): String = "KillObject"
}
