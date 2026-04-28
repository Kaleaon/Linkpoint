package com.linkpoint.linden.llinventory

import com.linkpoint.linden.llcommon.LLUUID

fun interface LLInventoryObserver {
    fun changed(changes: Set<ChangeType>)

    enum class ChangeType(val mask: UInt) {
        NONE(0u),
        LABEL(1u),
        ADD(2u),
        REMOVE(4u),
        STRUCTURE(8u),
        MOVE(16u),
        REBUILD(32u),
        GESTURE(64u),
        CALLING_CARD(128u),
        ALL(0xFFFFFFFFu);

        companion object {
            fun fromMask(mask: UInt): Set<ChangeType> =
                entries.filter { it != NONE && it != ALL && (mask and it.mask) != 0u }.toSet()
        }
    }
}
