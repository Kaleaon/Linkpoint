package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ScriptDialogMessage : SLMessage() {
    var objectId: UUID = UUID(0L, 0L)
    var firstName: ByteArray = ByteArray(0)
    var lastName: ByteArray = ByteArray(0)
    var objectName: ByteArray = ByteArray(0)
    var message: ByteArray = ByteArray(0)
    var chatChannel: Int = 0
    var imageId: UUID = UUID(0L, 0L)
    val buttons: MutableList<ButtonsBlock> = mutableListOf()
    val ownerData: MutableList<OwnerDataBlock> = mutableListOf()

    data class ButtonsBlock(
        var buttonLabel: ByteArray = ByteArray(0)
    )
    data class OwnerDataBlock(
        var ownerId: UUID = UUID(0L, 0L)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, objectId)
        packVariable(buffer, firstName, 1)
        packVariable(buffer, lastName, 1)
        packVariable(buffer, objectName, 1)
        packVariable(buffer, message, 2)
        packInt(buffer, chatChannel)
        packUUID(buffer, imageId)
        require(buttons.size <= 0xFF) { "Buttons size exceeds 255 (" + buttons.size + ")" }
        packByte(buffer, buttons.size)
        buttons.forEach { entry ->
            packVariable(buffer, entry.buttonLabel, 1)
        }
        require(ownerData.size <= 0xFF) { "OwnerData size exceeds 255 (" + ownerData.size + ")" }
        packByte(buffer, ownerData.size)
        ownerData.forEach { entry ->
            packUUID(buffer, entry.ownerId)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        objectId = unpackUUID(buffer)
        firstName = unpackVariable(buffer, 1)
        lastName = unpackVariable(buffer, 1)
        objectName = unpackVariable(buffer, 1)
        message = unpackVariable(buffer, 2)
        chatChannel = unpackInt(buffer)
        imageId = unpackUUID(buffer)
        run {
            val count = unpackByte(buffer)
            buttons.clear()
            repeat(count) {
                val entry = ButtonsBlock()
                entry.buttonLabel = unpackVariable(buffer, 1)
                buttons += entry
            }
        }
        run {
            val count = unpackByte(buffer)
            ownerData.clear()
            repeat(count) {
                val entry = OwnerDataBlock()
                entry.ownerId = unpackUUID(buffer)
                ownerData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00BE.toInt()

    override fun getMessageName(): String = "ScriptDialog"
}
