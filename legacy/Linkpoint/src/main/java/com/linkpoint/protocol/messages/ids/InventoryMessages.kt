package com.linkpoint.protocol.messages.ids

object InventoryMessages {
    const val MOVE_INVENTORY_ITEM = (0xFFFF010C).toInt()
    const val COPY_INVENTORY_ITEM = (0xFFFF010D).toInt()
    const val UPDATE_INVENTORY_ITEM = (0xFFFF010A).toInt()
    const val CREATE_INVENTORY_FOLDER = (0xFFFF0111).toInt()
    const val REZ_SINGLE_ATTACHMENT_FROM_INV = (0xFFFF018B).toInt()
    const val FETCH_INVENTORY_DESCENDENTS = (0xFFFF0115).toInt()
    const val FETCH_INVENTORY = (0xFFFF0117).toInt()
    const val INVENTORY_DESCENDENTS = (0xFFFF0116).toInt()
}
