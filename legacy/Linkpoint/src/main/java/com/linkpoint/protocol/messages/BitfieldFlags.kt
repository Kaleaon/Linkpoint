package com.linkpoint.protocol.messages

object RegionFlags {
    const val ALLOW_DAMAGE: Long = 1L shl 0
    const val ALLOW_LAND_RESELL: Long = 1L shl 1
    const val ALLOW_MORE_RESELL: Long = 1L shl 2
    const val IS_SANDBOX: Long = 1L shl 3
    const val ALLOW_VOICE: Long = 1L shl 28

    fun hasFlag(flags: Long, flag: Long): Boolean = (flags and flag) != 0L
}

object ObjectFlags {
    const val PHYSICS: Int = 0x00000001
    const val CREATE_SELECTED: Int = 0x00000002
    const val ALLOW_INVENTORY_DROP: Int = 0x00000004
    const val PHANTOM: Int = 0x00000010
    const val CAST_SHADOWS: Int = 0x00000020

    fun hasFlag(flags: Int, flag: Int): Boolean = (flags and flag) != 0
}
